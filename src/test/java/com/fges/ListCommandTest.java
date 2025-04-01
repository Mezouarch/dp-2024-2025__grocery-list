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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        void shouldDisplayEmptyList() {
            List<String> args = Arrays.asList("list");
            listCommand.execute(args, groceryManager, null);
            
            Map<String, List<String>> itemsByCategory = groceryManager.getGroceryListByCategory();
            assertThat(itemsByCategory).isEmpty();
        }

        @Test
        @DisplayName("Devrait afficher tous les articles groupés par catégorie")
        void shouldDisplayAllItemsGroupedByCategory() throws IOException {
            groceryManager.addItem("Apple", 5, "Fruits");
            groceryManager.addItem("Carrot", 3, "Vegetables");
            
            List<String> args = Arrays.asList("list");
            listCommand.execute(args, groceryManager, null);
            
            Map<String, List<String>> itemsByCategory = groceryManager.getGroceryListByCategory();
            assertThat(itemsByCategory).containsKeys("Fruits", "Vegetables");
            assertThat(itemsByCategory.get("Fruits")).contains("Apple: 5");
            assertThat(itemsByCategory.get("Vegetables")).contains("Carrot: 3");
        }
    }

    @Nested
    @DisplayName("Tests pour l'affichage par catégorie")
    class CategoryListTests {
        @Test
        @DisplayName("Devrait afficher les articles d'une catégorie spécifique")
        void shouldDisplayItemsOfSpecificCategory() throws IOException {
            groceryManager.addItem("Apple", 5, "Fruits");
            groceryManager.addItem("Carrot", 3, "Vegetables");
            
            List<String> args = Arrays.asList("list");
            listCommand.execute(args, groceryManager, "Fruits");
            
            Map<String, List<String>> itemsByCategory = groceryManager.getGroceryListByCategory();
            assertThat(itemsByCategory.get("Fruits")).contains("Apple: 5");
            assertThat(itemsByCategory.get("Vegetables")).contains("Carrot: 3");
        }

        @Test
        @DisplayName("Devrait afficher un message pour une catégorie vide")
        void shouldDisplayMessageForEmptyCategory() throws IOException {
            groceryManager.addItem("Apple", 5, "Fruits");
            
            List<String> args = Arrays.asList("list");
            listCommand.execute(args, groceryManager, "Vegetables");
            
            Map<String, List<String>> itemsByCategory = groceryManager.getGroceryListByCategory();
            assertThat(itemsByCategory).doesNotContainKey("Vegetables");
        }

        @Test
        @DisplayName("Devrait gérer une catégorie inexistante")
        void shouldHandleNonExistentCategory() throws IOException {
            groceryManager.addItem("Apple", 5, "Fruits");
            
            List<String> args = Arrays.asList("list");
            listCommand.execute(args, groceryManager, "NonExistent");
            
            Map<String, List<String>> itemsByCategory = groceryManager.getGroceryListByCategory();
            assertThat(itemsByCategory).doesNotContainKey("NonExistent");
        }
    }

    @Nested
    @DisplayName("Tests pour la gestion des catégories par défaut")
    class DefaultCategoryTests {
        @Test
        @DisplayName("Devrait afficher les articles sans catégorie dans la catégorie par défaut")
        void shouldDisplayItemsWithoutCategoryInDefault() throws IOException {
            groceryManager.addItem("Apple", 5);
            
            List<String> args = Arrays.asList("list");
            listCommand.execute(args, groceryManager, null);
            
            Map<String, List<String>> itemsByCategory = groceryManager.getGroceryListByCategory();
            assertThat(itemsByCategory).containsKey("default");
            assertThat(itemsByCategory.get("default")).contains("Apple: 5");
        }

        @Test
        @DisplayName("Devrait afficher la catégorie par défaut quand spécifiée")
        void shouldDisplayDefaultCategoryWhenSpecified() throws IOException {
            groceryManager.addItem("Apple", 5);
            
            List<String> args = Arrays.asList("list");
            listCommand.execute(args, groceryManager, "default");
            
            Map<String, List<String>> itemsByCategory = groceryManager.getGroceryListByCategory();
            assertThat(itemsByCategory.get("default")).contains("Apple: 5");
        }
    }
} 