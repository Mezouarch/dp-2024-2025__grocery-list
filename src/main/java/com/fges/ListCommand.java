package com.fges;

import java.util.List;
import java.util.Map;

/**
 * Commande pour afficher tous les articles de la liste de courses.
 */
public class ListCommand implements Command {

    @Override
    public void execute(List<String> args, GroceryManager groceryManager, String category) {
        Map<String, List<String>> groceryListByCategory = groceryManager.getGroceryListByCategory();
        
        if (groceryListByCategory.isEmpty()) {
            System.out.println("La liste de courses est vide.");
        } else {
            // Add the header "Liste de courses"
            System.out.println("Liste de courses:");
            
            // Si une catégorie est spécifiée, afficher uniquement cette catégorie
            if (category != null && !category.isEmpty()) {
                List<String> items = groceryListByCategory.get(category);
                if (items != null && !items.isEmpty()) {
                    System.out.println("# " + category + ":");
                    items.forEach(item -> System.out.println(item));
                } else {
                    System.out.println("Aucun article dans la catégorie: " + category);
                }
            } else {
                // Afficher toutes les catégories
                for (Map.Entry<String, List<String>> entry : groceryListByCategory.entrySet()) {
                    String categoryName = entry.getKey();
                    List<String> items = entry.getValue();
                    
                    System.out.println("# " + categoryName + ":");
                    items.forEach(item -> System.out.println(item));
                }
            }
        }
    }
}