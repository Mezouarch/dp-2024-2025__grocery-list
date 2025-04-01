package com.fges;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Gestionnaire de catégories pour la liste de courses.
 * Cette classe centralise la gestion des catégories et leurs articles associés.
 */
public class CategoryManager {
    private final Map<String, List<String>> categories;

    public CategoryManager() {
        this.categories = new HashMap<>();
    }

    /**
     * Ajoute un article à une catégorie.
     *
     * @param category la catégorie
     * @param item l'article à ajouter
     */
    public void addItemToCategory(String category, String item) {
        String normalizedCategory = normalizeCategory(category);
        categories.computeIfAbsent(normalizedCategory, k -> new ArrayList<>())
                 .add(item);
    }

    /**
     * Normalise le nom d'une catégorie.
     *
     * @param category la catégorie à normaliser
     * @return la catégorie normalisée
     */
    private String normalizeCategory(String category) {
        return category != null && !category.trim().isEmpty() 
            ? category.trim() 
            : "default";
    }

    /**
     * Vérifie si une catégorie existe.
     *
     * @param category la catégorie à vérifier
     * @return true si la catégorie existe
     */
    public boolean categoryExists(String category) {
        return categories.containsKey(normalizeCategory(category));
    }

    /**
     * Récupère tous les articles d'une catégorie.
     *
     * @param category la catégorie
     * @return la liste des articles de la catégorie
     */
    public List<String> getItemsInCategory(String category) {
        return categories.getOrDefault(normalizeCategory(category), new ArrayList<>());
    }

    /**
     * Récupère toutes les catégories.
     *
     * @return la map des catégories et leurs articles
     */
    public Map<String, List<String>> getAllCategories() {
        return new HashMap<>(categories);
    }

    /**
     * Supprime un article d'une catégorie.
     *
     * @param category la catégorie
     * @param item l'article à supprimer
     */
    public void removeItemFromCategory(String category, String item) {
        String normalizedCategory = normalizeCategory(category);
        List<String> items = categories.get(normalizedCategory);
        if (items != null) {
            items.remove(item);
            if (items.isEmpty()) {
                categories.remove(normalizedCategory);
            }
        }
    }
} 