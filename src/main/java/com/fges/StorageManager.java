package com.fges;

import java.io.IOException;
import java.util.Map;

/**
 * Interface pour gérer le stockage de la liste de courses dans différents formats.
 */
public interface StorageManager {
    /**
     * Charge la liste de courses depuis un fichier.
     * 
     * @param fileName le nom du fichier à charger
     * @return Map contenant les articles avec leurs quantités
     * @throws IOException en cas d'erreur lors de la lecture
     */
    Map<String, Integer> loadGroceryList(String fileName) throws IOException;

    /**
     * Sauvegarde la liste de courses dans un fichier.
     * 
     * @param fileName le nom du fichier de destination
     * @param groceryMap la map des articles avec leurs quantités
     * @throws IOException en cas d'erreur lors de l'écriture
     */
    void saveGroceryList(String fileName, Map<String, Integer> groceryMap) throws IOException;
} 