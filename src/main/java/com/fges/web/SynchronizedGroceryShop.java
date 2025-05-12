package com.fges.web;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fges.model.GroceryManager;

import fr.anthonyquere.MyGroceryShop;

/**
 * Implémentation de MyGroceryShop qui synchronise les modifications
 * avec le GroceryManager sous-jacent.
 */
public class SynchronizedGroceryShop implements MyGroceryShop {
    private final List<WebGroceryItem> groceries = new ArrayList<>();
    private final GroceryManager groceryManager;

    /**
     * Construit une nouvelle instance avec le gestionnaire fourni.
     * Charge initialement les articles depuis le gestionnaire.
     *
     * @param groceryManager le gestionnaire de liste de courses
     */
    public SynchronizedGroceryShop(GroceryManager groceryManager) {
        this.groceryManager = groceryManager;
        initializeFromGroceryManager();
    }

    /**
     * Initialise les articles depuis le gestionnaire.
     */
    private void initializeFromGroceryManager() {
        // Récupérer les articles par catégorie
        Map<String, List<String>> itemsByCategory = groceryManager.getGroceryListByCategory();
        
        // Parcourir les articles et les ajouter à la liste locale
        for (Map.Entry<String, List<String>> categoryEntry : itemsByCategory.entrySet()) {
            String categoryName = categoryEntry.getKey();
            List<String> items = categoryEntry.getValue();
            
            for (String itemString : items) {
                // Analyser la chaîne au format "nom: quantité"
                String[] parts = itemString.split(":");
                if (parts.length >= 2) {
                    String itemName = parts[0].trim();
                    int quantity = Integer.parseInt(parts[1].trim());
                    
                    // Ajouter l'article à la liste locale sans synchroniser
                    addItemWithoutSync(itemName, quantity, categoryName);
                }
            }
        }
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