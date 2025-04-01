package com.fges;

import java.util.List;

/**
 * Interface représentant une commande exécutable de l'application.
 * Chaque commande prend une liste d'arguments et un gestionnaire de liste de courses.
 */
public interface Command {
    /**
     * Exécute la commande avec les arguments fournis.
     *
     * @param args les arguments de la commande
     * @param groceryManager le gestionnaire de liste de courses
     * @param category la catégorie (peut être null)
     * @throws Exception si une erreur survient lors de l'exécution
     */
    void execute(List<String> args, GroceryManager groceryManager, String category) throws Exception;

    /**
     * Méthode de compatibilité pour l'ancienne API
     */
    default void execute(List<String> args, GroceryManager groceryManager) throws Exception {
        execute(args, groceryManager, null);
    }
}
