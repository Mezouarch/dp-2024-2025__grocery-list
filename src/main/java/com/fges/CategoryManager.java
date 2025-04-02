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
    private final Map<String, Map<String, Integer>> categories;

    public CategoryManager() {
        this.categories = new HashMap<>();
    }

    /**
     * Ajoute un article à une catégorie.
     *
     * @param category la catégorie
     * @param item l'article à ajouter
     * @param quantity la quantité de l'article
     */
    public void addItemToCategory(String category, String item, int quantity) {
        String normalizedCategory = normalizeCategory(category);
        categories.computeIfAbsent(normalizedCategory, k -> new HashMap<>())
                 .put(item, quantity);
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
     * @return la liste des articles de la catégorie au format "nom: quantité"
     */
    public List<String> getItemsInCategory(String category) {
        List<String> result = new ArrayList<>();
        Map<String, Integer> items = categories.getOrDefault(normalizeCategory(category), new HashMap<>());
        items.forEach((item, quantity) -> result.add(item + ": " + quantity));
        return result;
    }

    /**
     * Récupère toutes les catégories.
     *
     * @return la map des catégories et leurs articles
     */
    public Map<String, Map<String, Integer>> getAllCategories() {
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
        Map<String, Integer> items = categories.get(normalizedCategory);
        if (items != null) {
            items.remove(item);
            // Si la catégorie est vide, la supprimer
            if (items.isEmpty()) {
                categories.remove(normalizedCategory);
            }
        }
    }

    /**
     * Supprime une catégorie vide.
     *
     * @param category la catégorie à supprimer
     */
    public void removeCategory(String category) {
        String normalizedCategory = normalizeCategory(category);
        Map<String, Integer> items = categories.get(normalizedCategory);
        if (items != null && items.isEmpty()) {
            categories.remove(normalizedCategory);
        }
    }
} 