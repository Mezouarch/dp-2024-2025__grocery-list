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

        String commandName = positionalArgs.get(0);
        
        // Build command options
        CommandOptions.Builder optionsBuilder = new CommandOptions.Builder();
        
        // Add source file if present
        if (cmd.hasOption("source")) {
            optionsBuilder.fileName(cmd.getOptionValue("source"));
        }
        
        // Add format if present
        String format = cmd.getOptionValue("format", "json");
        if (!format.equals("json") && !format.equals("csv")) {
            System.err.println("Format non supporté. Utilisez 'json' ou 'csv'.");
            return 1;
        }
        optionsBuilder.format(format);
        
        // Add category if present
        if (cmd.hasOption("category")) {
            optionsBuilder.category(cmd.getOptionValue("category"));
        }
        
        CommandOptions options = optionsBuilder.build();
        
        // Vérifier les exigences selon la commande
        if (options.getFileName() == null && !"info".equals(commandName)) {
            System.err.println("L'option --source est requise pour cette commande.");
            return 1;
        }

        // Pour les commandes autres que info, on gère le fichier
        if (!"info".equals(commandName) && options.getFileName() != null) {
            String fileName = options.getFileName();
            // Normaliser le nom du fichier pour le format JSON
            if ("json".equalsIgnoreCase(options.getFormat()) && !fileName.toLowerCase().endsWith(".json")) {
                fileName = fileName + ".json";
                // Update the filename in options
                optionsBuilder.fileName(fileName);
                options = optionsBuilder.build();
            }

            // Créer le fichier s'il n'existe pas
            File file = new File(fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    System.err.println("Erreur lors de la création du fichier : " + fileName);
                    return 1;
                }
            }
        }

        // Vérifier si la commande est "category" suivie d'une valeur
        if ("category".equals(commandName) && positionalArgs.size() > 1) {
            optionsBuilder.category(positionalArgs.get(1));
            options = optionsBuilder.build();
            // Reconstruire la liste des arguments en supprimant "category" et sa valeur
            positionalArgs = positionalArgs.subList(2, positionalArgs.size());
            if (positionalArgs.isEmpty()) {
                System.err.println("Commande manquante après la spécification de la catégorie.");
                return 1;
            }
            commandName = positionalArgs.get(0);
        }

        // Initialiser les gestionnaires de données
        StorageManager storageManager = null;
        GroceryManager groceryManager = null;
        
        // Initialiser les gestionnaires si nécessaire
        if (!"info".equals(commandName)) {
            // Créer le gestionnaire de stockage approprié
            storageManager = StorageManagerFactory.createStorageManager(options.getFormat());
            
            // Gestionnaire de la liste de courses
            groceryManager = new GroceryManager(storageManager);
            
            // Charger la liste existante si le fichier est spécifié
            if (options.getFileName() != null) {
                try {
                    groceryManager.loadGroceryList(options.getFileName());
                } catch (IOException e) {
                    // Si le fichier est vide ou corrompu, on continue avec une liste vide
                    System.err.println("Attention : Le fichier " + options.getFileName() + " est vide ou corrompu. Une nouvelle liste sera créée.");
                }
            }
        }

        // Exécution de la commande
        Optional<Command> command = getCommand(commandName);
        if (command.isPresent()) {
            try {
                String result = command.get().execute(positionalArgs, groceryManager, options);
                System.out.println(result);
                
                // Sauvegarder la liste après chaque commande (sauf pour info et web)
                if (!"info".equals(commandName) && !"web".equals(commandName) && groceryManager != null && options.getFileName() != null) {
                    groceryManager.saveGroceryList(options.getFileName());
                }
                
                // Pour la commande web, on maintient le programme en vie
                if ("web".equals(commandName)) {
                    // Attente indéfinie pour maintenir le serveur web en vie
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
        } else {
            System.err.println("Commande inconnue : " + commandName);
            return 1;
        }
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