package com.fges;

public class Main {
    public static void main(String[] args) {
        try {
            // Déléguer l'exécution au CLIHandler
            System.exit(CLIHandler.exec(args));
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            System.exit(1);
        }
    }
}