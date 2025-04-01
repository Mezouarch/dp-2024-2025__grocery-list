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

class AddCommandTest {
    private AddCommand addCommand;
    private GroceryManager groceryManager;
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        addCommand = new AddCommand();
        groceryManager = new GroceryManager(new JsonStorageManager());
        File testFile = tempDir.resolve("test_grocery_list.json").toFile();
        groceryManager.loadGroceryList(testFile.getPath());
    }

    @Nested
    @DisplayName("Tests pour l'exécution de la commande add")
    class ExecuteTests {
        @Test
        @DisplayName("Devrait ajouter un nouvel article")
        void shouldAddNewItem() throws Exception {
            List<String> args = Arrays.asList("add", "Apple", "5");
            addCommand.execute(args, groceryManager, "Fruits");
            
            assertThat(groceryManager.doesItemExist("Apple")).isTrue();
            assertThat(groceryManager.getItemQuantity("Apple")).isEqualTo(5);
        }

        @Test
        @DisplayName("Devrait mettre à jour la quantité d'un article existant")
        void shouldUpdateExistingItemQuantity() throws Exception {
            groceryManager.addItem("Apple", 5, "Fruits");
            List<String> args = Arrays.asList("add", "Apple", "3");
            addCommand.execute(args, groceryManager, "Fruits");
            
            assertThat(groceryManager.getItemQuantity("Apple")).isEqualTo(8);
        }

        @Test
        @DisplayName("Devrait gérer une quantité négative")
        void shouldHandleNegativeQuantity() throws Exception {
            groceryManager.addItem("Apple", 5, "Fruits");
            List<String> args = Arrays.asList("add", "Apple", "-3");
            addCommand.execute(args, groceryManager, "Fruits");
            
            assertThat(groceryManager.getItemQuantity("Apple")).isEqualTo(2);
        }

        @Test
        @DisplayName("Devrait rejeter un nombre d'arguments insuffisant")
        void shouldRejectInsufficientArgs() {
            List<String> args = Arrays.asList("add", "Apple");
            assertThatThrownBy(() -> addCommand.execute(args, groceryManager, "Fruits"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Arguments manquants pour la commande 'add'.");
        }

        @Test
        @DisplayName("Devrait rejeter une quantité invalide")
        void shouldRejectInvalidQuantity() {
            List<String> args = Arrays.asList("add", "Apple", "invalid");
            assertThatThrownBy(() -> addCommand.execute(args, groceryManager, "Fruits"))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Devrait rejeter une quantité nulle")
        void shouldRejectZeroQuantity() {
            List<String> args = Arrays.asList("add", "Apple", "0");
            assertThatThrownBy(() -> addCommand.execute(args, groceryManager, "Fruits"))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Tests pour la gestion des catégories")
    class CategoryTests {
        @Test
        @DisplayName("Devrait ajouter un article avec une catégorie spécifique")
        void shouldAddItemWithSpecificCategory() throws Exception {
            List<String> args = Arrays.asList("add", "Apple", "5");
            addCommand.execute(args, groceryManager, "Fruits");
            
            Map<String, List<String>> itemsByCategory = groceryManager.getGroceryListByCategory();
            assertThat(itemsByCategory).containsKey("Fruits");
            assertThat(itemsByCategory.get("Fruits")).contains("Apple: 5");
        }

        @Test
        @DisplayName("Devrait utiliser la catégorie par défaut si aucune n'est spécifiée")
        void shouldUseDefaultCategoryWhenNoneSpecified() throws Exception {
            List<String> args = Arrays.asList("add", "Apple", "5");
            addCommand.execute(args, groceryManager, null);
            
            Map<String, List<String>> itemsByCategory = groceryManager.getGroceryListByCategory();
            assertThat(itemsByCategory).containsKey("default");
            assertThat(itemsByCategory.get("default")).contains("Apple: 5");
        }
    }
} 