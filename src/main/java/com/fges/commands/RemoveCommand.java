package com.fges.commands;

import com.fges.Command;
import com.fges.model.GroceryManager;
import com.fges.model.CommandOptions;
import com.fges.util.MessageFormatter;

import java.util.List;

/**
 * Commande pour supprimer des articles de la liste de courses.
 */
public class RemoveCommand implements Command {
    @Override
    public String execute(List<String> args, GroceryManager groceryManager, CommandOptions options) throws Exception {
        if (args.size() < 2) {
            throw new IllegalArgumentException("Nom d'article manquant pour la commande 'remove'.");
        }

        String itemName = args.get(1);
        
        // Vérifier que l'article existe
        if (!groceryManager.doesItemExist(itemName)) {
            throw new Exception(MessageFormatter.formatItemNotFound(itemName));
        }
        
        // Si une catégorie spécifique est fournie, vérifier qu'elle correspond à la catégorie de l'article
        String requestedCategory = options.getCategory();
        String itemCategory = groceryManager.getItemCategory(itemName);
        
        if (requestedCategory != null && !requestedCategory.equals(itemCategory)) {
            if (!groceryManager.categoryExists(requestedCategory)) {
                return MessageFormatter.formatCategoryNotFound(requestedCategory);
            }
        }
        
        // Récupérer la quantité avant suppression pour le message
        int quantity = groceryManager.getItemQuantity(itemName);
        
        // Supprimer l'article
        groceryManager.removeItem(itemName);
        
        return MessageFormatter.formatCompleteRemoval(itemName);
    }
} 