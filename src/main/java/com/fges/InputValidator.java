package com.fges;

import java.util.List;

/**
 * Classe utilitaire pour la validation des entrées.
 * Centralise toutes les validations des données d'entrée.
 */
public class InputValidator {
    
    /**
     * Valide le nom d'un article.
     *
     * @param itemName le nom de l'article à valider
     * @throws IllegalArgumentException si le nom est invalide
     */
    public static void validateItemName(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'article ne peut pas être vide.");
        }
    }

    /**
     * Valide une quantité.
     *
     * @param quantity la quantité à valider
     * @throws IllegalArgumentException si la quantité est invalide
     */
    public static void validateQuantity(int quantity) {
        if (quantity == 0) {
            throw new IllegalArgumentException("La quantité doit être différente de zéro.");
        }
    }

    /**
     * Parse et valide une quantité à partir d'une chaîne.
     *
     * @param quantityStr la chaîne représentant la quantité
     * @return la quantité en tant qu'entier
     * @throws IllegalArgumentException si la quantité est invalide
     */
    public static int parseAndValidateQuantity(String quantityStr) {
        try {
            int quantity = Integer.parseInt(quantityStr.trim());
            validateQuantity(quantity);
            return quantity;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La quantité doit être un entier valide.");
        }
    }

    /**
     * Valide les arguments d'une commande.
     *
     * @param args les arguments à valider
     * @param minArgs le nombre minimum d'arguments requis
     * @param commandName le nom de la commande
     * @throws IllegalArgumentException si les arguments sont invalides
     */
    public static void validateCommandArgs(List<String> args, int minArgs, String commandName) {
        if (args == null || args.size() < minArgs) {
            throw new IllegalArgumentException(
                String.format("Arguments manquants pour la commande '%s'.", commandName));
        }
    }

    /**
     * Valide un nom de fichier.
     *
     * @param fileName le nom du fichier à valider
     * @throws IllegalArgumentException si le nom de fichier est invalide
     */
    public static void validateFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du fichier ne peut pas être vide.");
        }
    }

    /**
     * Valide un format de stockage.
     *
     * @param format le format à valider
     * @throws IllegalArgumentException si le format est invalide
     */
    public static void validateStorageFormat(String format) {
        if (format == null || format.trim().isEmpty()) {
            throw new IllegalArgumentException("Le format de stockage ne peut pas être vide.");
        }
        if (!format.equalsIgnoreCase("json") && !format.equalsIgnoreCase("csv")) {
            throw new IllegalArgumentException("Format de stockage non supporté. Utilisez 'json' ou 'csv'.");
        }
    }
} 