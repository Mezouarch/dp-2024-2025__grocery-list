package com.fges;

import java.util.List;

/**
 * Commande pour ajouter ou mettre à jour un article dans la liste de courses.
 */
public class AddCommand implements Command {

    @Override
public void execute(List<String> args, GroceryManager groceryManager, String category) throws Exception {
    // Vérifier le nombre d'arguments
    validateArgs(args);
    
    // Extraire et valider le nom et la quantité
    String itemName = args.get(1);
    int quantity = parseQuantity(args.get(2));
    
    // Ajouter ou modifier la quantité de l'article
    groceryManager.addItem(itemName, quantity, category);
    
    // Affiche un message de confirmation
    displayConfirmation(itemName, quantity);
}
    
    /**
     * Valide les arguments de la commande.
     *
     * @param args les arguments à valider
     * @throws Exception si les arguments sont invalides
     */
    private void validateArgs(List<String> args) throws Exception {
        if (args.size() < 3) {
            throw new Exception("Arguments manquants pour la commande 'add'. Utilisation : add <itemName> <quantity>");
        }
    }
    
    /**
     * Parse et valide la quantité.
     *
     * @param quantityStr la chaîne représentant la quantité
     * @return la quantité en tant qu'entier
     * @throws Exception si la quantité est invalide
     */
    private int parseQuantity(String quantityStr) throws Exception {
        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity == 0) {
                throw new Exception("La quantité doit être différente de zéro.");
            }
            return quantity;
        } catch (NumberFormatException e) {
            throw new Exception("La quantité doit être un entier.");
        }
    }
    
    /**
     * Affiche un message de confirmation adapté à l'opération.
     *
     * @param itemName le nom de l'article
     * @param quantity la quantité ajoutée ou retirée
     */
    private void displayConfirmation(String itemName, int quantity) {
        if (quantity > 0) {
            System.out.println(quantity + " unités de " + itemName + " ajoutées.");
        } else {
            System.out.println(Math.abs(quantity) + " unités de " + itemName + " supprimées.");
        }
    }
}