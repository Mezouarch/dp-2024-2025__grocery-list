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
    private StorageManager storageManager;
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        storageManager = new JsonStorageManager();
        groceryManager = new GroceryManager(storageManager);
    }

    @Nested
    @DisplayName("Tests pour l'ajout d'articles")
    class AddItemTests {
        @Test
        @DisplayName("Devrait ajouter un nouvel article")
        void shouldAddNewItem() throws IOException {
            groceryManager.addItem("Apple", 5, "Fruits");
            assertThat(groceryManager.doesItemExist("Apple")).isTrue();
            assertThat(groceryManager.getItemQuantity("Apple")).isEqualTo(5);
        }

        @Test
        @DisplayName("Devrait mettre à jour la quantité d'un article existant")
        void shouldUpdateExistingItemQuantity() throws IOException {
            groceryManager.addItem("Apple", 5, "Fruits");
            groceryManager.addItem("Apple", 3, "Fruits");
            assertThat(groceryManager.getItemQuantity("Apple")).isEqualTo(8);
        }

        @Test
        @DisplayName("Devrait supprimer un article quand la quantité devient nulle")
        void shouldRemoveItemWhenQuantityBecomesZero() throws IOException {
            groceryManager.addItem("Apple", 5, "Fruits");
            groceryManager.addItem("Apple", -5, "Fruits");
            assertThat(groceryManager.doesItemExist("Apple")).isFalse();
        }

        @Test
        @DisplayName("Devrait rejeter un nom d'article invalide")
        void shouldRejectInvalidItemName() {
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
    class CategoryManagementTests {
        @Test
        @DisplayName("Devrait organiser les articles par catégorie")
        void shouldOrganizeItemsByCategory() throws IOException {
            groceryManager.addItem("Apple", 5, "Fruits");
            groceryManager.addItem("Carrot", 3, "Vegetables");
            Map<String, List<String>> itemsByCategory = groceryManager.getGroceryListByCategory();
            
            assertThat(itemsByCategory).containsKeys("Fruits", "Vegetables");
            assertThat(itemsByCategory.get("Fruits")).contains("Apple: 5");
            assertThat(itemsByCategory.get("Vegetables")).contains("Carrot: 3");
        }

        @Test
        @DisplayName("Devrait gérer les articles sans catégorie")
        void shouldHandleItemsWithoutCategory() throws IOException {
            groceryManager.addItem("Apple", 5);
            Map<String, List<String>> itemsByCategory = groceryManager.getGroceryListByCategory();
            
            assertThat(itemsByCategory).containsKey("default");
            assertThat(itemsByCategory.get("default")).contains("Apple: 5");
        }
    }

    @Nested
    @DisplayName("Tests pour la persistance des données")
    class DataPersistenceTests {
        @Test
        @DisplayName("Devrait sauvegarder et charger la liste de courses")
        void shouldSaveAndLoadGroceryList() throws IOException {
            File testFile = tempDir.resolve("test_grocery_list.json").toFile();
            groceryManager.addItem("Apple", 5, "Fruits");
            groceryManager.saveGroceryList(testFile.getPath());
            
            GroceryManager newManager = new GroceryManager(new JsonStorageManager());
            newManager.loadGroceryList(testFile.getPath());
            
            assertThat(newManager.doesItemExist("Apple")).isTrue();
            assertThat(newManager.getItemQuantity("Apple")).isEqualTo(5);
        }

        @Test
        @DisplayName("Devrait gérer les erreurs de fichier")
        void shouldHandleFileErrors() {
            assertThatThrownBy(() -> groceryManager.loadGroceryList("nonexistent.json"))
                .isInstanceOf(IOException.class);
        }
    }

    @Nested
    @DisplayName("Tests pour la sauvegarde automatique")
    class AutoSaveTests {
        @Test
        @DisplayName("Devrait sauvegarder automatiquement après chaque modification")
        void shouldAutoSaveAfterModifications() throws IOException {
            File testFile = tempDir.resolve("test_grocery_list.json").toFile();
            TestUtils.createEmptyJsonFile(testFile);
            groceryManager.loadGroceryList(testFile.getPath());
            
            groceryManager.addItem("Apple", 5, "Fruits");
            GroceryManager newManager = new GroceryManager(new JsonStorageManager());
            newManager.loadGroceryList(testFile.getPath());
            
            assertThat(newManager.doesItemExist("Apple")).isTrue();
            assertThat(newManager.getItemQuantity("Apple")).isEqualTo(5);
        }
    }
} 