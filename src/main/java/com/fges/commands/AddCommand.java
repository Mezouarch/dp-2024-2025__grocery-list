package com.fges.commands;

import com.fges.Command;
import com.fges.model.GroceryManager;
import com.fges.model.CommandOptions;

import java.util.List;

/**
 * Commande pour ajouter des articles à la liste de courses.
 */
public class AddCommand implements Command {
    @Override
    public String execute(List<String> args, GroceryManager groceryManager, CommandOptions options) throws Exception {
        if (args.size() < 3) {
            throw new IllegalArgumentException("Arguments manquants pour la commande 'add'.");
        }

        String itemName = args.get(1);
        int quantity;
        try {
            quantity = Integer.parseInt(args.get(2));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Quantité invalide. La quantité doit être un nombre entier.");
        }

        if (quantity == 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être zéro.");
        }

        // Ajouter l'article avec sa catégorie
        groceryManager.addItem(itemName, quantity, options.getCategory());

        return "Ajouté " + quantity + " " + itemName + " dans la catégorie '" + 
               (options.getCategory() != null ? options.getCategory() : "default") + "'";
    }
} 