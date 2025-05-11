package com.fges.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

/**
 * Classe gérant les catégories d'articles.
 * Permet de regrouper et organiser les articles par catégorie.
 */
public class CategoryManager {
    private final Map<String, Set<String>> itemsByCategory;
    private final Map<String, String> categoriesByItem;

    /**
     * Constructeur initialisant les structures de données.
     */
    public CategoryManager() {
        // LinkedHashMap pour conserver l'ordre d'insertion
        this.itemsByCategory = new LinkedHashMap<>();
        this.categoriesByItem = new HashMap<>();
        
        // Ajouter la catégorie par défaut
        this.itemsByCategory.put("default", new HashSet<>());
    }

    /**
     * Ajoute un article à une catégorie.
     *
     * @param itemName le nom de l'article
     * @param category la catégorie (si null, utilise la catégorie par défaut)
     */
    public void addItemToCategory(String itemName, String category) {
        String actualCategory = (category != null && !category.trim().isEmpty()) ? category : "default";
        
        // Créer la catégorie si elle n'existe pas encore
        itemsByCategory.computeIfAbsent(actualCategory, k -> new HashSet<>());
        
        // Supprimer l'article de son ancienne catégorie si nécessaire
        if (categoriesByItem.containsKey(itemName)) {
            String oldCategory = categoriesByItem.get(itemName);
            if (!oldCategory.equals(actualCategory) && itemsByCategory.containsKey(oldCategory)) {
                itemsByCategory.get(oldCategory).remove(itemName);
                
                // Supprimer la catégorie si elle est vide et n'est pas la catégorie par défaut
                if (itemsByCategory.get(oldCategory).isEmpty() && !oldCategory.equals("default")) {
                    itemsByCategory.remove(oldCategory);
                }
            }
        }
        
        // Ajouter l'article à la nouvelle catégorie
        itemsByCategory.get(actualCategory).add(itemName);
        categoriesByItem.put(itemName, actualCategory);
    }

    /**
     * Supprime un article de sa catégorie.
     *
     * @param itemName le nom de l'article
     */
    public void removeItem(String itemName) {
        if (categoriesByItem.containsKey(itemName)) {
            String category = categoriesByItem.get(itemName);
            if (itemsByCategory.containsKey(category)) {
                itemsByCategory.get(category).remove(itemName);
                
                // Supprimer la catégorie si elle est vide et n'est pas la catégorie par défaut
                if (itemsByCategory.get(category).isEmpty() && !category.equals("default")) {
                    itemsByCategory.remove(category);
                }
            }
            categoriesByItem.remove(itemName);
        }
    }

    /**
     * Vérifie si une catégorie existe.
     *
     * @param category le nom de la catégorie
     * @return true si la catégorie existe, false sinon
     */
    public boolean categoryExists(String category) {
        return itemsByCategory.containsKey(category);
    }

    /**
     * Récupère la catégorie d'un article.
     *
     * @param itemName le nom de l'article
     * @return la catégorie de l'article ou "default" si non trouvé
     */
    public String getItemCategory(String itemName) {
        return categoriesByItem.getOrDefault(itemName, "default");
    }

    /**
     * Récupère tous les articles d'une catégorie.
     *
     * @param category le nom de la catégorie
     * @return la liste des noms d'articles dans cette catégorie
     */
    public List<String> getItemsInCategory(String category) {
        if (itemsByCategory.containsKey(category)) {
            return new ArrayList<>(itemsByCategory.get(category));
        }
        return new ArrayList<>();
    }

    /**
     * Récupère toutes les catégories avec leurs articles.
     *
     * @return un mapping des catégories avec leurs articles
     */
    public Map<String, Set<String>> getAllCategories() {
        return new LinkedHashMap<>(itemsByCategory);
    }

    /**
     * Vérifie si un article existe dans n'importe quelle catégorie.
     *
     * @param itemName le nom de l'article
     * @return true si l'article existe, false sinon
     */
    public boolean containsItem(String itemName) {
        return categoriesByItem.containsKey(itemName);
    }
} 