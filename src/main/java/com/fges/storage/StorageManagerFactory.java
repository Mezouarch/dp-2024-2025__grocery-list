package com.fges.storage;

import com.fges.util.MessageFormatter;

/**
 * Factory pour créer le gestionnaire de stockage approprié selon le format.
 */
public class StorageManagerFactory {
    /**
     * Crée un gestionnaire de stockage pour le format spécifié.
     *
     * @param format le format de stockage (json ou csv)
     * @return le gestionnaire de stockage approprié
     * @throws IllegalArgumentException si le format n'est pas supporté
     */
    public static StorageManager createStorageManager(String format) {
        if ("json".equalsIgnoreCase(format)) {
            return new JsonStorageManager();
        } else if ("csv".equalsIgnoreCase(format)) {
            return new CsvStorageManager();
        } else {
            throw new IllegalArgumentException(MessageFormatter.formatInvalidStorageFormat());
        }
    }
} 