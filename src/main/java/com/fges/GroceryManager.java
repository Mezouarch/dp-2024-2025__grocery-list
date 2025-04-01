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
    private Map<String, ItemInfo> groceryItems = new HashMap<>();
    private CategoryManager categoryManager;
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
        this.groceryItems = new HashMap<>();
        this.categoryManager = new CategoryManager();
    }

    /**
     * Charge la liste de courses depuis un fichier.
     *
     * @param fileName le nom du fichier à charger
     * @throws IOException en cas d'erreur lors du chargement
     */
    public void loadGroceryList(String fileName) throws IOException {
        try {
            // Réinitialiser le CategoryManager
            categoryManager = new CategoryManager();
            
            if (storageManager instanceof JsonStorageManager) {
                // Utiliser la méthode spécifique pour charger avec les catégories
                Map<String, ItemInfo> loadedItems = ((JsonStorageManager) storageManager).loadWithCategories(fileName);
                groceryItems.clear();
                groceryItems.putAll(loadedItems);
                
                // Mettre à jour le CategoryManager avec les catégories chargées
                for (Map.Entry<String, ItemInfo> entry : loadedItems.entrySet()) {
                    categoryManager.addItemToCategory(entry.getValue().getCategory(), entry.getKey());
                }
            } else {
                // Pour les autres types de StorageManager, utiliser la méthode standard
                Map<String, Integer> loadedItems = storageManager.loadGroceryList(fileName);
                groceryItems.clear();
                loadedItems.forEach((itemName, quantity) -> {
                    ItemInfo info = new ItemInfo(quantity, "default");
                    groceryItems.put(itemName, info);
                    categoryManager.addItemToCategory("default", itemName);
                });
            }
            currentFileName = fileName;
        } catch (IOException e) {
            // Si le fichier est vide ou corrompu, on commence avec une liste vide
            groceryItems.clear();
            throw e;
        }
    }
    
    /**
     * Sauvegarde la liste de courses dans le fichier spécifié.
     *
     * @param fileName le nom du fichier de destination
     * @throws IOException si une erreur survient lors de la sauvegarde
     */
    public void saveGroceryList(String fileName) throws IOException {
        InputValidator.validateFileName(fileName);
        if (storageManager instanceof JsonStorageManager) {
            // Utiliser la méthode spécifique pour sauvegarder avec les catégories
            ((JsonStorageManager) storageManager).saveWithCategories(fileName, groceryItems);
        } else {
            // Pour les autres types de StorageManager, utiliser la méthode standard
            Map<String, Integer> itemsToSave = new HashMap<>();
            groceryItems.forEach((name, info) -> itemsToSave.put(name, info.getQuantity()));
            storageManager.saveGroceryList(fileName, itemsToSave);
        }
        currentFileName = fileName;
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
        String itemCategory = category != null ? category.trim() : "default";
        
        // Chercher l'article existant
        ItemInfo existingItem = groceryItems.get(trimmedName);
        
        if (existingItem != null) {
            // Article existant
            String existingCategory = existingItem.getCategory();
            int newQuantity = existingItem.getQuantity() + quantity;
            
            // Si nouvel article avec catégorie spécifiée et l'article existant est dans 'default',
            // alors mettre à jour sa catégorie
            boolean shouldUpdateCategory = itemCategory != null && !itemCategory.equals("default") && 
                                          "default".equals(existingCategory);
            
            if (newQuantity <= 0) {
                // Supprimer l'article si la quantité devient nulle ou négative
                groceryItems.remove(trimmedName);
                groceryMap.remove(trimmedName);
                categoryManager.removeItemFromCategory(existingCategory, trimmedName);
            } else {
                // Mettre à jour l'article
                String categoryToUse = shouldUpdateCategory ? itemCategory : existingCategory;
                
                // Si la catégorie change, mettre à jour le gestionnaire de catégories
                if (shouldUpdateCategory) {
                    categoryManager.removeItemFromCategory(existingCategory, trimmedName);
                    categoryManager.addItemToCategory(itemCategory, trimmedName);
                }
                
                groceryItems.put(trimmedName, new ItemInfo(newQuantity, categoryToUse));
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
        String category = info.getCategory();
        categoryManager.removeItemFromCategory(category, itemName);
        groceryItems.remove(itemName);
        groceryMap.remove(itemName);
        
        // Si la catégorie est vide, la supprimer
        if (categoryManager.getItemsInCategory(category).isEmpty()) {
            categoryManager.removeCategory(category);
        }
        
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

    /**
     * Vérifie si une catégorie existe.
     *
     * @param category la catégorie à vérifier
     * @return true si la catégorie existe
     */
    public boolean categoryExists(String category) {
        return categoryManager.categoryExists(category);
    }

    /**
     * Obtient la liste des articles dans une catégorie.
     *
     * @param category la catégorie
     * @return la liste des articles formatés
     */
    public List<String> getItemsInCategory(String category) {
        return categoryManager.getItemsInCategory(category);
    }

    /**
     * Obtient la catégorie d'un article.
     *
     * @param itemName le nom de l'article
     * @return la catégorie de l'article
     */
    public String getItemCategory(String itemName) {
        ItemInfo info = groceryItems.get(itemName);
        return info != null ? info.getCategory() : "default";
    }
}