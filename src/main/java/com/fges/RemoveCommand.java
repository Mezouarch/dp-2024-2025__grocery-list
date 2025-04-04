package com.fges;

import java.util.List;
import java.io.IOException;

/**
 * Commande pour supprimer un article de la liste de courses.
 * Peut supprimer soit une quantité spécifique, soit l'article entier.
 */
public class RemoveCommand implements Command {

    @Override
    public String execute(List<String> args, GroceryManager groceryManager, String category) throws Exception {
        // Vérifier les arguments
        InputValidator.validateCommandArgs(args, 2, "remove");

        String itemName = args.get(1);
        
        // Vérifier si l'article existe avant toute opération
        if (!groceryManager.doesItemExist(itemName)) {
            throw new IllegalArgumentException(MessageFormatter.formatItemNotFound(itemName));
        }

        // Si aucune quantité n'est spécifiée, supprimer complètement l'article
        if (args.size() == 2) {
            return removeEntireItem(itemName, groceryManager);
        } else {
            // Supprimer une quantité spécifique
            return removeSpecificQuantity(itemName, args.get(2), groceryManager);
        }
    }
    
    /**
     * Supprime complètement un article de la liste.
     *
     * @param itemName le nom de l'article à supprimer
     * @param groceryManager le gestionnaire de liste de courses
     * @return le message de confirmation
     * @throws Exception si une erreur survient lors de l'exécution
     */
    private String removeEntireItem(String itemName, GroceryManager groceryManager) throws Exception {
        groceryManager.removeItem(itemName);
        return MessageFormatter.formatCompleteRemoval(itemName);
    }
    
    /**
     * Supprime une quantité spécifique d'un article.
     *
     * @param itemName le nom de l'article
     * @param quantityStr la quantité à supprimer (en tant que chaîne)
     * @param groceryManager le gestionnaire de liste de courses
     * @return le message de confirmation
     * @throws Exception si une erreur survient lors de l'exécution
     */
    private String removeSpecificQuantity(String itemName, String quantityStr, GroceryManager groceryManager) 
            throws Exception {
        int quantity = InputValidator.parseAndValidateQuantity(quantityStr);
        quantity = -Math.abs(quantity); // Convertir en valeur négative pour le retrait
        
        // Vérifier si la quantité à supprimer est valide
        int currentQuantity = groceryManager.getItemQuantity(itemName);
        if (Math.abs(quantity) > currentQuantity) {
            throw new IllegalArgumentException(MessageFormatter.formatInvalidQuantity(itemName, currentQuantity, Math.abs(quantity)));
        }
        
        // Ajouter ou modifier la quantité de l'article
        groceryManager.addItem(itemName, quantity, groceryManager.getItemCategory(itemName));
        
        return MessageFormatter.formatRemoveConfirmation(itemName, Math.abs(quantity));
    }
}