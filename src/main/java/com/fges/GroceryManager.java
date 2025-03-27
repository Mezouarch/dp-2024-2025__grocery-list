package com.fges;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class GroceryManager {
    // Classe interne pour le format JSON (reste inchangée)
    private static class GroceryItem {
        public String name;
        public int quantity;

        public GroceryItem() {}

        public GroceryItem(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }
    }

    private Map<String, Integer> groceryItems = new HashMap<>();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Méthodes existantes de chargement et sauvegarde (restent inchangées)
    public void loadGroceryList(String fileName) throws IOException {
        Path filePath = Paths.get(fileName);
        if (Files.exists(filePath)) {
            List<GroceryItem> items = OBJECT_MAPPER.readValue(
                filePath.toFile(),
                new TypeReference<List<GroceryItem>>() {}
            );
            
            groceryItems.clear();
            for (GroceryItem item : items) {
                groceryItems.put(item.name, item.quantity);
            }
        } else {
            groceryItems.clear();
        }
    }

    public void saveGroceryList(String fileName) throws IOException {
        List<GroceryItem> itemsToSave = groceryItems.entrySet().stream()
            .filter(entry -> entry.getValue() > 0)
            .map(entry -> new GroceryItem(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
        
        OBJECT_MAPPER.writeValue(new File(fileName), itemsToSave);
    }

    // Méthodes de gestion des articles
    public void addItem(String itemName, int quantity) {
        groceryItems.put(itemName, 
            groceryItems.getOrDefault(itemName, 0) + quantity);
        
        if (groceryItems.getOrDefault(itemName, 0) <= 0) {
            groceryItems.remove(itemName);
        }
    }

    // Nouvelle méthode pour vérifier l'existence d'un article
    public boolean doesItemExist(String itemName) {
        return groceryItems.containsKey(itemName) && groceryItems.get(itemName) > 0;
    }

    // Nouvelle méthode pour obtenir la quantité d'un article
    public int getItemQuantity(String itemName) {
        return groceryItems.getOrDefault(itemName, 0);
    }

    public List<String> getGroceryList() {
        return groceryItems.entrySet().stream()
            .filter(entry -> entry.getValue() > 0)
            .map(entry -> entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.toList());
    }

    public void removeItem(String itemName) throws Exception {
        if (!groceryItems.containsKey(itemName)) {
            throw new Exception("L'article " + itemName + " n'existe pas dans la liste.");
        }
        
        int currentQuantity = groceryItems.get(itemName);
        if (currentQuantity > 1) {
            groceryItems.put(itemName, currentQuantity - 1);
        } else {
            groceryItems.remove(itemName);
        }
    }
}