package com.fges.storage;

import com.fges.model.GroceryItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestionnaire de stockage pour le format JSON.
 */
public class JsonStorageManager implements StorageManager {
    private final Gson gson;
    private final Type itemsListType;

    /**
     * Construit un nouveau gestionnaire de stockage JSON.
     */
    public JsonStorageManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.itemsListType = new TypeToken<List<GroceryItem>>(){}.getType();
    }

    @Override
    public void saveGroceryList(List<GroceryItem> items, String fileName) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Pour la rétrocompatibilité avec l'ancien format qui n'avait pas de catégories,
            // nous devons gérer les catégories explicitement
            if (containsCategories(items)) {
                // Nouveau format avec catégories
                gson.toJson(items, writer);
            } else {
                // Ancien format: liste de paires nom-quantité
                Map<String, Integer> simpleItems = new HashMap<>();
                for (GroceryItem item : items) {
                    simpleItems.put(item.getName(), item.getQuantity());
                }
                gson.toJson(simpleItems, writer);
            }
        }
    }

    @Override
    public List<GroceryItem> loadGroceryList(String fileName) throws IOException {
        try (FileReader reader = new FileReader(fileName)) {
            // Essayer d'abord de charger au nouveau format (liste de GroceryItem)
            try {
                List<GroceryItem> items = gson.fromJson(reader, itemsListType);
                if (items != null) {
                    return items;
                }
                return new ArrayList<>();
            } catch (Exception e) {
                // Si ça échoue, essayer l'ancien format (Map<String, Integer>)
                try (FileReader readerRetry = new FileReader(fileName)) {
                    Type mapType = new TypeToken<Map<String, Integer>>(){}.getType();
                    Map<String, Integer> simpleItems = gson.fromJson(readerRetry, mapType);
                    
                    if (simpleItems == null) {
                        return new ArrayList<>();
                    }
                    
                    List<GroceryItem> items = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry : simpleItems.entrySet()) {
                        items.add(new GroceryItem(entry.getKey(), entry.getValue()));
                    }
                    return items;
                } catch (Exception ex) {
                    // Si les deux formats échouent, retourner une liste vide
                    return new ArrayList<>();
                }
            }
        }
    }

    /**
     * Vérifie si la liste contient des articles avec des catégories autres que la catégorie par défaut.
     *
     * @param items la liste d'articles à vérifier
     * @return true si au moins un article a une catégorie différente de "default"
     */
    private boolean containsCategories(List<GroceryItem> items) {
        for (GroceryItem item : items) {
            if (!"default".equals(item.getCategory())) {
                return true;
            }
        }
        return false;
    }
} 