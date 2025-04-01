package com.fges;

import java.util.List;
import java.util.Map;

/**
 * Commande pour afficher la liste des articles.
 */
public class ListCommand implements Command {

    @Override
    public String execute(List<String> args, GroceryManager groceryManager, String category) throws Exception {
        StringBuilder result = new StringBuilder();
        
        // Si une catégorie est spécifiée, afficher uniquement les articles de cette catégorie
        if (category != null) {
            if (!groceryManager.categoryExists(category)) {
                return MessageFormatter.formatCategoryNotFound(category);
            }
            result.append(MessageFormatter.formatCategoryHeader(category)).append("\n");
            groceryManager.getItemsInCategory(category).forEach(item -> result.append(item).append("\n"));
            return result.toString();
        }

        // Sinon, afficher tous les articles groupés par catégorie
        Map<String, List<String>> itemsByCategory = groceryManager.getGroceryListByCategory();
        if (itemsByCategory.isEmpty()) {
            return MessageFormatter.formatEmptyList();
        }

        itemsByCategory.forEach((cat, items) -> {
            result.append(MessageFormatter.formatCategoryHeader(cat)).append("\n");
            items.forEach(item -> result.append(item).append("\n"));
            result.append("\n"); // Ligne vide entre les catégories
        });
        
        return result.toString();
    }
}