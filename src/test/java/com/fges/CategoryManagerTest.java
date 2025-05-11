package com.fges;

import com.fges.model.CategoryManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CategoryManagerTest {
    
    private CategoryManager categoryManager;
    
    @BeforeEach
    void setUp() {
        categoryManager = new CategoryManager();
    }
    
    @Test
    void shouldAddItemToCategory() {
        // Arrange
        String itemName = "apple";
        String category = "fruits";
        
        // Act
        categoryManager.addItemToCategory(itemName, category);
        
        // Assert
        assertTrue(categoryManager.containsItem(itemName));
        assertEquals(category, categoryManager.getItemCategory(itemName));
        assertTrue(categoryManager.getItemsInCategory(category).contains(itemName));
    }
    
    @Test
    void shouldUseDefaultCategoryWhenCategoryIsNull() {
        // Arrange
        String itemName = "apple";
        
        // Act
        categoryManager.addItemToCategory(itemName, null);
        
        // Assert
        assertEquals("default", categoryManager.getItemCategory(itemName));
        assertTrue(categoryManager.getItemsInCategory("default").contains(itemName));
    }
    
    @Test
    void shouldUseDefaultCategoryWhenCategoryIsEmpty() {
        // Arrange
        String itemName = "apple";
        
        // Act
        categoryManager.addItemToCategory(itemName, "");
        
        // Assert
        assertEquals("default", categoryManager.getItemCategory(itemName));
        assertTrue(categoryManager.getItemsInCategory("default").contains(itemName));
    }
    
    @Test
    void shouldMoveItemToNewCategory() {
        // Arrange
        String itemName = "apple";
        String oldCategory = "fruits";
        String newCategory = "food";
        
        // Act
        categoryManager.addItemToCategory(itemName, oldCategory);
        categoryManager.addItemToCategory(itemName, newCategory);
        
        // Assert
        assertEquals(newCategory, categoryManager.getItemCategory(itemName));
        assertFalse(categoryManager.getItemsInCategory(oldCategory).contains(itemName));
        assertTrue(categoryManager.getItemsInCategory(newCategory).contains(itemName));
    }
    
    @Test
    void shouldRemoveItem() {
        // Arrange
        String itemName = "apple";
        String category = "fruits";
        categoryManager.addItemToCategory(itemName, category);
        
        // Act
        categoryManager.removeItem(itemName);
        
        // Assert
        assertFalse(categoryManager.containsItem(itemName));
        assertFalse(categoryManager.getItemsInCategory(category).contains(itemName));
    }
    
    @Test
    void shouldCheckIfCategoryExists() {
        // Arrange
        String category = "fruits";
        categoryManager.addItemToCategory("apple", category);
        
        // Assert
        assertTrue(categoryManager.categoryExists(category));
        assertFalse(categoryManager.categoryExists("nonexistent"));
    }
    
    @Test
    void shouldGetAllCategories() {
        // Arrange
        categoryManager.addItemToCategory("apple", "fruits");
        categoryManager.addItemToCategory("carrot", "vegetables");
        
        // Act
        Map<String, Set<String>> allCategories = categoryManager.getAllCategories();
        
        // Assert
        assertTrue(allCategories.containsKey("fruits"));
        assertTrue(allCategories.containsKey("vegetables"));
        assertTrue(allCategories.containsKey("default"));
    }
} 