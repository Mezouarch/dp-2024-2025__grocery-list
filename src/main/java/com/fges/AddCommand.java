package com.fges;

import java.util.List;

public class AddCommand implements Command {

    @Override
    public void execute(List<String> args, GroceryManager groceryManager) throws Exception {
        if (args.size() < 3) {
            throw new Exception("Arguments manquants pour la commande 'add'. Utilisation : add <itemName> <quantity>");
        }

        String itemName = args.get(1);
        int quantity;
        try {
            quantity = Integer.parseInt(args.get(2));
            if (quantity == 0) {
                throw new Exception("La quantité doit être différente de zéro.");
            }
        } catch (NumberFormatException e) {
            throw new Exception("La quantité doit être un entier.");
        }

        // Ajouter ou modifier la quantité de l'article
        groceryManager.addItem(itemName, quantity);
        
        if (quantity > 0) {
            System.out.println(quantity + " unités de " + itemName + " ajoutées.");
        } else {
            System.out.println(Math.abs(quantity) + " unités de " + itemName + " supprimées.");
        }
    }
}