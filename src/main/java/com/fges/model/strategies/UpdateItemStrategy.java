package com.fges.model.strategies;

import com.fges.model.GroceryItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Stratégie pour mettre à jour un article existant dans la liste de courses.
 */
public class UpdateItemStrategy implements ItemOperationStrategy {
    
    @Override
    public List<GroceryItem> execute(List<GroceryItem> items, String itemName, int quantity, String category) throws Exception {
        // Vérifier si l'article existe
        Optional<GroceryItem> itemOpt = findItem(items, itemName);
        
        if (!itemOpt.isPresent()) {
            throw new Exception("Article non trouvé : " + itemName);
        }
        
        // Copier la liste
        List<GroceryItem> result = new ArrayList<>(items);
        
        // Mettre à jour l'article
        updateItem(result, itemOpt.get(), quantity, category);
        
        return result;
    }
    
    /**
     * Trouve un article par son nom.
     */
    private Optional<GroceryItem> findItem(List<GroceryItem> items, String itemName) {
        return items.stream()
                .filter(item -> item.getName().equals(itemName))
                .findFirst();
    }
    
    /**
     * Met à jour un article existant.
     */
    private void updateItem(List<GroceryItem> items, GroceryItem oldItem, int quantity, String category) {
        // Supprimer l'ancien item
        items.remove(oldItem);
        
        // Déterminer les valeurs à utiliser
        int newQuantity = quantity > 0 ? quantity : oldItem.getQuantity();
        String newCategory = category != null ? category : oldItem.getCategory();
        
        // Ajouter l'article mis à jour
        items.add(new GroceryItem(oldItem.getName(), newQuantity, newCategory));
    }
} 