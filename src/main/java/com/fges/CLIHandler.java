package com.fges;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
        String fileName = cmd.getOptionValue("source");
        
        // Traiter le format de fichier
        String format = processFormatOption(cmd);
        if (format == null) {
            return 1; // Format non supporté
        }
        
        // Récupérer la catégorie si elle est spécifiée
        String category = cmd.getOptionValue("category");

        // Traiter les arguments positionnels
        List<String> positionalArgs = cmd.getArgList();
        if (positionalArgs.isEmpty()) {
            System.err.println("Commande manquante. Utilisez 'help' pour voir les commandes disponibles.");
            return 1;
        }

        // Exécuter la commande
        return executeCommand(positionalArgs, fileName, format, category);
    }

    /**
     * Crée les options de ligne de commande.
     *
     * @return les options configurées
     */
    private static Options createOptions() {
        Options cliOptions = new Options();
        
        // Options existantes
        cliOptions.addRequiredOption("s", "source", true, "Fichier contenant la liste de courses");
        cliOptions.addOption("f", "format", true, "Format de fichier (json ou csv)");
        
        // Nouvelle option pour la catégorie
        cliOptions.addOption("c", "category", true, "Catégorie de l'article");
        
        return cliOptions;
    }

    /**
     * Traite l'option de format et vérifie sa validité.
     *
     * @param cmd la ligne de commande parsée
     * @return le format validé, ou null si le format est invalide
     */
    private static String processFormatOption(CommandLine cmd) {
        // Déterminer le format (json par défaut)
        String format = cmd.hasOption("format") ? cmd.getOptionValue("format") : "json";
        
        // Vérifier que le format est supporté
        if (!format.equalsIgnoreCase("json") && !format.equalsIgnoreCase("csv")) {
            System.err.println("Format non supporté : " + format + ". Utilisez 'json' ou 'csv'.");
            return null;
        }
        
        return format;
    }

    /**
     * Exécute la commande demandée avec le gestionnaire de liste approprié.
     *
     * @param positionalArgs les arguments positionnels
     * @param fileName le nom du fichier de données
     * @param format le format de stockage
     * @param category la catégorie de l'article (peut être null)
     * @return 0 en cas de succès, 1 en cas d'erreur
     * @throws IOException en cas d'erreur d'accès aux fichiers
     */
    private static int executeCommand(List<String> positionalArgs, String fileName, String format, String category) 
            throws IOException {
        String commandName = positionalArgs.get(0);
        
        // Vérifier si la commande est "category" suivie d'une valeur
        if ("category".equals(commandName) && positionalArgs.size() > 1) {
            category = positionalArgs.get(1);
            // Reconstruire la liste des arguments en supprimant "category" et sa valeur
            positionalArgs = positionalArgs.subList(2, positionalArgs.size());
            if (positionalArgs.isEmpty()) {
                System.err.println("Commande manquante après la spécification de la catégorie.");
                return 1;
            }
            commandName = positionalArgs.get(0);
        }
    
        // Créer le gestionnaire de stockage approprié
        StorageManager storageManager = StorageManagerFactory.createStorageManager(format);
        
        // Gestionnaire de la liste de courses
        GroceryManager groceryManager = new GroceryManager(storageManager);
        groceryManager.loadGroceryList(fileName);
    
        // Exécution de la commande
        Optional<Command> command = getCommand(commandName);
        if (command.isPresent()) {
            try {
                // Passer la catégorie à la commande
                command.get().execute(positionalArgs, groceryManager, category);
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
     * Obtient l'instance de commande correspondant au nom fourni.
     *
     * @param commandName le nom de la commande
     * @return une Optional contenant la commande si elle existe
     */
    private static Optional<Command> getCommand(String commandName) {
        return switch (commandName) {
            case "add" -> Optional.of(new AddCommand());
            case "list" -> Optional.of(new ListCommand());
            case "remove" -> Optional.of(new RemoveCommand());
            default -> Optional.empty();
        };
    }
}