package com.fges.model.strategies;

import com.fges.model.GroceryItem;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Stratégie pour supprimer un article de la liste de courses.
 */
public class RemoveItemStrategy implements ItemOperationStrategy {
    
    @Override
    public List<GroceryItem> execute(List<GroceryItem> items, String itemName, int quantity, String category) throws Exception {
        validateItemExists(items, itemName);
        
        if (category != null) {
            validateItemInCategory(items, itemName, category);
        }
        
        // Filtrer l'article à supprimer
        return items.stream()
                .filter(item -> !item.getName().equals(itemName))
                .collect(Collectors.toList());
    }
    
    /**
     * Vérifie que l'article existe.
     */
    private void validateItemExists(List<GroceryItem> items, String itemName) throws Exception {
        Optional<GroceryItem> item = items.stream()
                .filter(i -> i.getName().equals(itemName))
                .findFirst();
                
        if (!item.isPresent()) {
            throw new Exception("Article non trouvé : " + itemName);
        }
    }
    
    /**
     * Vérifie que l'article appartient à la catégorie spécifiée.
     */
    private void validateItemInCategory(List<GroceryItem> items, String itemName, String category) throws Exception {
        boolean itemInCategory = items.stream()
                .anyMatch(item -> item.getName().equals(itemName) && 
                         item.getCategory().equals(category));
        
        if (!itemInCategory) {
            throw new Exception("L'article '" + itemName + "' n'existe pas dans la catégorie '" + category + "'");
        }
    }
} 