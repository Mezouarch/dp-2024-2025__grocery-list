package com.fges;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestionnaire principal de la liste de courses.
 * Cette classe utilise un StorageManager pour charger et sauvegarder les données.
 */
public class GroceryManager {
    
    private final Map<String, Integer> groceryMap = new HashMap<>();
    private final Map<String, ItemInfo> groceryItems = new HashMap<>();
    private final CategoryManager categoryManager;
    private String currentFileName;
    private final StorageManager storageManager;

    // Classe interne pour stocker les informations d'un article
    public static class ItemInfo {
        private final int quantity;
        private final String category;

        public ItemInfo(int quantity, String category) {
            this.quantity = quantity;
            this.category = category != null ? category : "default";
        }

        // Getters
        public int getQuantity() { return quantity; }
        public String getCategory() { return category; }
    }

    /**
     * Crée un nouveau gestionnaire de liste de courses.
     *
     * @param storageManager le gestionnaire de stockage à utiliser
     */
    public GroceryManager(StorageManager storageManager) {
        this.storageManager = storageManager;
        this.categoryManager = new CategoryManager();
    }

    /**
     * Charge la liste de courses depuis un fichier.
     *
     * @param fileName le nom du fichier à charger
     * @throws IOException en cas d'erreur lors du chargement
     */
    public void loadGroceryList(String fileName) throws IOException {
        InputValidator.validateFileName(fileName);
        this.currentFileName = fileName;
        groceryItems.clear();
        categoryManager = new CategoryManager();
        
        if (storageManager instanceof JsonStorageManager) {
            JsonStorageManager jsonManager = (JsonStorageManager) storageManager;
            groceryItems.putAll(jsonManager.loadWithCategories(fileName));
            // Mettre à jour le CategoryManager
            groceryItems.forEach((name, info) -> 
                categoryManager.addItemToCategory(info.getCategory(), name));
        } else {
            Map<String, Integer> loadedMap = storageManager.loadGroceryList(fileName);
            loadedMap.forEach((k, v) -> {
                groceryItems.put(k, new ItemInfo(v, "default"));
                categoryManager.addItemToCategory("default", k);
            });
        }
    }
    
    public void saveGroceryList(String fileName) throws IOException {
        InputValidator.validateFileName(fileName);
        this.currentFileName = fileName;
        
        if (storageManager instanceof JsonStorageManager) {
            JsonStorageManager jsonManager = (JsonStorageManager) storageManager;
            jsonManager.saveWithCategories(fileName, groceryItems);
        } else {
            Map<String, Integer> simpleMap = new HashMap<>();
            groceryItems.forEach((k, v) -> simpleMap.put(k, v.getQuantity()));
            storageManager.saveGroceryList(fileName, simpleMap);
        }
    }

    /**
     * Ajoute ou met à jour un article dans la liste avec sa catégorie.
     *
     * @param itemName le nom de l'article
     * @param quantity la quantité à ajouter (ou retirer si négative)
     * @param category la catégorie de l'article
     * @throws IOException en cas d'erreur lors de la sauvegarde automatique
     */
    public void addItem(String itemName, int quantity, String category) throws IOException {
        InputValidator.validateItemName(itemName);
        InputValidator.validateQuantity(quantity);
        
        String trimmedName = itemName.trim();
        String itemCategory = category != null ? category : "default";
        
        // Chercher l'article existant
        ItemInfo existingItem = groceryItems.get(trimmedName);
        if (existingItem != null) {
            // Additionner la quantité tout en conservant la catégorie
            int newQuantity = existingItem.getQuantity() + quantity;
            if (newQuantity <= 0) {
                groceryItems.remove(trimmedName);
                groceryMap.remove(trimmedName);
                categoryManager.removeItemFromCategory(existingItem.getCategory(), trimmedName);
            } else {
                groceryItems.put(trimmedName, new ItemInfo(newQuantity, existingItem.getCategory()));
                groceryMap.put(trimmedName, newQuantity);
            }
        } else if (quantity > 0) {
            // Nouvel article
            groceryItems.put(trimmedName, new ItemInfo(quantity, itemCategory));
            groceryMap.put(trimmedName, quantity);
            categoryManager.addItemToCategory(itemCategory, trimmedName);
        }
        
        saveIfFileNameExists();
    }

    /**
     * Version de compatibilité pour l'ancienne API.
     *
     * @param itemName le nom de l'article
     * @param quantity la quantité à ajouter (ou retirer si négative)
     * @throws IOException en cas d'erreur lors de la sauvegarde automatique
     */
    public void addItem(String itemName, int quantity) throws IOException {
        addItem(itemName, quantity, "default");
    }

    /**
     * Méthode pour obtenir la liste d'articles groupés par catégorie.
     *
     * @return une map avec les catégories comme clés et les listes d'articles comme valeurs
     */
    public Map<String, List<String>> getGroceryListByCategory() {
        Map<String, List<String>> result = new HashMap<>();
        
        for (Map.Entry<String, ItemInfo> entry : groceryItems.entrySet()) {
            String itemName = entry.getKey();
            ItemInfo info = entry.getValue();
            String category = info.getCategory();
            
            result.computeIfAbsent(category, k -> new ArrayList<>())
                  .add(itemName + ": " + info.getQuantity());
        }
        
        return result;
    }

    /**
     * Méthode de compatibilité pour l'ancienne API.
     *
     * @return une liste d'articles au format "nom: quantité"
     */
    public List<String> getGroceryList() {
        List<String> result = new ArrayList<>();
        Map<String, List<String>> byCategory = getGroceryListByCategory();
        
        for (Map.Entry<String, List<String>> entry : byCategory.entrySet()) {
            result.addAll(entry.getValue());
        }
        
        return result;
    }

    /**
     * Vérifie si un article existe dans la liste.
     *
     * @param itemName le nom de l'article à vérifier
     * @return true si l'article existe avec une quantité positive
     */
    public boolean doesItemExist(String itemName) {
        return groceryItems.containsKey(itemName) && groceryItems.get(itemName).getQuantity() > 0;
    }

    /**
     * Récupère la quantité d'un article.
     *
     * @param itemName le nom de l'article
     * @return la quantité de l'article, ou 0 s'il n'existe pas
     */
    public int getItemQuantity(String itemName) {
        ItemInfo info = groceryItems.get(itemName);
        return info != null ? info.getQuantity() : 0;
    }

    /**
     * Supprime un article de la liste.
     *
     * @param itemName le nom de l'article à supprimer
     * @throws Exception si l'article n'existe pas
     * @throws IOException en cas d'erreur lors de la sauvegarde automatique
     */
    public void removeItem(String itemName) throws Exception {
        if (!groceryItems.containsKey(itemName)) {
            throw new Exception(MessageFormatter.formatItemNotFound(itemName));
        }
        
        ItemInfo info = groceryItems.get(itemName);
        categoryManager.removeItemFromCategory(info.getCategory(), itemName);
        groceryItems.remove(itemName);
        groceryMap.remove(itemName);
        
        saveIfFileNameExists();
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