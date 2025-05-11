package com.fges.model;

/**
 * Classe représentant un article de la liste de courses.
 * Un article a un nom, une quantité et une catégorie.
 */
public class GroceryItem {
    private final String name;
    private final int quantity;
    private final String category;

    /**
     * Construit un nouvel article avec les valeurs spécifiées.
     *
     * @param name     le nom de l'article
     * @param quantity la quantité de l'article
     * @param category la catégorie de l'article
     */
    public GroceryItem(String name, int quantity, String category) {
        this.name = name;
        this.quantity = quantity;
        this.category = category != null ? category : "default";
    }

    /**
     * Construit un nouvel article avec une catégorie par défaut.
     *
     * @param name     le nom de l'article
     * @param quantity la quantité de l'article
     */
    public GroceryItem(String name, int quantity) {
        this(name, quantity, "default");
    }

    /**
     * Récupère le nom de l'article.
     *
     * @return le nom de l'article
     */
    public String getName() {
        return name;
    }

    /**
     * Récupère la quantité de l'article.
     *
     * @return la quantité de l'article
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Récupère la catégorie de l'article.
     *
     * @return la catégorie de l'article
     */
    public String getCategory() {
        return category;
    }

    /**
     * Crée une nouvelle instance d'article avec la quantité modifiée.
     *
     * @param newQuantity la nouvelle quantité
     * @return une nouvelle instance d'article avec la nouvelle quantité
     */
    public GroceryItem withQuantity(int newQuantity) {
        return new GroceryItem(this.name, newQuantity, this.category);
    }

    /**
     * Crée une nouvelle instance d'article avec la catégorie modifiée.
     *
     * @param newCategory la nouvelle catégorie
     * @return une nouvelle instance d'article avec la nouvelle catégorie
     */
    public GroceryItem withCategory(String newCategory) {
        return new GroceryItem(this.name, this.quantity, newCategory);
    }

    /**
     * Fournit une représentation textuelle de l'article.
     *
     * @return une chaîne représentant l'article au format "nom: quantité"
     */
    @Override
    public String toString() {
        return name + ": " + quantity;
    }
} 