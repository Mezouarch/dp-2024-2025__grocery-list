package com.fges.model.strategies;

import com.fges.model.GroceryItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Stratégie pour ajouter un article à la liste de courses.
 * Gère à la fois l'ajout d'un nouvel article et la mise à jour d'un article existant.
 */
public class AddItemStrategy implements ItemOperationStrategy {
    
    @Override
    public List<GroceryItem> execute(List<GroceryItem> items, String itemName, int quantity, String category) throws Exception {
        validateInput(itemName, quantity);
        
        // Copier la liste pour ne pas modifier l'originale
        List<GroceryItem> result = new ArrayList<>(items);
        
        // Vérifier si l'article existe déjà
        Optional<GroceryItem> existingItem = result.stream()
                .filter(item -> item.getName().equals(itemName))
                .findFirst();
        
        if (existingItem.isPresent()) {
            updateExistingItem(result, existingItem.get(), quantity, category);
        } else {
            addNewItem(result, itemName, quantity, category);
        }
        
        return result;
    }
    
    /**
     * Valide les paramètres d'entrée.
     */
    private void validateInput(String itemName, int quantity) {
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'article ne peut pas être vide");
        }
        
        if (quantity == 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être nulle");
        }
    }
    
    /**
     * Met à jour un article existant.
     */
    private void updateExistingItem(List<GroceryItem> items, GroceryItem oldItem, int quantity, String category) {
        // Supprimer l'ancien item
        items.remove(oldItem);
        
        // Calculer la nouvelle quantité
        int newQuantity = oldItem.getQuantity() + quantity;
        
        // Utiliser la catégorie spécifiée ou conserver l'ancienne
        String categoryToUse = category != null ? category : oldItem.getCategory();
        
        // Ajouter le nouvel item avec les valeurs mises à jour
        items.add(new GroceryItem(oldItem.getName(), newQuantity, categoryToUse));
    }
    
    /**
     * Ajoute un nouvel article.
     */
    private void addNewItem(List<GroceryItem> items, String itemName, int quantity, String category) {
        items.add(new GroceryItem(itemName, quantity, category));
    }
} 