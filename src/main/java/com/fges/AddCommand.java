package com.fges;

import java.util.List;

/**
 * Commande pour ajouter ou modifier un article dans la liste de courses.
 */
public class AddCommand implements Command {

    @Override
    public String execute(List<String> args, GroceryManager groceryManager, String category) throws Exception {
        // Vérifier les arguments
        InputValidator.validateCommandArgs(args, 3, "add");

        String itemName = args.get(1);
        String quantityStr = args.get(2);
        int quantity = InputValidator.parseAndValidateQuantity(quantityStr);
        
        // Utiliser la catégorie fournie ou "default"
        String itemCategory = category != null ? category : "default";
        
        // Ajouter ou mettre à jour l'article
        groceryManager.addItem(itemName, quantity, itemCategory);
        return MessageFormatter.formatAddConfirmation(itemName, quantity, itemCategory);
    }
}