package com.fges;

/**
 * Fabrique pour créer des instances de StorageManager selon le format spécifié.
 */
public class StorageManagerFactory {
    
    /**
     * Crée une instance de StorageManager adaptée au format spécifié.
     * 
     * @param format le format de stockage ("json" ou "csv")
     * @return une instance de StorageManager appropriée
     * @throws IllegalArgumentException si le format n'est pas supporté
     */
    public static StorageManager createStorageManager(String format) {
        return switch (format.toLowerCase()) {
            case "json" -> new JsonStorageManager();
            case "csv" -> new CsvStorageManager();
            default -> throw new IllegalArgumentException("Format non supporté : " + format);
        };
    }
} 