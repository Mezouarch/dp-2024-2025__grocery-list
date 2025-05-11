package com.fges.model;

import com.fges.storage.StorageManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Gestionnaire de la liste de courses.
 * Cette classe centralise toutes les opérations sur la liste des articles.
 */
public class GroceryManager {
    private final List<GroceryItem> items;
    private final StorageManager storageManager;
    private final CategoryManager categoryManager;

    /**
     * Construit un gestionnaire de liste de courses avec le gestionnaire de stockage spécifié.
     *
     * @param storageManager le gestionnaire de stockage à utiliser
     */
    public GroceryManager(StorageManager storageManager) {
        this.items = new ArrayList<>();
        this.storageManager = storageManager;
        this.categoryManager = new CategoryManager();
    }

    /**
     * Charge la liste de courses à partir d'un fichier.
     *
     * @param fileName le nom du fichier à charger
     * @throws IOException en cas d'erreur de lecture
     */
    public void loadGroceryList(String fileName) throws IOException {
        // Charger les articles
        List<GroceryItem> loadedItems = storageManager.loadGroceryList(fileName);
        
        // Remplacer la liste actuelle
        items.clear();
        items.addAll(loadedItems);
        
        // Mettre à jour les catégories
        for (GroceryItem item : loadedItems) {
            categoryManager.addItemToCategory(item.getName(), item.getCategory());
        }
    }

    /**
     * Sauvegarde la liste de courses dans un fichier.
     *
     * @param fileName le nom du fichier où sauvegarder
     * @throws IOException en cas d'erreur d'écriture
     */
    public void saveGroceryList(String fileName) throws IOException {
        storageManager.saveGroceryList(items, fileName);
    }

    /**
     * Ajoute un article à la liste de courses.
     * Si l'article existe déjà, sa quantité est mise à jour.
     *
     * @param name le nom de l'article
     * @param quantity la quantité à ajouter
     * @param category la catégorie de l'article (peut être null)
     * @throws IOException en cas d'erreur lors de la mise à jour
     */
    public void addItem(String name, int quantity, String category) throws IOException {
        // Validation des paramètres d'entrée
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'article ne peut pas être vide");
        }
        
        if (quantity == 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être nulle");
        }
        
        // Vérifier si l'article existe déjà
        Optional<GroceryItem> existingItem = items.stream()
                .filter(item -> item.getName().equals(name))
                .findFirst();

