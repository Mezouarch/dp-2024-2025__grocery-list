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
    }

    /**
     * Charge la liste de courses depuis un fichier.
     *
     * @param fileName le nom du fichier à charger
     * @throws IOException en cas d'erreur lors du chargement
     */
   

     public void loadGroceryList(String fileName) throws IOException {
        this.currentFileName = fileName;
        groceryItems.clear();
        
        if (storageManager instanceof JsonStorageManager) {
            JsonStorageManager jsonManager = (JsonStorageManager) storageManager;
            // Plus besoin de cast car les méthodes sont maintenant accessibles
            groceryItems.putAll(jsonManager.loadWithCategories(fileName));
        } else {
            Map<String, Integer> loadedMap = storageManager.loadGroceryList(fileName);
            loadedMap.forEach((k, v) -> groceryItems.put(k, new ItemInfo(v, "default")));
        }
    }
    
    public void saveGroceryList(String fileName) throws IOException {
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
     * Si la quantité devient négative ou nulle, l'article est supprimé.
     *
     * @param itemName le nom de l'article
     * @param quantity la quantité à ajouter (ou retirer si négative)
     * @param category la catégorie de l'article
     * @throws IOException en cas d'erreur lors de la sauvegarde automatique
     */
    public void addItem(String itemName, int quantity, String category) throws IOException {
        String trimmedName = itemName.trim();
        String itemCategory = category != null ? category : "default";
        
        // Chercher l'article existant
        ItemInfo existingItem = groceryItems.get(trimmedName);
        if (existingItem != null) {
            // Additionner la quantité tout en conservant la catégorie
            int newQuantity = existingItem.getQuantity() + quantity;
            if (newQuantity <= 0) {
                groceryItems.remove(trimmedName);
                groceryMap.remove(trimmedName);  // Aussi supprimer de groceryMap
            } else {
                groceryItems.put(trimmedName, new ItemInfo(newQuantity, existingItem.getCategory()));
                groceryMap.put(trimmedName, newQuantity);  // Mettre à jour groceryMap
            }
        } else if (quantity > 0) {
            // Nouvel article
            groceryItems.put(trimmedName, new ItemInfo(quantity, itemCategory));
            groceryMap.put(trimmedName, quantity);  // Ajouter à groceryMap
        }
        
        saveIfFileNameExists();
    }

    /**
     * Version de compatibilité pour l'ancienne API.
     * Ajoute ou met à jour un article dans la liste avec la catégorie par défaut.
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
        
        // Grouper les articles par catégorie
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
            throw new Exception("L'article " + itemName + " n'existe pas dans la liste.");
        }
        
        groceryItems.remove(itemName);
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