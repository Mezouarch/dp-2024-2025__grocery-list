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
    private List<String> groceryList = new ArrayList<>();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Charge la liste de courses depuis un fichier
    public void loadGroceryList(String fileName) throws IOException {
        Path filePath = Paths.get(fileName);
        if (Files.exists(filePath)) {
            String fileContent = Files.readString(filePath);
            List<String> originalList = OBJECT_MAPPER.readValue(fileContent, new TypeReference<List<String>>() {});
            
            // Consolider la liste lors du chargement
            Map<String, Integer> consolidatedItems = new HashMap<>();
            for (String item : originalList) {
                String[] parts = item.split(": ");
                String itemName = parts[0].trim();
                int quantity = Integer.parseInt(parts[1]);
                
                consolidatedItems.put(itemName, 
                    consolidatedItems.getOrDefault(itemName, 0) + quantity);
            }
            
            // Convertir la map consolidée en liste
            groceryList = consolidatedItems.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.toList());
        } else {
            groceryList = new ArrayList<>();
        }
    }

    // Sauvegarde la liste de courses dans un fichier
    public void saveGroceryList(String fileName) throws IOException {
        // Pas besoin de consolider ici, car loadGroceryList le fait déjà
        OBJECT_MAPPER.writeValue(new File(fileName), groceryList);
    }

    // Ajoute ou met à jour un élément dans la liste
    public void addItem(String itemName, int quantity) {
        Optional<Integer> existingItemIndex = findItemIndex(itemName);
        
        if (existingItemIndex.isPresent()) {
            // Si l'article existe déjà, mettre à jour sa quantité
            String existingItem = groceryList.get(existingItemIndex.get());
            int currentQuantity = Integer.parseInt(existingItem.split(": ")[1]);
            int newQuantity = currentQuantity + quantity;
            
            if (newQuantity > 0) {
                groceryList.set(existingItemIndex.get(), itemName + ": " + newQuantity);
            } else {
                // Si la nouvelle quantité est 0 ou négative, supprimer l'article
                groceryList.remove(existingItemIndex.get().intValue());
            }
        } else {
            // Si l'article n'existe pas et que la quantité est positive, l'ajouter
            if (quantity > 0) {
                groceryList.add(itemName + ": " + quantity);
            }
        }
    }

    // Supprime un élément de la liste ou réduit sa quantité
    public void removeItem(String itemName) throws Exception {
        Optional<Integer> existingItemIndex = findItemIndex(itemName);
        
        if (existingItemIndex.isPresent()) {
            String existingItem = groceryList.get(existingItemIndex.get());
            int currentQuantity = Integer.parseInt(existingItem.split(": ")[1]);
            
            if (currentQuantity > 1) {
                // Réduire la quantité de 1
                groceryList.set(existingItemIndex.get(), itemName + ": " + (currentQuantity - 1));
            } else {
                // Supprimer l'article si la quantité devient 0
                groceryList.remove(existingItemIndex.get().intValue());
            }
        } else {
            throw new Exception("L'article " + itemName + " n'existe pas dans la liste.");
        }
    }

    // Trouve l'index d'un article dans la liste
    private Optional<Integer> findItemIndex(String itemName) {
        for (int i = 0; i < groceryList.size(); i++) {
            if (groceryList.get(i).split(": ")[0].trim().equalsIgnoreCase(itemName)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    // Retourne la liste complète
    public List<String> getGroceryList() {
        return groceryList;
    }
}