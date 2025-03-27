package com.fges;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implémentation de StorageManager pour le format JSON.
 */
public class JsonStorageManager implements StorageManager {
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public Map<String, Integer> loadGroceryList(String fileName) throws IOException {
        Path filePath = Paths.get(fileName);
        Map<String, Integer> groceryMap = new HashMap<>();
        
        if (Files.exists(filePath)) {
            // Lire le contenu du fichier JSON
            String fileContent = Files.readString(filePath);
            List<String> loadedList = OBJECT_MAPPER.readValue(fileContent, new TypeReference<List<String>>() {});
            
            // Traiter les entrées et consolider la liste
            processEntries(loadedList, groceryMap);
        }
        
        return groceryMap;
    }

    @Override
    public void saveGroceryList(String fileName, Map<String, Integer> groceryMap) throws IOException {
        List<String> listToSave = groceryMap.entrySet().stream()
            .map(entry -> entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.toList());
        
        OBJECT_MAPPER.writeValue(new File(fileName), listToSave);
    }
    
    /**
     * Traite les entrées de la liste chargée et consolide les quantités.
     * 
     * @param loadedList la liste chargée depuis le fichier
     * @param groceryMap la map à remplir
     */
    private void processEntries(List<String> loadedList, Map<String, Integer> groceryMap) {
        Map<String, Integer> tempMap = new HashMap<>();
        Map<String, String> originalNames = new HashMap<>();
        
        for (String item : loadedList) {
            String[] parts = item.split(": ");
            String itemName = parts[0].trim();
            String itemNameLower = itemName.toLowerCase();
            int quantity = Integer.parseInt(parts[1]);
            
            // Garder le premier nom d'article rencontré (avec sa casse originale)
            originalNames.putIfAbsent(itemNameLower, itemName);
            
            // Additionner les quantités dans la map temporaire
            tempMap.merge(itemNameLower, quantity, Integer::sum);
        }
        
        // Mettre à jour la map principale avec les noms originaux
        for (Map.Entry<String, Integer> entry : tempMap.entrySet()) {
            groceryMap.put(originalNames.get(entry.getKey()), entry.getValue());
        }
    }
} 