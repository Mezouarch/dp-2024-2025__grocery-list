package com.fges;

import java.io.File;
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

        // Traiter les arguments positionnels
        List<String> positionalArgs = cmd.getArgList();
        if (positionalArgs.isEmpty()) {
            System.err.println("Commande manquante.");
            return 1;
        }

        String commandName = positionalArgs.get(0); // <== récupérer ici la commande
        
        // Pour toutes les commandes sauf "info", vérifier le fichier source
        String fileName = cmd.getOptionValue("source");
        if (fileName == null && !"info".equals(commandName)) {
            System.err.println("L'option --source est requise pour cette commande.");
            return 1;
        }

        // Pour la commande info, on accepte qu'il n'y ait pas de fichier source
        // mais on continue le flux normal d'exécution
        
        // Traiter le format de fichier
        String format = processFormatOption(cmd);
        if (format == null) {
            return 1; // Format non supporté
        }
        
        // Récupérer la catégorie si elle est spécifiée
        String category = cmd.getOptionValue("category");

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
        cliOptions.addOption("s", "source", true, "Fichier contenant la liste de courses");
        cliOptions.addOption("f", "format", true, "Format de fichier (json ou csv)");

        // Option pour la catégorie
        cliOptions.addOption("c", "category", true, "Catégorie de l'article");
        
        return cliOptions;
    }

    /**
     * Traite l'option de format.
     *
     * @param cmd la ligne de commande parsée
     * @return le format traité ou null si non supporté
     */
    private static String processFormatOption(CommandLine cmd) {
        String format = cmd.getOptionValue("format", "json");
        if (!format.equals("json") && !format.equals("csv")) {
            System.err.println("Format non supporté. Utilisez 'json' ou 'csv'.");
            return null;
        }
        return format;
    }

    /**
     * Exécute la commande appropriée.
     *
     * @param positionalArgs les arguments positionnels
     * @param fileName le nom du fichier de données (peut être null pour la commande "info")
     * @param format le format de stockage (peut être null pour la commande "info")
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

        // Pour les commandes autres que info, on vérifie que le fichier est bien spécifié
        if (fileName == null && !"info".equals(commandName)) {
            System.err.println("Fichier source requis pour cette commande.");
            return 1;
        }

        // Pour les commandes autres que info, on gère le fichier
        if (!"info".equals(commandName) && fileName != null) {
            // Normaliser le nom du fichier pour le format JSON
            if ("json".equalsIgnoreCase(format) && !fileName.toLowerCase().endsWith(".json")) {
                fileName = fileName + ".json";
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

        // Initialiser les gestionnaires de données
        StorageManager storageManager = null;
        GroceryManager groceryManager = null;
        
        // Pour les commandes autres que info, on initialise les gestionnaires
        if (!"info".equals(commandName)) {
            // Créer le gestionnaire de stockage approprié
            storageManager = StorageManagerFactory.createStorageManager(format);
            
            // Gestionnaire de la liste de courses
            groceryManager = new GroceryManager(storageManager);
            
            // Charger la liste existante si le fichier est spécifié
            if (fileName != null) {
                try {
                    groceryManager.loadGroceryList(fileName);
                } catch (IOException e) {
                    // Si le fichier est vide ou corrompu, on continue avec une liste vide
                    System.err.println("Attention : Le fichier " + fileName + " est vide ou corrompu. Une nouvelle liste sera créée.");
                }
            }
        }

        // Exécution de la commande
        Optional<Command> command = getCommand(commandName);
        if (command.isPresent()) {
            try {
                String result = command.get().execute(positionalArgs, groceryManager, category);
                System.out.println(result);
                
                // Sauvegarder la liste après chaque commande (sauf pour info et web)
                if (!"info".equals(commandName) && !"web".equals(commandName) && groceryManager != null && fileName != null) {
                    groceryManager.saveGroceryList(fileName);
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