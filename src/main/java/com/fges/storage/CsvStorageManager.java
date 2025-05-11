package com.fges.storage;

import com.fges.model.GroceryItem;
import com.fges.util.MessageFormatter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestionnaire de stockage pour le format CSV.
 */
public class CsvStorageManager implements StorageManager {
    // Constants
    private static final String HEADER = "name,quantity,category";
    private static final String SEPARATOR = ",";
    private static final int NAME_INDEX = 0;
    private static final int QUANTITY_INDEX = 1;
    private static final int CATEGORY_INDEX = 2;
    private static final String DEFAULT_CATEGORY = "default";

    @Override
    public void saveGroceryList(List<GroceryItem> items, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Écrire l'en-tête
            writer.write(HEADER);
            writer.newLine();

            // Écrire les articles
            for (GroceryItem item : items) {
                String line = String.join(SEPARATOR,
                        escapeCSV(item.getName()),
                        String.valueOf(item.getQuantity()),
                        escapeCSV(item.getCategory()));
                writer.write(line);
                writer.newLine();
            }
        }
    }

    @Override
    public List<GroceryItem> loadGroceryList(String fileName) throws IOException {
        List<GroceryItem> items = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line = reader.readLine();
            
            // Si le fichier est vide, retourner une liste vide
            if (line == null) {
                return items;
            }
            
            // Vérifier si nous avons un fichier au nouveau format avec en-tête
            boolean isNewFormat = HEADER.equalsIgnoreCase(line.trim());
            
            // Si c'est l'ancien format, revenir au début du fichier
            if (!isNewFormat) {
                reader.close();
                return loadOldFormat(fileName);
            }
            
            // Lire les articles
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                
                // Analyser la ligne
                String[] parts = line.split(SEPARATOR, -1); // -1 pour garder les champs vides
                
                if (parts.length >= 3) {
                    String name = unescapeCSV(parts[NAME_INDEX]);
                    int quantity;
                    try {
                        quantity = Integer.parseInt(parts[QUANTITY_INDEX]);
                    } catch (NumberFormatException e) {
                        // Ignorer les articles avec des quantités non valides
                        continue;
                    }
                    
                    String category = parts.length > CATEGORY_INDEX && !parts[CATEGORY_INDEX].isEmpty() 
                            ? unescapeCSV(parts[CATEGORY_INDEX]) 
                            : DEFAULT_CATEGORY;
                    
                    items.add(new GroceryItem(name, quantity, category));
                }
            }
        } catch (IOException e) {
            // Si une erreur survient, essayer de charger au format ancien
            return loadOldFormat(fileName);
        }
        
        return items;
    }
    
    /**
     * Charge la liste de courses à partir d'un fichier CSV au format ancien (sans en-tête).
     *
     * @param fileName le nom du fichier
     * @return la liste des articles
     * @throws IOException en cas d'erreur de lecture
     */
    private List<GroceryItem> loadOldFormat(String fileName) throws IOException {
        List<GroceryItem> items = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                
                String[] parts = line.split(SEPARATOR, 2);
                if (parts.length >= 2) {
                    String name = unescapeCSV(parts[0]);
                    int quantity;
                    try {
                        quantity = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        // Ignorer les articles avec des quantités non valides
                        continue;
                    }
                    
                    items.add(new GroceryItem(name, quantity));
                }
            }
        }
        
        return items;
    }
    
    /**
     * Échappe les caractères spéciaux pour le format CSV.
     *
     * @param text le texte à échapper
     * @return le texte échappé
     */
    private String escapeCSV(String text) {
        if (text == null) {
            return "";
        }
        
        if (text.contains("\"") || text.contains(",") || text.contains("\n")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        
        return text;
    }
    
    /**
     * Déséchappe les caractères spéciaux du format CSV.
     *
     * @param text le texte à désêchapper
     * @return le texte désêchappé
     */
    private String unescapeCSV(String text) {
        if (text == null) {
            return "";
        }
        
        if (text.startsWith("\"") && text.endsWith("\"")) {
            text = text.substring(1, text.length() - 1);
            text = text.replace("\"\"", "\"");
        }
        
        return text;
    }
} 