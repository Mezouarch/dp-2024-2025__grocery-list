package com.fges;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.ArrayList;

/**
 * Gestionnaire de l'interface en ligne de commande.
 * Cette classe gère la lecture et l'interprétation des commandes.
 */
public class CLIHandler {
    
    private static final Map<String, Command> COMMANDS = Map.of(
        "add", new AddCommand(),
        "remove", new RemoveCommand(),
        "list", new ListCommand()
    );

    /**
     * Point d'entrée principal de l'application.
     *
     * @param args les arguments de la ligne de commande
     * @return le code de sortie (0 pour succès, 1 pour erreur)
     */
    public static int main(String[] args) {
        try {
            if (args.length == 0) {
                printUsage();
                return 1;
            }

            // Parse les options
            String fileName = null;
            String format = "json";
            String category = null;
            List<String> positionalArgs = new ArrayList<>();

            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg.startsWith("-")) {
                    switch (arg) {
                        case "-s":
                        case "--source":
                            if (i + 1 < args.length) {
                                fileName = args[++i];
                            } else {
                                System.err.println("Option -s/--source nécessite un nom de fichier.");
                                return 1;
                            }
                            break;
                        case "-f":
                        case "--format":
                            if (i + 1 < args.length) {
                                format = args[++i];
                            } else {
                                System.err.println("Option -f/--format nécessite un format (json/csv).");
                                return 1;
                            }
                            break;
                        default:
                            System.err.println("Option inconnue : " + arg);
                            return 1;
                    }
                } else {
                    positionalArgs.add(arg);
                }
            }

            // Valider les options
            if (fileName == null) {
                System.err.println("L'option -s/--source est requise.");
                return 1;
            }

            InputValidator.validateStorageFormat(format);

            // Exécuter la commande
            return executeCommand(positionalArgs, fileName, format, category);
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            return 1;
        }
    }

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
                command.get().execute(positionalArgs, groceryManager, category);
                return 0;
            } catch (Exception e) {
                System.err.println("Erreur lors de l'exécution de la commande : " + e.getMessage());
                return 1;
            }
        } else {
            System.err.println(MessageFormatter.formatUnknownCommand(commandName));
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
        return Optional.ofNullable(COMMANDS.get(commandName));
    }

    /**
     * Affiche l'aide de l'application.
     */
    private static void printUsage() {
        System.out.println("Utilisation :");
        System.out.println("  java -jar grocery-list.jar -s <fichier> [-f format] <commande> [arguments]");
        System.out.println();
        System.out.println("Options :");
        System.out.println("  -s, --source <fichier>  Fichier source (requis)");
        System.out.println("  -f, --format <format>   Format de stockage (json/csv, défaut: json)");
        System.out.println();
        System.out.println("Commandes :");
        System.out.println("  add <item> <quantity>   Ajouter ou modifier un article");
        System.out.println("  remove <item> [quantity] Supprimer un article ou une quantité");
        System.out.println("  list [category]         Afficher la liste des articles");
        System.out.println();
        System.out.println("Exemples :");
        System.out.println("  java -jar grocery-list.jar -s groceries.json add Milk 2");
        System.out.println("  java -jar grocery-list.jar -s groceries.csv -f csv list");
        System.out.println("  java -jar grocery-list.jar -s groceries.json category Produits laitiers add Milk 2");
    }
}