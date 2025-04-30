package com.fges;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import fr.anthonyquere.MyGroceryShop;

/**
 * Implémentation de MyGroceryShop qui synchronise les modifications
 * avec le GroceryManager sous-jacent.
 */
public class SynchronizedGroceryShop implements MyGroceryShop {
    private final List<WebGroceryItem> groceries = new ArrayList<>();
    private final GroceryManager groceryManager;

    public SynchronizedGroceryShop(GroceryManager groceryManager) {
        this.groceryManager = groceryManager;
    }

    @Override
    public List<WebGroceryItem> getGroceries() {
        return new ArrayList<>(groceries);
    }

    /**
     * Ajoute un article au magasin sans synchroniser avec le GroceryManager.
     * Utilisé uniquement lors de l'initialisation.
     */
    public void addItemWithoutSync(String name, int quantity, String category) {
        groceries.add(new WebGroceryItem(name, quantity, category));
    }

    @Override
    public void addGroceryItem(String name, int quantity, String category) {
        // Si la catégorie est null ou vide, utilisez "default"
        if (category == null || category.trim().isEmpty()) {
            category = "default";
        }

        // Vérifier si l'élément existe déjà
        boolean exists = false;
        for (WebGroceryItem item : groceries) {
            if (item.name().equals(name)) {
                exists = true;
                // Modifier l'élément existant pour ajouter la quantité
                int newQuantity = item.quantity() + quantity;
                
                // Supprimer l'ancien élément
                groceries.remove(item);
                
                // Ajouter l'élément avec la nouvelle quantité
                groceries.add(new WebGroceryItem(name, newQuantity, item.category()));
                
                // Synchroniser avec le GroceryManager
                try {
                    // Supprimer d'abord l'ancien élément
                    groceryManager.removeItem(name);
                    
                    // Puis ajouter l'élément avec la nouvelle quantité
                    groceryManager.addItem(name, newQuantity, item.category());
                    
                    System.out.println("Article mis à jour et synchronisé: " + name + " (" + newQuantity + ") dans " + item.category());
                } catch (Exception e) {
                    System.err.println("Erreur lors de la synchronisation de la mise à jour: " + e.getMessage());
                }
                
                break;
            }
        }

        if (!exists) {
            // Ajouter à la liste locale
            groceries.add(new WebGroceryItem(name, quantity, category));

            // Synchroniser avec le GroceryManager
            try {
                groceryManager.addItem(name, quantity, category);
                System.out.println("Article ajouté et synchronisé: " + name + " (" + quantity + ") dans " + category);
            } catch (IOException e) {
                System.err.println("Erreur lors de la synchronisation de l'ajout: " + e.getMessage());
            }
        }
    }

    @Override
    public void removeGroceryItem(String name) {
        // Supprimer de la liste locale
        groceries.removeIf(item -> item.name().equals(name));

        // Synchroniser avec le GroceryManager
        try {
            groceryManager.removeItem(name);
            System.out.println("Article supprimé et synchronisé: " + name);
        } catch (Exception e) {
            System.err.println("Erreur lors de la synchronisation de la suppression: " + e.getMessage());
        }
    }

    @Override
    public Runtime getRuntime() {
        return new Runtime(
            LocalDate.now(),
            System.getProperty("java.version"),
            System.getProperty("os.name")
        );
    }
}