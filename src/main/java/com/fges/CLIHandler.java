package com.fges;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.fges.commands.AddCommand;
import com.fges.commands.InfoCommand;
import com.fges.commands.ListCommand;
import com.fges.commands.RemoveCommand;
import com.fges.commands.WebCommand;
import com.fges.model.CommandOptions;
import com.fges.model.GroceryManager;
import com.fges.storage.StorageManager;
import com.fges.storage.StorageManagerFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Gestionnaire en ligne de commande pour l'application de liste de courses.
 * Parse les arguments et délègue l'exécution aux commandes appropriées.
 */
public class CLIHandler {

    /**
     * Point d'entrée principal pour l'exécution des commandes.
     *
     * @param args les arguments de la ligne de commande
     * @return 0 en cas de succès, 1 en cas d'erreur
     * @throws ParseException en cas d'erreur dans l'analyse des arguments
     * @throws IOException en cas d'erreur d'accès aux fichiers
     */
    public static int exec(String[] args) throws ParseException, IOException {
        // Configurer les options de ligne de commande
        Options cliOptions = createOptions();
        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = parser.parse(cliOptions, args);

        // Traiter les arguments positionnels
        List<String> positionalArgs = cmd.getArgList();
        if (positionalArgs.isEmpty()) {
            System.err.println("Commande manquante.");
            return 1;
        }

        // Extract original command
        String commandName = positionalArgs.get(0);
        
        // Prepare CommandOptions
        CommandOptions options = parseCommandOptions(cmd, positionalArgs);
        
        // If category command was used, update positional args and command name
        if ("category".equals(commandName) && positionalArgs.size() > 1) {
            positionalArgs = positionalArgs.subList(2, positionalArgs.size());
            if (positionalArgs.isEmpty()) {
                System.err.println("Commande manquante après la spécification de la catégorie.");
                return 1;
            }
            commandName = positionalArgs.get(0);
        }

        // Check if the command requires a file and if it's provided
        if (!commandRequiresNoFile(commandName) && options.getFileName() == null) {
            System.err.println("L'option --source est requise pour cette commande.");
            return 1;
        }

        // Prepare file if necessary
        if (prepareFile(options, commandRequiresNoFile(commandName)) != 0) {
            return 1;
        }

        // Execute command with appropriate managers
        return executeCommand(commandName, positionalArgs, options);
    }

    /**
     * Parse command line options and build CommandOptions object
     */
    private static CommandOptions parseCommandOptions(CommandLine cmd, List<String> positionalArgs) {
        CommandOptions.Builder optionsBuilder = new CommandOptions.Builder();
        
        // Add source file if present
        if (cmd.hasOption("source")) {
            optionsBuilder.fileName(cmd.getOptionValue("source"));
        }
        
        // Add format if present
        String format = cmd.getOptionValue("format", "json");
        if (!format.equals("json") && !format.equals("csv")) {
            System.err.println("Format non supporté. Utilisez 'json' ou 'csv'.");
            format = "json"; // Default to JSON for invalid formats
        }
        optionsBuilder.format(format);
        
        // Add category if present from --category option
        if (cmd.hasOption("category")) {
            optionsBuilder.category(cmd.getOptionValue("category"));
        }
        
        // Add category if present from "category" command
        String commandName = positionalArgs.get(0);
        if ("category".equals(commandName) && positionalArgs.size() > 1) {
            optionsBuilder.category(positionalArgs.get(1));
        }
        
        return optionsBuilder.build();
    }
    
    /**
     * Prepare the file for operations if needed
     */
    private static int prepareFile(CommandOptions options, boolean noFileRequired) {
        if (noFileRequired || options.getFileName() == null) {
            return 0; // No file preparation needed
        }
        
        String fileName = options.getFileName();
        
        // Normalize filename for JSON format
        if ("json".equalsIgnoreCase(options.getFormat()) && !fileName.toLowerCase().endsWith(".json")) {
            fileName = fileName + ".json";
        }
        
        // Create file if it doesn't exist
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Erreur lors de la création du fichier : " + fileName);
                return 1;
            }
        }
        
        return 0;
    }
    
    /**
     * Execute the command with appropriate managers
     */
    private static int executeCommand(String commandName, List<String> positionalArgs, CommandOptions options) {
        // Get the command implementation
        Optional<Command> command = getCommand(commandName);
        if (command.isEmpty()) {
            System.err.println("Commande inconnue : " + commandName);
            return 1;
        }
        
        // Initialize managers only if required
        StorageManager storageManager = null;
        GroceryManager groceryManager = null;
        
        if (!commandRequiresNoFile(commandName)) {
            try {
                storageManager = StorageManagerFactory.createStorageManager(options.getFormat());
                groceryManager = new GroceryManager(storageManager);
                
                if (options.getFileName() != null) {
                    try {
                        groceryManager.loadGroceryList(options.getFileName());
                    } catch (IOException e) {
                        System.err.println("Attention : Le fichier " + options.getFileName() + 
                                           " est vide ou corrompu. Une nouvelle liste sera créée.");
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de l'initialisation : " + e.getMessage());
                return 1;
            }
        }
        
        // Execute the command
        try {
            String result = command.get().execute(positionalArgs, groceryManager, options);
            System.out.println(result);
            
            // Save if needed
            if (commandRequiresSaving(commandName) && groceryManager != null && options.getFileName() != null) {
                groceryManager.saveGroceryList(options.getFileName());
            }
            
            // Handle web command special case
            if ("web".equals(commandName)) {
                try {
                    Thread.currentThread().join();
                } catch (InterruptedException e) {
                    System.err.println("Serveur web interrompu: " + e.getMessage());
                }
            }
            
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de la commande : " + e.getMessage());
            return 1;
        }
    }

    /**
     * Détermine si une commande ne nécessite pas de fichier.
     *
     * @param commandName le nom de la commande
     * @return true si la commande ne nécessite pas de fichier, false sinon
     */
    private static boolean commandRequiresNoFile(String commandName) {
        return "info".equals(commandName);
    }

    /**
     * Détermine si une commande nécessite de sauvegarder la liste après exécution.
     *
     * @param commandName le nom de la commande
     * @return true si la commande nécessite une sauvegarde, false sinon
     */
    private static boolean commandRequiresSaving(String commandName) {
        return !("info".equals(commandName) || "web".equals(commandName));
    }

    /**
     * Crée les options de ligne de commande.
     *
     * @return les options configurées
     */
    private static Options createOptions() {
        Options cliOptions = new Options();

        // Options existantes
        cliOptions.addOption("s", "source", true, "Fichier contenant la liste de courses");
        cliOptions.addOption("f", "format", true, "Format de fichier (json ou csv)");

        // Option pour la catégorie
        cliOptions.addOption("c", "category", true, "Catégorie de l'article");
        
        return cliOptions;
    }

    /**
     * Obtient la commande appropriée à partir de son nom.
     *
     * @param commandName le nom de la commande
     * @return la commande ou Optional.empty() si non trouvée
     */
    private static Optional<Command> getCommand(String commandName) {
        return switch (commandName.toLowerCase()) {
            case "add" -> Optional.of(new AddCommand());
            case "remove" -> Optional.of(new RemoveCommand());
            case "list" -> Optional.of(new ListCommand());
            case "info" -> Optional.of(new InfoCommand());
            case "web" -> Optional.of(new WebCommand());
            default -> Optional.empty();
        };
    }
}