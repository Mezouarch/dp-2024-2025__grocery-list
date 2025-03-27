package com.fges;

import java.util.List;

/**
 * Commande pour afficher tous les articles de la liste de courses.
 */
public class ListCommand implements Command {

    @Override
    public void execute(List<String> args, GroceryManager groceryManager) {
        List<String> groceryList = groceryManager.getGroceryList();
        
        if (groceryList.isEmpty()) {
            System.out.println("La liste de courses est vide.");
        } else {
            System.out.println("Liste de courses :");
            groceryList.forEach(System.out::println);
        }
    }
}
