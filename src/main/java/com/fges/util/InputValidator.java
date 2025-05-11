package com.fges.util;

/**
 * Classe utilitaire pour la validation des entrées utilisateur.
 */
public class InputValidator {
    /**
     * Vérifie si un nom d'article est valide.
     * Un nom est considéré valide s'il n'est pas null, pas vide et ne contient pas de caractères spéciaux.
     *
     * @param name le nom à vérifier
     * @return true si le nom est valide, false sinon
     */
    public static boolean isValidItemName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        // Vérifier que le nom ne contient pas de caractères spéciaux
        // qui pourraient causer des problèmes avec les formats de stockage
        return !name.contains(",") && !name.contains(":") && !name.contains(";");
    }
    
    /**
     * Vérifie si une quantité est valide.
     * Une quantité est considérée valide si elle est supérieure à 0.
     *
     * @param quantity la quantité à vérifier
     * @return true si la quantité est valide, false sinon
     */
    public static boolean isValidQuantity(int quantity) {
        return quantity > 0;
    }
    
    /**
     * Vérifie si un nom de fichier est valide.
     * Un nom de fichier est considéré valide s'il n'est pas null, pas vide et ne contient pas de caractères spéciaux
     * qui pourraient causer des problèmes avec le système de fichiers.
     *
     * @param fileName le nom de fichier à vérifier
     * @return true si le nom de fichier est valide, false sinon
     */
    public static boolean isValidFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }
        
        // Vérifier que le nom de fichier ne contient pas de caractères invalides
        return !fileName.contains("\\") && !fileName.contains("/") && !fileName.contains(":")
                && !fileName.contains("*") && !fileName.contains("?") && !fileName.contains("\"")
                && !fileName.contains("<") && !fileName.contains(">") && !fileName.contains("|");
    }
    
    /**
     * Vérifie si un nom de catégorie est valide.
     * Un nom de catégorie est considéré valide s'il n'est pas null, pas vide et ne contient pas de caractères spéciaux
     * qui pourraient causer des problèmes avec les formats de stockage.
     *
     * @param category le nom de catégorie à vérifier
     * @return true si le nom de catégorie est valide, false sinon
     */
    public static boolean isValidCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return false;
        }
        
        // Vérifier que la catégorie ne contient pas de caractères spéciaux
        return !category.contains(",") && !category.contains(":") && !category.contains(";");
    }
    
    /**
     * Vérifie si un format de stockage est valide.
     * Un format est considéré valide s'il est égal à "json" ou "csv" (insensible à la casse).
     *
     * @param format le format à vérifier
     * @return true si le format est valide, false sinon
     */
    public static boolean isValidStorageFormat(String format) {
        if (format == null || format.trim().isEmpty()) {
            return false;
        }
        
        return "json".equalsIgnoreCase(format) || "csv".equalsIgnoreCase(format);
    }
    
    /**
     * Vérifie si un port est valide.
     * Un port est considéré valide s'il est compris entre 1 et 65535.
     *
     * @param port le port à vérifier
     * @return true si le port est valide, false sinon
     */
    public static boolean isValidPort(int port) {
        return port >= 1 && port <= 65535;
    }
} 