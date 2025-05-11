package com.fges.web;

import com.fges.model.GroceryManager;
import com.fges.model.WebGroceryItem;

import fr.anthonyquere.MyGroceryShop;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation simple de l'interface de magasin pour le serveur web.
 */
public class SimpleGroceryShop implements MyGroceryShop {
    private final GroceryManager groceryManager;
    private final List<WebGroceryItem> groceries = new ArrayList<>();

    /**
     * Construit un nouveau SimpleGroceryShop avec le gestionnaire fourni.
     *
     * @param groceryManager le gestionnaire de liste de courses
     */
    public SimpleGroceryShop(GroceryManager groceryManager) {
        this.groceryManager = groceryManager;
    }

    @Override
    public List<WebGroceryItem> getGroceries() {
        return new ArrayList<>(groceries);
    }

    @Override
    public void addGroceryItem(String name, int quantity, String category) {
        groceries.add(new WebGroceryItem(name, quantity, category));
    }

    @Override
    public void removeGroceryItem(String name) {
        groceries.removeIf(item -> item.name().equals(name));
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