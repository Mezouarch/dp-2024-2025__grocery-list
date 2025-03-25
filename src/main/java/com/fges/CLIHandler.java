package com.fges;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CLIHandler {

    public static int exec(String[] args) throws ParseException, IOException {
        Options cliOptions = new Options();
        CommandLineParser parser = new DefaultParser();

        // Option pour le fichier source
        cliOptions.addRequiredOption("s", "source", true, "Fichier contenant la liste de courses");

        CommandLine cmd = parser.parse(cliOptions, args);
        String fileName = cmd.getOptionValue("s");

        List<String> positionalArgs = cmd.getArgList();
        if (positionalArgs.isEmpty()) {
            System.err.println("Commande manquante. Utilisez 'help' pour voir les commandes disponibles.");
            return 1;
        }

        String commandName = positionalArgs.get(0);

        // Gestionnaire de la liste de courses
        GroceryManager groceryManager = new GroceryManager();
        groceryManager.loadGroceryList(fileName);

        // Exécution de la commande
        Optional<Command> command = getCommand(commandName);
        if (command.isPresent()) {
            try {
                command.get().execute(positionalArgs, groceryManager);
                groceryManager.saveGroceryList(fileName);
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

    // Obtenir la commande à exécuter
    private static Optional<Command> getCommand(String commandName) {
        return switch (commandName) {
            case "add" -> Optional.of(new AddCommand());
            case "list" -> Optional.of(new ListCommand());
            case "remove" -> Optional.of(new RemoveCommand());
            default -> Optional.empty();
        };
    }
}
