package com.fges;

import com.fges.model.CommandOptions;
import com.fges.model.GroceryManager;

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
     * @param options options de commande (fichier, format, catégorie)
     * @return le résultat de l'exécution de la commande
     * @throws Exception si une erreur survient lors de l'exécution
     */
    String execute(List<String> args, GroceryManager groceryManager, CommandOptions options) throws Exception;
    
    /**
     * Méthode de compatibilité pour l'ancienne API avec catégorie
     */
    default String execute(List<String> args, GroceryManager groceryManager, String category) throws Exception {
        return execute(args, groceryManager, 
                new CommandOptions.Builder().category(category).build());
    }

    /**
     * Méthode de compatibilité pour l'ancienne API sans catégorie
     */
    default String execute(List<String> args, GroceryManager groceryManager) throws Exception {
        return execute(args, groceryManager, (String)null);
    }
}
