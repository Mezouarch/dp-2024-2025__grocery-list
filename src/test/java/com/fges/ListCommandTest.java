package com.fges;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListCommandTest {
    private ListCommand listCommand;
    private GroceryManager groceryManager;
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        listCommand = new ListCommand();
        groceryManager = new GroceryManager(new JsonStorageManager());
        File testFile = tempDir.resolve("test_grocery_list.json").toFile();
        TestUtils.createEmptyJsonFile(testFile);
        groceryManager.loadGroceryList(testFile.getPath());
    }

    @Nested
    @DisplayName("Tests pour l'affichage de la liste complète")
    class FullListTests {
        @Test
        @DisplayName("Devrait afficher une liste vide")
        void shouldDisplayEmptyList() throws Exception {
            List<String> args = Arrays.asList("list");
            String result = listCommand.execute(args, groceryManager, null);
            assertEquals(MessageFormatter.formatEmptyList(), result);
        }

        @Test
        @DisplayName("Devrait afficher tous les articles groupés par catégorie")
        void shouldDisplayAllItemsGroupedByCategory() throws Exception {
            groceryManager.addItem("Apple", 5, "Fruits");
            groceryManager.addItem("Carrot", 3, "Vegetables");
            
            List<String> args = Arrays.asList("list");
            String result = listCommand.execute(args, groceryManager, null);
            
            assertTrue(result.contains("Fruits"));
            assertTrue(result.contains("Vegetables"));
            assertTrue(result.contains("Apple: 5"));
            assertTrue(result.contains("Carrot: 3"));
        }
    }

    @Nested
    @DisplayName("Tests pour l'affichage par catégorie")
    class CategoryListTests {
        @Test
        @DisplayName("Devrait gérer une catégorie inexistante")
        void shouldHandleNonExistentCategory() throws Exception {
            groceryManager.addItem("Apple", 5, "Fruits");
            
            List<String> args = Arrays.asList("list");
            String result = listCommand.execute(args, groceryManager, "NonExistent");
            
            assertEquals(MessageFormatter.formatCategoryNotFound("NonExistent"), result);
        }
    }

    @Nested
    @DisplayName("Tests pour la gestion des catégories par défaut")
    class DefaultCategoryTests {
        @Test
        @DisplayName("Devrait afficher les articles sans catégorie dans la catégorie par défaut")
        void shouldDisplayItemsWithoutCategoryInDefault() throws Exception {
            groceryManager.addItem("Apple", 5);
            
            List<String> args = Arrays.asList("list");
            String result = listCommand.execute(args, groceryManager, null);
            
            assertTrue(result.contains("default"));
            assertTrue(result.contains("Apple: 5"));
        }
    }
} 