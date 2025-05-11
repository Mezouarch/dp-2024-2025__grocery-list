package com.fges.commands;

import com.fges.Command;
import com.fges.model.GroceryManager;
import com.fges.model.CommandOptions;
import com.fges.util.MessageFormatter;

import java.util.List;
import java.util.Map;

/**
 * Commande pour afficher la liste des articles.
 */
public class ListCommand implements Command {
    @Override
    public String execute(List<String> args, GroceryManager groceryManager, CommandOptions options) throws Exception {
        StringBuilder result = new StringBuilder();
        String category = options.getCategory();
        
        if (category != null) {
            // Afficher uniquement les articles de la catégorie spécifiée
            List<String> itemsInCategory = groceryManager.getItemsInCategory(category);
            
            if (itemsInCategory.isEmpty()) {
                return MessageFormatter.formatEmptyCategory(category);
            }
            
            result.append(MessageFormatter.formatCategoryHeader(category)).append("\n");
            
            for (String item : itemsInCategory) {
                result.append(item).append("\n");
            }
        } else {
            // Afficher tous les articles groupés par catégorie
            Map<String, List<String>> itemsByCategory = groceryManager.getGroceryListByCategory();
            
            if (itemsByCategory.isEmpty()) {
                return MessageFormatter.formatEmptyList();
            }
            
            for (Map.Entry<String, List<String>> entry : itemsByCategory.entrySet()) {
                String categoryName = entry.getKey();
                List<String> items = entry.getValue();
                
                result.append(MessageFormatter.formatCategoryHeader(categoryName)).append("\n");
                
                for (String item : items) {
                    result.append(item).append("\n");
                }
                
                result.append("\n");
            }
        }
        
        return result.toString();
    }
} 