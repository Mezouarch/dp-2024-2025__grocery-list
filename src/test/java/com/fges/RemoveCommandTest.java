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

class RemoveCommandTest {
    private RemoveCommand removeCommand;
    private GroceryManager groceryManager;
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        removeCommand = new RemoveCommand();
        groceryManager = new GroceryManager(new JsonStorageManager());
        File testFile = tempDir.resolve("test_grocery_list.json").toFile();
        groceryManager.loadGroceryList(testFile.getPath());
    }

    @Nested
    @DisplayName("Tests pour la suppression complète d'un article")
    class CompleteRemovalTests {
        @Test
        @DisplayName("Devrait supprimer complètement un article existant")
        void shouldRemoveExistingItem() throws Exception {
            groceryManager.addItem("Apple", 5, "Fruits");
            List<String> args = Arrays.asList("remove", "Apple");
            removeCommand.execute(args, groceryManager, null);
            
            assertThat(groceryManager.doesItemExist("Apple")).isFalse();
        }

        @Test
        @DisplayName("Devrait rejeter la suppression d'un article inexistant")
        void shouldRejectRemovingNonExistentItem() {
            List<String> args = Arrays.asList("remove", "Apple");
            assertThatThrownBy(() -> removeCommand.execute(args, groceryManager, null))
                .isInstanceOf(Exception.class)
                .hasMessage(MessageFormatter.formatItemNotFound("Apple"));
        }

        @Test
        @DisplayName("Devrait rejeter un nombre d'arguments insuffisant")
        void shouldRejectInsufficientArgs() {
            List<String> args = Arrays.asList("remove");
            assertThatThrownBy(() -> removeCommand.execute(args, groceryManager, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Arguments manquants pour la commande 'remove'.");
        }
    }

    @Nested
    @DisplayName("Tests pour la suppression partielle d'un article")
    class PartialRemovalTests {
        @Test
        @DisplayName("Devrait supprimer une quantité spécifique d'un article")
        void shouldRemoveSpecificQuantity() throws Exception {
            groceryManager.addItem("Apple", 5, "Fruits");
            List<String> args = Arrays.asList("remove", "Apple", "3");
            removeCommand.execute(args, groceryManager, null);
            
            assertThat(groceryManager.getItemQuantity("Apple")).isEqualTo(2);
        }

        @Test
        @DisplayName("Devrait rejeter la suppression d'une quantité supérieure à la quantité disponible")
        void shouldRejectRemovingMoreThanAvailable() throws Exception {
            groceryManager.addItem("Apple", 5, "Fruits");
            List<String> args = Arrays.asList("remove", "Apple", "6");
            assertThatThrownBy(() -> removeCommand.execute(args, groceryManager, null))
                .isInstanceOf(Exception.class)
                .hasMessage(MessageFormatter.formatInvalidQuantity("Apple", 5, 6));
        }

        @Test
        @DisplayName("Devrait rejeter une quantité invalide")
        void shouldRejectInvalidQuantity() {
            List<String> args = Arrays.asList("remove", "Apple", "invalid");
            assertThatThrownBy(() -> removeCommand.execute(args, groceryManager, null))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Devrait rejeter une quantité nulle")
        void shouldRejectZeroQuantity() {
            List<String> args = Arrays.asList("remove", "Apple", "0");
            assertThatThrownBy(() -> removeCommand.execute(args, groceryManager, null))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Tests pour la gestion des catégories lors de la suppression")
    class CategoryRemovalTests {
        @Test
        @DisplayName("Devrait supprimer un article de sa catégorie")
        void shouldRemoveItemFromCategory() throws Exception {
            groceryManager.addItem("Apple", 5, "Fruits");
            List<String> args = Arrays.asList("remove", "Apple");
            removeCommand.execute(args, groceryManager, null);
            
            Map<String, List<String>> itemsByCategory = groceryManager.getGroceryListByCategory();
            assertThat(itemsByCategory.get("Fruits")).doesNotContain("Apple: 5");
        }

        @Test
        @DisplayName("Devrait supprimer une catégorie vide")
        void shouldRemoveEmptyCategory() throws Exception {
            groceryManager.addItem("Apple", 5, "Fruits");
            List<String> args = Arrays.asList("remove", "Apple");
            removeCommand.execute(args, groceryManager, null);
            
            Map<String, List<String>> itemsByCategory = groceryManager.getGroceryListByCategory();
            assertThat(itemsByCategory).doesNotContainKey("Fruits");
        }
    }
} 