package com.fges;

import java.util.List;

/**
 * Commande pour ajouter ou mettre à jour un article dans la liste de courses.
 */
public class AddCommand implements Command {

    @Override
    public void execute(List<String> args, GroceryManager groceryManager, String category) throws Exception {
        // Vérifier le nombre d'arguments
        InputValidator.validateCommandArgs(args, 3, "add");
        
        // Extraire et valider le nom et la quantité
        String itemName = args.get(1);
        int quantity = InputValidator.parseAndValidateQuantity(args.get(2));
        
        // Ajouter ou modifier la quantité de l'article
        groceryManager.addItem(itemName, quantity, category);
        
        // Afficher un message de confirmation
        if (quantity > 0) {
            System.out.println(MessageFormatter.formatAddConfirmation(itemName, quantity));
        } else {
            System.out.println(MessageFormatter.formatRemoveConfirmation(itemName, Math.abs(quantity)));
        }
    }
}