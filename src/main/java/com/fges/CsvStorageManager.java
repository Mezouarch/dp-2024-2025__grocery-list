package com.fges;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Implémentation de StorageManager pour le format CSV.
 */
public class CsvStorageManager implements StorageManager {
    
    private static final String CSV_HEADER = "item,quantity";
    private static final String CSV_HEADER_WITH_CATEGORY = "item,quantity,category";
    private static final String CSV_SEPARATOR = ",";

    @Override
    public Map<String, Integer> loadGroceryList(String fileName) throws IOException {
        Path filePath = Paths.get(fileName);
        Map<String, Integer> groceryMap = new HashMap<>();
        
        if (Files.exists(filePath)) {
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                // Vérifier l'en-tête
                String header = reader.readLine();
                if (header == null) {
                    return groceryMap;
                }
                
                // Vérifier format
                boolean hasCategory = header.equals(CSV_HEADER_WITH_CATEGORY);
                if (!hasCategory && !header.equals(CSV_HEADER)) {
                    throw new IOException("Format CSV invalide : en-tête incorrect");
                }
                
                // Lire et traiter chaque ligne
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(CSV_SEPARATOR);
                    if (parts.length < 2) {
                        continue; // Ignorer les lignes mal formatées
                    }
                    
                    try {
                        String itemName = parts[0].trim();
                        int quantity = Integer.parseInt(parts[1].trim());
                        
                        // On ignore la catégorie dans cette implémentation simplifiée 
                        // puisque l'interface StorageManager utilise Map<String, Integer>
                        groceryMap.put(itemName, quantity);
                    } catch (NumberFormatException e) {
                        // Ignorer les lignes avec des quantités non numériques
                        System.err.println("Ignorer ligne mal formatée: " + line);
                    }
                }
            }
        }
        
        return groceryMap;
    }
    
    @Override
    public void saveGroceryList(String fileName, Map<String, Integer> groceryMap) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            // Écrire l'en-tête standard (sans catégorie)
            writer.println(CSV_HEADER);
            
            // Écrire les données
            for (Map.Entry<String, Integer> entry : groceryMap.entrySet()) {
                writer.printf("%s,%d%n", 
                    entry.getKey(),
                    entry.getValue());
            }
        }
    }
    
    /**
     * Lit et traite le fichier CSV pour extraire les articles et leurs quantités.
     * 
     * @param filePath le chemin du fichier CSV
     * @param groceryMap la map à remplir
     * @throws IOException en cas d'erreur lors de la lecture
     */
    private void readAndProcessCsvFile(Path filePath, Map<String, Integer> groceryMap) throws IOException {
        Map<String, String> originalNames = new HashMap<>();
        Map<String, Integer> tempMap = new HashMap<>();
        
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            // Vérifier l'en-tête
            String header = reader.readLine();
            if (header == null || (!header.equals(CSV_HEADER) && !header.equals(CSV_HEADER_WITH_CATEGORY))) {
                throw new IOException("Format CSV invalide : en-tête manquant ou incorrect");
            }
            
            // Lire et traiter chaque ligne
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line, originalNames, tempMap);
            }
        }
        
        // Mettre à jour la map principale avec les noms originaux
        for (Map.Entry<String, Integer> entry : tempMap.entrySet()) {
            groceryMap.put(originalNames.get(entry.getKey()), entry.getValue());
        }
    }
    
    /**
     * Traite une ligne du fichier CSV.
     * 
     * @param line la ligne à traiter
     * @param originalNames la map des noms originaux
     * @param tempMap la map temporaire pour les quantités
     * @throws IOException si le format de la ligne est invalide
     */
    private void processLine(String line, Map<String, String> originalNames, Map<String, Integer> tempMap) 
            throws IOException {
        String[] parts = line.split(CSV_SEPARATOR);
        if (parts.length < 2) {
            throw new IOException("Format CSV invalide : chaque ligne doit avoir au moins 2 colonnes");
        }
        
        String itemName = parts[0].trim();
        String itemNameLower = itemName.toLowerCase();
        
        try {
            int quantity = Integer.parseInt(parts[1].trim());
            
            // Garder le premier nom d'article rencontré (avec sa casse originale)
            originalNames.putIfAbsent(itemNameLower, itemName);
            
            // Additionner les quantités dans la map temporaire
            tempMap.merge(itemNameLower, quantity, Integer::sum);
        } catch (NumberFormatException e) {
            throw new IOException("Format CSV invalide : la quantité doit être un nombre entier");
        }
    }
}