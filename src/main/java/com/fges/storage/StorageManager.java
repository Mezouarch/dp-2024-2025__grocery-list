package com.fges.storage;

import com.fges.model.GroceryItem;
import java.io.IOException;
import java.util.List;

/**
 * Interface définissant les opérations de stockage et de chargement pour les listes de courses.
 * Permet de sauvegarder et charger la liste de courses dans différents formats.
 */
public interface StorageManager {
    /**
     * Sauvegarde la liste d'articles dans un fichier.
     *
     * @param items    la liste des articles à sauvegarder
     * @param fileName le nom du fichier où sauvegarder
     * @throws IOException en cas d'erreur d'écriture
     */
    void saveGroceryList(List<GroceryItem> items, String fileName) throws IOException;

    /**
     * Charge la liste d'articles depuis un fichier.
     *
     * @param fileName le nom du fichier à charger
     * @return la liste des articles chargés
     * @throws IOException en cas d'erreur de lecture
     */
    List<GroceryItem> loadGroceryList(String fileName) throws IOException;
} 