package com.fges.model;

import com.fges.model.strategies.AddItemStrategy;
import com.fges.model.strategies.ItemOperationStrategy;
import com.fges.model.strategies.RemoveItemStrategy;
import com.fges.model.strategies.UpdateItemStrategy;
import com.fges.storage.StorageManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Gestionnaire de la liste de courses.
 * Cette classe centralise toutes les opérations sur la liste des articles
 * en utilisant des stratégies pour les différentes opérations.
 */
public class GroceryManager {
    private List<GroceryItem> items;
    private final StorageManager storageManager;
    private final CategoryManager categoryManager;
    
    // Stratégies pour les opérations sur les articles
    private final ItemOperationStrategy addStrategy;
    private final ItemOperationStrategy removeStrategy;
    private final ItemOperationStrategy updateStrategy;

    /**
     * Construit un gestionnaire de liste de courses avec le gestionnaire de stockage spécifié.
     *
     * @param storageManager le gestionnaire de stockage à utiliser
     */
    public GroceryManager(StorageManager storageManager) {
        this.items = new ArrayList<>();
        this.storageManager = storageManager;
        this.categoryManager = new CategoryManager();
        
        // Initialiser les stratégies
        this.addStrategy = new AddItemStrategy();
        this.removeStrategy = new RemoveItemStrategy();
        this.updateStrategy = new UpdateItemStrategy();
    }

    /**
     * Charge la liste de courses à partir d'un fichier.
     *
     * @param fileName le nom du fichier à charger
     * @throws IOException en cas d'erreur de lecture
     */
    public void loadGroceryList(String fileName) throws IOException {
        List<GroceryItem> loadedItems = storageManager.loadGroceryList(fileName);
        
        items.clear();
        items.addAll(loadedItems);
        
        // Mettre à jour les catégories
        loadedItems.forEach(item -> 
            categoryManager.addItemToCategory(item.getName(), item.getCategory())
        );
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
     * @throws IllegalArgumentException si les paramètres sont invalides
     * @throws IOException en cas d'erreur lors de la mise à jour
     */
    public void addItem(String name, int quantity, String category) throws IllegalArgumentException, IOException {
        try {
            items = addStrategy.execute(items, name, quantity, category);
            categoryManager.addItemToCategory(name, category);
        } catch (IllegalArgumentException e) {
            throw e; // Propager directement les exceptions de validation
        } catch (Exception e) {
            throw new IOException("Erreur lors de l'ajout de l'article: " + e.getMessage(), e);
        }
    }

    /**
     * Supprime un article de la liste de courses.
     *
     * @param name le nom de l'article à supprimer
     * @throws Exception si l'article n'existe pas
     */
    public void removeItem(String name) throws Exception {
        items = removeStrategy.execute(items, name, 0, null);
        categoryManager.removeItem(name);
    }

    /**
     * Met à jour un article existant.
     *
     * @param name le nom de l'article
     * @param newQuantity la nouvelle quantité (0 pour conserver l'ancienne)
     * @param newCategory la nouvelle catégorie (null pour conserver l'ancienne)
     * @throws Exception si l'article n'existe pas
     */
    public void updateItem(String name, int newQuantity, String newCategory) throws Exception {
        items = updateStrategy.execute(items, name, newQuantity, newCategory);
        
        if (newCategory != null) {
            categoryManager.addItemToCategory(name, newCategory);
        }
    }

    /**
     * Vérifie si un article existe dans la liste.
     *
     * @param name le nom de l'article à vérifier
     * @return true si l'article existe, false sinon
     */
    public boolean doesItemExist(String name) {
        return getItemByName(name) != null;
    }

    /**
     * Récupère la quantité d'un article.
     *
     * @param name le nom de l'article
     * @return la quantité de l'article, ou 0 s'il n'existe pas
     */
    public int getItemQuantity(String name) {
        GroceryItem item = getItemByName(name);
        return item != null ? item.getQuantity() : 0;
    }

    /**
     * Récupère la catégorie d'un article.
     *
     * @param name le nom de l'article
     * @return la catégorie de l'article, ou "default" s'il n'existe pas
     */
    public String getItemCategory(String name) {
        GroceryItem item = getItemByName(name);
        return item != null ? item.getCategory() : "default";
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
        Map<String, List<GroceryItem>> itemsByCategory = items.stream()
                .collect(Collectors.groupingBy(GroceryItem::getCategory));
        
        Map<String, List<String>> result = new HashMap<>();
        itemsByCategory.forEach((category, categoryItems) -> {
            List<String> formattedItems = categoryItems.stream()
                    .map(GroceryItem::toString)
                    .collect(Collectors.toList());
            
            result.put(category, formattedItems);
        });
        
        return result;
    }

    /**
     * Récupère la liste complète des articles.
     *
     * @return la liste de tous les articles
     */
    public List<GroceryItem> getItems() {
        return new ArrayList<>(items);
    }
} 