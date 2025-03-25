package com.fges;

import org.apache.commons.cli.ParseException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            int status = CLIHandler.exec(args);
            System.exit(status);
        } catch (ParseException | IOException ex) {
            System.err.println("Erreur lors de l'exécution: " + ex.getMessage());
            System.exit(1);
        }
    }
}
