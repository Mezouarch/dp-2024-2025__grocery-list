package com.fges;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryManagerTest {
    private CategoryManager categoryManager;

    @BeforeEach
    void setUp() {
        categoryManager = new CategoryManager();
    }

    @Nested
    @DisplayName("Tests pour l'ajout d'articles")
    class AddItemTests {
        @Test
        @DisplayName("Devrait ajouter un article à une catégorie")
        void shouldAddItemToCategory() {
            categoryManager.addItemToCategory("Fruits", "Apple", 5);
            List<String> items = categoryManager.getItemsInCategory("Fruits");
            assertThat(items).containsExactly("Apple: 5");
        }

        @Test
        @DisplayName("Devrait ajouter plusieurs articles à une catégorie")
        void shouldAddMultipleItemsToCategory() {
            categoryManager.addItemToCategory("Fruits", "Apple", 5);
            categoryManager.addItemToCategory("Fruits", "Banana", 3);
            List<String> items = categoryManager.getItemsInCategory("Fruits");
            assertThat(items).containsExactly("Apple: 5", "Banana: 3");
        }

        @Test
        @DisplayName("Devrait gérer les catégories vides ou nulles")
        void shouldHandleEmptyOrNullCategories() {
            categoryManager.addItemToCategory("", "Apple", 5);
            categoryManager.addItemToCategory(null, "Banana", 3);
            List<String> items = categoryManager.getItemsInCategory("default");
            assertThat(items).containsExactly("Apple: 5", "Banana: 3");
        }
    }

    @Nested
    @DisplayName("Tests pour la suppression d'articles")
    class RemoveItemTests {
        @Test
        @DisplayName("Devrait supprimer une catégorie vide")
        void shouldRemoveEmptyCategory() {
            categoryManager.addItemToCategory("Fruits", "Apple", 5);
            categoryManager.removeItemFromCategory("Fruits", "Apple");
            Map<String, Map<String, Integer>> allCategories = categoryManager.getAllCategories();
            assertThat(allCategories).doesNotContainKey("Fruits");
        }

        @Test
        @DisplayName("Devrait gérer la suppression d'un article inexistant")
        void shouldHandleRemovingNonExistentItem() {
            categoryManager.addItemToCategory("Fruits", "Apple", 5);
            categoryManager.removeItemFromCategory("Fruits", "Banana");
            List<String> items = categoryManager.getItemsInCategory("Fruits");
            assertThat(items).containsExactly("Apple: 5");
        }
    }

    @Nested
    @DisplayName("Tests pour la récupération des catégories")
    class GetCategoriesTests {
        @Test
        @DisplayName("Devrait récupérer toutes les catégories")
        void shouldGetAllCategories() {
            categoryManager.addItemToCategory("Fruits", "Apple", 5);
            categoryManager.addItemToCategory("Vegetables", "Carrot", 3);
            Map<String, Map<String, Integer>> allCategories = categoryManager.getAllCategories();
            assertThat(allCategories).containsKeys("Fruits", "Vegetables");
            assertThat(allCategories.get("Fruits")).containsEntry("Apple", 5);
            assertThat(allCategories.get("Vegetables")).containsEntry("Carrot", 3);
        }

        @Test
        @DisplayName("Devrait retourner une liste vide pour une catégorie inexistante")
        void shouldReturnEmptyListForNonExistentCategory() {
            List<String> items = categoryManager.getItemsInCategory("NonExistent");
            assertThat(items).isEmpty();
        }

        @Test
        @DisplayName("Devrait vérifier l'existence d'une catégorie")
        void shouldCheckCategoryExistence() {
            categoryManager.addItemToCategory("Fruits", "Apple", 5);
            assertThat(categoryManager.categoryExists("Fruits")).isTrue();
            assertThat(categoryManager.categoryExists("NonExistent")).isFalse();
        }
    }
} 