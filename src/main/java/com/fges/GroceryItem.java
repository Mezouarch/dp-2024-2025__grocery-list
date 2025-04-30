package com.fges;

/**
 * Représente un article de la liste de courses.
 */
public class GroceryItem {
    private String name;
    private int quantity;
    private String category;

    /**
     * Constructeur pour un article avec toutes les propriétés.
     *
     * @param name     Le nom de l'article
     * @param quantity La quantité de l'article
     * @param category La catégorie de l'article
     */
    public GroceryItem(String name, int quantity, String category) {
        this.name = name;
        this.quantity = quantity;
        this.category = category;
    }

    /**
     * Constructeur par défaut.
     */
    public GroceryItem() {
        this("", 0, "");
    }

    /**
     * Obtient le nom de l'article.
     *
     * @return Le nom de l'article
     */
    public String getName() {
        return name;
    }

    /**
     * Définit le nom de l'article.
     *
     * @param name Le nom de l'article
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtient la quantité de l'article.
     *
     * @return La quantité de l'article
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Définit la quantité de l'article.
     *
     * @param quantity La quantité de l'article
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Obtient la catégorie de l'article.
     *
     * @return La catégorie de l'article
     */
    public String getCategory() {
        return category;
    }

    /**
     * Définit la catégorie de l'article.
     *
     * @param category La catégorie de l'article
     */
    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return String.format("%s (quantité: %d, catégorie: %s)", name, quantity, category);
    }
}