        if (existingItem.isPresent()) {
            // Si l'article existe, mettre à jour sa quantité
            GroceryItem oldItem = existingItem.get();
            int newQuantity = oldItem.getQuantity() + quantity;
            
            try {
                // Supprimer l'ancien item
                removeItem(name);
                
                // Ajouter le nouvel item avec la quantité mise à jour et la catégorie appropriée
                String categoryToUse = category != null ? category : oldItem.getCategory();
                addNewItem(name, newQuantity, categoryToUse);
            } catch (Exception e) {
                throw new IOException("Erreur lors de la mise à jour de l'article: " + e.getMessage(), e);
            }
        } else {
            // Si l'article n'existe pas, l'ajouter
            addNewItem(name, quantity, category);
        }
    }

    /**
     * Ajoute un nouvel article à la liste.
     *
     * @param name le nom de l'article
     * @param quantity la quantité
     * @param category la catégorie
     */
    private void addNewItem(String name, int quantity, String category) {
        // Validation
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'article ne peut pas être vide");
        }
        
        if (quantity == 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être nulle");
        }
        
        // Créer le nouvel article
        GroceryItem newItem = new GroceryItem(name, quantity, category);
        
        // Ajouter à la liste
        items.add(newItem);
        
        // Enregistrer la catégorie
        categoryManager.addItemToCategory(name, category);
    }

    /**
     * Supprime un article de la liste de courses.
     *
     * @param name le nom de l'article à supprimer
     * @throws Exception si l'article n'existe pas
     */
    public void removeItem(String name) throws Exception {
        // Vérifier si l'article existe
        if (!doesItemExist(name)) {
            throw new Exception("Article non trouvé : " + name);
        }
        
        // Supprimer l'article de la liste
        items.removeIf(item -> item.getName().equals(name));
        
        // Supprimer l'article des catégories
        categoryManager.removeItem(name);
    }

    /**
     * Vérifie si un article existe dans la liste.
     *
     * @param name le nom de l'article à vérifier
     * @return true si l'article existe, false sinon
     */
    public boolean doesItemExist(String name) {
        return items.stream().anyMatch(item -> item.getName().equals(name));
    }

    /**
     * Récupère la quantité d'un article.
     *
     * @param name le nom de l'article
     * @return la quantité de l'article, ou 0 s'il n'existe pas
     */
    public int getItemQuantity(String name) {
        return items.stream()
                .filter(item -> item.getName().equals(name))
                .findFirst()
                .map(GroceryItem::getQuantity)
                .orElse(0);
    }

    /**
     * Récupère la catégorie d'un article.
     *
     * @param name le nom de l'article
     * @return la catégorie de l'article, ou "default" s'il n'existe pas
     */
    public String getItemCategory(String name) {
        return items.stream()
                .filter(item -> item.getName().equals(name))
                .findFirst()
                .map(GroceryItem::getCategory)
                .orElse("default");
    }

    /**
     * Vérifie si une catégorie existe.
     *
     * @param category le nom de la catégorie
     * @return true si la catégorie existe, false sinon
     */
    public boolean categoryExists(String category) {
        return categoryManager.categoryExists(category);
    }

    /**
     * Récupère la liste des articles dans une catégorie.
     *
     * @param category le nom de la catégorie
     * @return la liste des articles dans cette catégorie (formatés en chaînes)
     */
    public List<String> getItemsInCategory(String category) {
        List<String> itemNames = categoryManager.getItemsInCategory(category);
        
        // Transformer les noms en chaînes formatées avec la quantité
        return itemNames.stream()
                .map(name -> {
                    GroceryItem item = getItemByName(name);
                    return item != null ? item.toString() : name;
                })
                .collect(Collectors.toList());
    }

    /**
     * Récupère un article par son nom.
     *
     * @param name le nom de l'article
     * @return l'article correspondant, ou null s'il n'existe pas
     */
    private GroceryItem getItemByName(String name) {
        return items.stream()
                .filter(item -> item.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Récupère la liste de courses groupée par catégorie.
     *
     * @return un mapping des catégories vers leurs articles (formatés en chaînes)
     */
    public Map<String, List<String>> getGroceryListByCategory() {
        Map<String, List<String>> result = new HashMap<>();
        
        Map<String, List<GroceryItem>> itemsByCategory = items.stream()
                .collect(Collectors.groupingBy(GroceryItem::getCategory));
        
        // Transformer chaque article en chaîne formatée
        for (Map.Entry<String, List<GroceryItem>> entry : itemsByCategory.entrySet()) {
            List<String> formattedItems = entry.getValue().stream()
                    .map(GroceryItem::toString)
                    .collect(Collectors.toList());
            
            result.put(entry.getKey(), formattedItems);
        }
        
        return result;
    }

    /**
     * Récupère la liste complète des articles.
     *
     * @return la liste des articles
     */
    public List<GroceryItem> getItems() {
        return new ArrayList<>(items);
    }

    /**
     * Met à jour un article existant avec de nouvelles valeurs.
     *
     * @param name le nom de l'article à mettre à jour
     * @param newQuantity la nouvelle quantité
     * @param newCategory la nouvelle catégorie
     * @throws Exception si l'article n'existe pas
     */
    public void updateItem(String name, int newQuantity, String newCategory) throws Exception {
        // Vérifier si l'article existe
        if (!doesItemExist(name)) {
            throw new Exception("Article non trouvé : " + name);
        }
        
        // Trouver l'article
        Optional<GroceryItem> itemOpt = items.stream()
                .filter(item -> item.getName().equals(name))
                .findFirst();
        
        if (itemOpt.isPresent()) {
            GroceryItem oldItem = itemOpt.get();
            
            // Supprimer l'ancien item
            items.remove(oldItem);
            
            // Créer un nouvel item avec les nouvelles valeurs
            int quantity = newQuantity > 0 ? newQuantity : oldItem.getQuantity();
            String category = newCategory != null ? newCategory : oldItem.getCategory();
            GroceryItem newItem = new GroceryItem(name, quantity, category);
            
            // Ajouter le nouvel item
            items.add(newItem);
            
            // Mettre à jour la catégorie si nécessaire
            if (newCategory != null && !newCategory.equals(oldItem.getCategory())) {
                categoryManager.addItemToCategory(name, newCategory);
            }
        }
    }
} 