package com.fges;

import org.apache.commons.cli.ParseException;
import java.io.IOException;

/**
 * Point d'entrée principal de l'application de liste de courses.
 */
public class Main {
    /**
     * Méthode principale.
     * 
     * @param args les arguments de la ligne de commande
     */
    public static void main(String[] args) {
        try {
            System.exit(CLIHandler.exec(args));
        } catch (ParseException e) {
            System.err.println("Erreur dans les arguments de la ligne de commande : " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Erreur d'entrée/sortie : " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Erreur inattendue : " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}