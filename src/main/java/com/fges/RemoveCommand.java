package com.fges;

import java.util.List;

public class RemoveCommand implements Command {

    @Override
    public void execute(List<String> args, GroceryManager groceryManager) throws Exception {
        if (args.size() < 2) {
            throw new Exception("Arguments manquants pour la commande 'remove'. Utilisation : remove <itemName> [quantity]");
        }

        String itemName = args.get(1);
        
        // Si aucune quantité n'est spécifiée, supprimer complètement l'article
        if (args.size() == 2) {
            if (!groceryManager.getGroceryList().stream().anyMatch(item -> item.startsWith(itemName + ":"))) {
                throw new Exception("L'article " + itemName + " n'existe pas dans la liste.");
            }
            
            // Supprimer complètement l'article
            groceryManager.addItem(itemName, Integer.MIN_VALUE);
            System.out.println("L'article " + itemName + " a été complètement supprimé.");
        } else {
            // Logique existante pour supprimer une quantité spécifique
            try {
                int quantity = Integer.parseInt(args.get(2));
                if (quantity == 0) {
                    throw new Exception("La quantité doit être différente de zéro.");
                }
                
                // Convertir en valeur négative pour le retrait
                quantity = -Math.abs(quantity);
                
                // Ajouter ou modifier la quantité de l'article
                groceryManager.addItem(itemName, quantity);
                
                System.out.println(Math.abs(quantity) + " unités de " + itemName + " supprimées.");
            } catch (NumberFormatException e) {
                throw new Exception("La quantité doit être un entier.");
            }
        }
    }
}