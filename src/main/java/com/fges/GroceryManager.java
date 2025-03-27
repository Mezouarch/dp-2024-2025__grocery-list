package com.fges;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Gestionnaire principal de la liste de courses.
 * Cette classe utilise un StorageManager pour charger et sauvegarder les données.
 */
public class GroceryManager {
    
    private final Map<String, Integer> groceryMap = new HashMap<>();
    private String currentFileName;
    private final StorageManager storageManager;

    /**
     * Crée un nouveau gestionnaire de liste de courses.
     *
     * @param storageManager le gestionnaire de stockage à utiliser
     */
    public GroceryManager(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    /**
     * Charge la liste de courses depuis un fichier.
     *
     * @param fileName le nom du fichier à charger
     * @throws IOException en cas d'erreur lors du chargement
     */
    public void loadGroceryList(String fileName) throws IOException {
        this.currentFileName = fileName;
        groceryMap.clear();
        groceryMap.putAll(storageManager.loadGroceryList(fileName));
    }

    /**
     * Sauvegarde la liste de courses dans un fichier.
     *
     * @param fileName le nom du fichier de destination
     * @throws IOException en cas d'erreur lors de la sauvegarde
     */
    public void saveGroceryList(String fileName) throws IOException {
        this.currentFileName = fileName;
        storageManager.saveGroceryList(fileName, groceryMap);
    }

    /**
     * Ajoute ou met à jour un article dans la liste.
     * Si la quantité devient négative ou nulle, l'article est supprimé.
     *
     * @param itemName le nom de l'article
     * @param quantity la quantité à ajouter (ou retirer si négative)
     * @throws IOException en cas d'erreur lors de la sauvegarde automatique
     */
    public void addItem(String itemName, int quantity) throws IOException {
        String trimmedName = itemName.trim();
        groceryMap.merge(trimmedName, quantity, Integer::sum);
        
        // Si la quantité est devenue 0 ou négative, supprimer l'article
        if (groceryMap.getOrDefault(trimmedName, 0) <= 0) {
            groceryMap.remove(trimmedName);
        }
        
        saveIfFileNameExists();
    }

    /**
     * Vérifie si un article existe dans la liste.
     *
     * @param itemName le nom de l'article à vérifier
     * @return true si l'article existe avec une quantité positive
     */
    public boolean doesItemExist(String itemName) {
        return groceryMap.containsKey(itemName) && groceryMap.get(itemName) > 0;
    }

    /**
     * Récupère la quantité d'un article.
     *
     * @param itemName le nom de l'article
     * @return la quantité de l'article, ou 0 s'il n'existe pas
     */
    public int getItemQuantity(String itemName) {
        return groceryMap.getOrDefault(itemName, 0);
    }

    /**
     * Supprime un article de la liste. Si la quantité est spécifiée,
     * seule cette quantité est supprimée.
     *
     * @param itemName le nom de l'article à supprimer
     * @throws Exception si l'article n'existe pas
     * @throws IOException en cas d'erreur lors de la sauvegarde automatique
     */
    public void removeItem(String itemName) throws Exception {
        if (!groceryMap.containsKey(itemName)) {
            throw new Exception("L'article " + itemName + " n'existe pas dans la liste.");
        }
        
        groceryMap.remove(itemName);
        saveIfFileNameExists();
    }

    /**
     * Retourne la liste complète des articles au format "nom: quantité".
     *
     * @return liste des articles formatée
     */
    public List<String> getGroceryList() {
        return groceryMap.entrySet().stream()
            .map(entry -> entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.toList());
    }
    
    /**
     * Sauvegarde la liste si un nom de fichier existe.
     *
     * @throws IOException en cas d'erreur lors de la sauvegarde
     */
    private void saveIfFileNameExists() throws IOException {
        if (currentFileName != null) {
            saveGroceryList(currentFileName);
        }
    }
}