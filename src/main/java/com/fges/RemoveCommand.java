package com.fges;

import java.util.List;

/**
 * Commande pour supprimer un article de la liste de courses.
 * Peut supprimer soit une quantité spécifique, soit l'article entier.
 */
public class RemoveCommand implements Command {

    @Override
    public void execute(List<String> args, GroceryManager groceryManager) throws Exception {
        // Vérifier les arguments
        validateArgs(args);

        String itemName = args.get(1);
        
        // Vérifier si l'article existe avant toute opération
        checkItemExists(itemName, groceryManager);

        // Si aucune quantité n'est spécifiée, supprimer complètement l'article
        if (args.size() == 2) {
            removeEntireItem(itemName, groceryManager);
        } else {
            // Supprimer une quantité spécifique
            removeSpecificQuantity(itemName, args.get(2), groceryManager);
        }
    }
    
    /**
     * Valide les arguments de la commande.
     *
     * @param args les arguments à valider
     * @throws Exception si les arguments sont invalides
     */
    private void validateArgs(List<String> args) throws Exception {
        if (args.size() < 2) {
            throw new Exception("Arguments manquants pour la commande 'remove'. Utilisation : remove <itemName> [quantity]");
        }
    }
    
    /**
     * Vérifie si l'article existe dans la liste.
     *
     * @param itemName le nom de l'article à vérifier
     * @param groceryManager le gestionnaire de liste de courses
     * @throws Exception si l'article n'existe pas
     */
    private void checkItemExists(String itemName, GroceryManager groceryManager) throws Exception {
        if (!groceryManager.doesItemExist(itemName)) {
            throw new Exception("L'article " + itemName + " n'existe pas dans la liste.");
        }
    }
    
    /**
     * Supprime complètement un article de la liste.
     *
     * @param itemName le nom de l'article à supprimer
     * @param groceryManager le gestionnaire de liste de courses
     * @throws Exception en cas d'erreur
     */
    private void removeEntireItem(String itemName, GroceryManager groceryManager) throws Exception {
        groceryManager.removeItem(itemName);
        System.out.println("L'article " + itemName + " a été complètement supprimé.");
    }
    
    /**
     * Supprime une quantité spécifique d'un article.
     *
     * @param itemName le nom de l'article
     * @param quantityStr la quantité à supprimer (en tant que chaîne)
     * @param groceryManager le gestionnaire de liste de courses
     * @throws Exception en cas d'erreur
     */
    private void removeSpecificQuantity(String itemName, String quantityStr, GroceryManager groceryManager) 
            throws Exception {
        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity == 0) {
                throw new Exception("La quantité doit être différente de zéro.");
            }
            
            // Convertir en valeur négative pour le retrait
            quantity = -Math.abs(quantity);
            
            // Vérifier si la quantité à supprimer est valide
            int currentQuantity = groceryManager.getItemQuantity(itemName);
            if (Math.abs(quantity) > currentQuantity) {
                throw new Exception("Impossible de supprimer plus d'articles qu'il n'en existe.");
            }
            
            // Ajouter ou modifier la quantité de l'article
            groceryManager.addItem(itemName, quantity);
            
            System.out.println(Math.abs(quantity) + " unités de " + itemName + " supprimées.");
        } catch (NumberFormatException e) {
            throw new Exception("La quantité doit être un entier.");
        }
    }
}