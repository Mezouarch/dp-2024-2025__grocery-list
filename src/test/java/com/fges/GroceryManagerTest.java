package com.fges;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GroceryManagerTest {
    private GroceryManager groceryManager;
    private JsonStorageManager storageManager;
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        storageManager = new JsonStorageManager();
        groceryManager = new GroceryManager(storageManager);
        File testFile = tempDir.resolve("test_grocery_list.json").toFile();
        TestUtils.createEmptyJsonFile(testFile);
        groceryManager.loadGroceryList(testFile.getPath());
    }

    @Nested
    @DisplayName("Tests pour l'ajout d'articles")
    class AddItemTests {
        @Test
        @DisplayName("Devrait ajouter un nouvel article")
        void shouldAddNewItem() throws Exception {
            groceryManager.addItem("Apple", 5, "Fruits");
            assertThat(groceryManager.doesItemExist("Apple")).isTrue();
            assertThat(groceryManager.getItemQuantity("Apple")).isEqualTo(5);
        }

        @Test
        @DisplayName("Devrait mettre à jour la quantité d'un article existant")
        void shouldUpdateExistingItemQuantity() throws Exception {
            groceryManager.addItem("Apple", 5, "Fruits");
            groceryManager.addItem("Apple", 3, "Fruits");
            assertThat(groceryManager.getItemQuantity("Apple")).isEqualTo(8);
        }

        @Test
        @DisplayName("Devrait gérer une quantité négative")
        void shouldHandleNegativeQuantity() throws Exception {
            groceryManager.addItem("Apple", 5, "Fruits");
            groceryManager.addItem("Apple", -3, "Fruits");
            assertThat(groceryManager.getItemQuantity("Apple")).isEqualTo(2);
        }

        @Test
        @DisplayName("Devrait rejeter un nom d'article vide")
        void shouldRejectEmptyItemName() {
            assertThatThrownBy(() -> groceryManager.addItem("", 5, "Fruits"))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Devrait rejeter une quantité nulle")
        void shouldRejectZeroQuantity() {
            assertThatThrownBy(() -> groceryManager.addItem("Apple", 0, "Fruits"))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Tests pour la suppression d'articles")
    class RemoveItemTests {
        @Test
        @DisplayName("Devrait supprimer un article existant")
        void shouldRemoveExistingItem() throws Exception {
            groceryManager.addItem("Apple", 5, "Fruits");
            groceryManager.removeItem("Apple");
            assertThat(groceryManager.doesItemExist("Apple")).isFalse();
        }

        @Test
        @DisplayName("Devrait rejeter la suppression d'un article inexistant")
        void shouldRejectRemovingNonExistentItem() {
            assertThatThrownBy(() -> groceryManager.removeItem("Apple"))
                .isInstanceOf(Exception.class)
                .hasMessage(MessageFormatter.formatItemNotFound("Apple"));
        }
    }

    @Nested
    @DisplayName("Tests pour la gestion des catégories")
    class CategoryTests {
        @Test
        @DisplayName("Devrait ajouter un article avec une catégorie spécifique")
        void shouldAddItemWithSpecificCategory() throws Exception {
            groceryManager.addItem("Apple", 5, "Fruits");
            assertThat(groceryManager.categoryExists("Fruits")).isTrue();
            assertThat(groceryManager.getItemsInCategory("Fruits")).contains("Apple: 5");
        }

        @Test
        @DisplayName("Devrait utiliser la catégorie par défaut si aucune n'est spécifiée")
        void shouldUseDefaultCategoryWhenNoneSpecified() throws Exception {
            groceryManager.addItem("Apple", 5);
            assertThat(groceryManager.categoryExists("default")).isTrue();
            assertThat(groceryManager.getItemsInCategory("default")).contains("Apple: 5");
        }

        @Test
        @DisplayName("Devrait supprimer une catégorie vide")
        void shouldRemoveEmptyCategory() throws Exception {
            groceryManager.addItem("Apple", 5, "Fruits");
            groceryManager.removeItem("Apple");
            assertThat(groceryManager.categoryExists("Fruits")).isFalse();
        }
    }

    @Nested
    @DisplayName("Tests pour l'affichage de la liste")
    class ListDisplayTests {
        @Test
        @DisplayName("Devrait afficher tous les articles groupés par catégorie")
        void shouldDisplayAllItemsGroupedByCategory() throws Exception {
            groceryManager.addItem("Apple", 5, "Fruits");
            groceryManager.addItem("Carrot", 3, "Vegetables");
            
            Map<String, List<String>> itemsByCategory = groceryManager.getGroceryListByCategory();
            assertThat(itemsByCategory).containsKeys("Fruits", "Vegetables");
            assertThat(itemsByCategory.get("Fruits")).contains("Apple: 5");
            assertThat(itemsByCategory.get("Vegetables")).contains("Carrot: 3");
        }

        @Test
        @DisplayName("Devrait afficher une liste vide")
        void shouldDisplayEmptyList() throws Exception {
            Map<String, List<String>> itemsByCategory = groceryManager.getGroceryListByCategory();
            assertThat(itemsByCategory).isEmpty();
        }
    }
} 