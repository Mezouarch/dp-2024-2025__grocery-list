package com.fges.util;

import java.util.List;
import java.util.Map;

/**
 * Classe utilitaire pour le formatage des messages de sortie.
 * Centralise tous les messages affichés à l'utilisateur.
 */
public class MessageFormatter {
    
    /**
     * Formate un message de confirmation d'ajout.
     *
     * @param itemName le nom de l'article
     * @param quantity la quantité ajoutée
     * @param category la catégorie de l'article
     * @return le message formaté
     */
    public static String formatAddConfirmation(String itemName, int quantity, String category) {
        return String.format("Ajouté %d %s dans la catégorie '%s'", quantity, itemName, category);
    }

    /**
     * Formate un message de confirmation de suppression.
     *
     * @param itemName le nom de l'article
     * @param quantity la quantité supprimée
     * @return le message formaté
     */
    public static String formatRemoveConfirmation(String itemName, int quantity) {
        return String.format("Supprimé %d %s", quantity, itemName);
    }

    /**
     * Formate un message de suppression complète.
     *
     * @param itemName le nom de l'article
     * @return le message formaté
     */
    public static String formatCompleteRemoval(String itemName) {
        return String.format("Supprimé %s", itemName);
    }

    /**
     * Formate un message pour une liste vide.
     *
     * @return le message formaté
     */
    public static String formatEmptyList() {
        return "La liste de courses est vide.";
    }

    /**
     * Formate un message pour une catégorie vide.
     *
     * @param category la catégorie
     * @return le message formaté
     */
    public static String formatEmptyCategory(String category) {
        return String.format("Aucun article dans la catégorie: %s", category);
    }

    /**
     * Formate un message d'erreur pour un article inexistant.
     *
     * @param itemName le nom de l'article
     * @return le message formaté
     */
    public static String formatItemNotFound(String itemName) {
        return String.format("Article non trouvé : %s", itemName);
    }

    /**
     * Formate un message d'erreur pour une quantité invalide.
     *
     * @param itemName le nom de l'article
     * @param currentQuantity la quantité actuelle
     * @param requestedQuantity la quantité demandée
     * @return le message formaté
     */
    public static String formatInvalidQuantity(String itemName, int currentQuantity, int requestedQuantity) {
        return String.format("Quantité invalide pour %s. Quantité actuelle : %d, Quantité demandée : %d",
                itemName, currentQuantity, requestedQuantity);
    }

    /**
     * Formate un message d'erreur pour un format de stockage invalide.
     *
     * @return le message formaté
     */
    public static String formatInvalidStorageFormat() {
        return "Format de stockage non supporté. Utilisez 'json' ou 'csv'.";
    }

    /**
     * Formate un message d'erreur pour un fichier manquant.
     *
     * @param fileName le nom du fichier
     * @return le message formaté
     */
    public static String formatFileNotFound(String fileName) {
        return String.format("Le fichier %s n'existe pas.", fileName);
    }

    /**
     * Formate un message d'erreur pour une commande inconnue.
     *
     * @param commandName le nom de la commande
     * @return le message formaté
     */
    public static String formatUnknownCommand(String commandName) {
        return String.format("Commande inconnue : %s", commandName);
    }

    /**
     * Formate un message pour une catégorie non trouvée.
     *
     * @param category la catégorie
     * @return le message formaté
     */
    public static String formatCategoryNotFound(String category) {
        return String.format("Catégorie non trouvée : %s", category);
    }

    /**
     * Formate l'en-tête d'une catégorie.
     *
     * @param category la catégorie
     * @return le message formaté
     */
    public static String formatCategoryHeader(String category) {
        return String.format("# %s:", category);
    }
} 