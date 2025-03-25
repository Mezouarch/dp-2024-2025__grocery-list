package com.fges;

import java.util.List;

public class RemoveCommand implements Command {

    @Override
    public void execute(List<String> args, GroceryManager groceryManager) throws Exception {
        if (args.size() < 2) {
            throw new Exception("Arguments manquants pour la commande 'remove'. Utilisation : remove <itemName> [quantity]");
        }

        String itemName = args.get(1);
        int quantity = 1; // Par défaut, retire 1 unité

        // Si une quantité est spécifiée
        if (args.size() > 2) {
            try {
                quantity = Integer.parseInt(args.get(2));
                if (quantity == 0) {
                    throw new Exception("La quantité doit être différente de zéro.");
                }
                // Convertir en valeur négative pour le retrait
                quantity = -Math.abs(quantity);
            } catch (NumberFormatException e) {
                throw new Exception("La quantité doit être un entier.");
            }
        }

        // Ajouter ou modifier la quantité de l'article (en utilisant addItem avec une quantité négative)
        groceryManager.addItem(itemName, quantity);
        
        if (quantity < 0) {
            System.out.println(Math.abs(quantity) + " unités de " + itemName + " supprimées.");
        }
    }
}