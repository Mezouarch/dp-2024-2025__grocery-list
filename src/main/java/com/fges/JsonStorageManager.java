package com.fges;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonStorageManager implements StorageManager {
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public Map<String, Integer> loadGroceryList(String fileName) throws IOException {
        Map<String, Integer> result = new HashMap<>();
        Map<String, GroceryManager.ItemInfo> itemsWithCategories = loadWithCategories(fileName);
        
        itemsWithCategories.forEach((name, info) -> 
            result.put(name, info.getQuantity()));
        
        return result;
    }

    @Override
    public void saveGroceryList(String fileName, Map<String, Integer> groceryMap) throws IOException {
        Map<String, GroceryManager.ItemInfo> itemsWithCategories = new HashMap<>();
        groceryMap.forEach((name, quantity) -> 
            itemsWithCategories.put(name, new GroceryManager.ItemInfo(quantity, "default")));
        
        saveWithCategories(fileName, itemsWithCategories);
    }

    // Changé de private à package-private (pas de modificateur)
    Map<String, GroceryManager.ItemInfo> loadWithCategories(String fileName) throws IOException {
        Map<String, GroceryManager.ItemInfo> result = new HashMap<>();
        
        try {
            Map<String, Map<String, List<String>>> categoryMap = 
                OBJECT_MAPPER.readValue(new File(fileName), new TypeReference<>() {});
            
            for (Map.Entry<String, Map<String, List<String>>> categoryEntry : categoryMap.entrySet()) {
                String category = categoryEntry.getKey();
                Map<String, List<String>> items = categoryEntry.getValue();
                
                for (Map.Entry<String, List<String>> itemEntry : items.entrySet()) {
                    String itemName = itemEntry.getKey();
                    List<String> quantities = itemEntry.getValue();
                    if (!quantities.isEmpty()) {
                        int quantity = Integer.parseInt(quantities.get(0));
                        result.put(itemName, new GroceryManager.ItemInfo(quantity, category));
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException("Erreur lors du chargement du fichier JSON: " + fileName, e);
        }
        
        return result;
    }

    // Changé de private à package-private (pas de modificateur)
    void saveWithCategories(String fileName, Map<String, GroceryManager.ItemInfo> groceryItems) throws IOException {
        Map<String, Map<String, List<String>>> categoryMap = new HashMap<>();
        
        for (Map.Entry<String, GroceryManager.ItemInfo> entry : groceryItems.entrySet()) {
            String itemName = entry.getKey();
            GroceryManager.ItemInfo info = entry.getValue();
            String category = info.getCategory() != null ? info.getCategory().trim() : "default";
            
            // S'assurer que la catégorie n'est pas vide
            if (category.isEmpty()) {
                category = "default";
            }
            
            categoryMap.computeIfAbsent(category, k -> new HashMap<>())
                      .put(itemName, Collections.singletonList(String.valueOf(info.getQuantity())));
        }
        
        File file = new File(fileName);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        
        OBJECT_MAPPER.writeValue(file, categoryMap);
    }
}