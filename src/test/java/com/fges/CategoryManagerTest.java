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
            categoryManager.addItemToCategory("Fruits", "Apple");
            List<String> items = categoryManager.getItemsInCategory("Fruits");
            assertThat(items).containsExactly("Apple");
        }

        @Test
        @DisplayName("Devrait ajouter plusieurs articles à une catégorie")
        void shouldAddMultipleItemsToCategory() {
            categoryManager.addItemToCategory("Fruits", "Apple");
            categoryManager.addItemToCategory("Fruits", "Banana");
            List<String> items = categoryManager.getItemsInCategory("Fruits");
            assertThat(items).containsExactly("Apple", "Banana");
        }

        @Test
        @DisplayName("Devrait gérer les catégories vides ou nulles")
        void shouldHandleEmptyOrNullCategories() {
            categoryManager.addItemToCategory("", "Apple");
            categoryManager.addItemToCategory(null, "Banana");
            List<String> items = categoryManager.getItemsInCategory("default");
            assertThat(items).containsExactly("Apple", "Banana");
        }
    }

    @Nested
    @DisplayName("Tests pour la suppression d'articles")
    class RemoveItemTests {
        @Test
        @DisplayName("Devrait supprimer un article d'une catégorie")
        void shouldRemoveItemFromCategory() {
            categoryManager.addItemToCategory("Fruits", "Apple");
            categoryManager.removeItemFromCategory("Fruits", "Apple");
            List<String> items = categoryManager.getItemsInCategory("Fruits");
            assertThat(items).isEmpty();
        }

        @Test
        @DisplayName("Devrait supprimer une catégorie vide")
        void shouldRemoveEmptyCategory() {
            categoryManager.addItemToCategory("Fruits", "Apple");
            categoryManager.removeItemFromCategory("Fruits", "Apple");
            Map<String, List<String>> allCategories = categoryManager.getAllCategories();
            assertThat(allCategories).doesNotContainKey("Fruits");
        }

        @Test
        @DisplayName("Devrait gérer la suppression d'un article inexistant")
        void shouldHandleRemovingNonExistentItem() {
            categoryManager.addItemToCategory("Fruits", "Apple");
            categoryManager.removeItemFromCategory("Fruits", "Banana");
            List<String> items = categoryManager.getItemsInCategory("Fruits");
            assertThat(items).containsExactly("Apple");
        }
    }

    @Nested
    @DisplayName("Tests pour la récupération des catégories")
    class GetCategoriesTests {
        @Test
        @DisplayName("Devrait récupérer toutes les catégories")
        void shouldGetAllCategories() {
            categoryManager.addItemToCategory("Fruits", "Apple");
            categoryManager.addItemToCategory("Vegetables", "Carrot");
            Map<String, List<String>> allCategories = categoryManager.getAllCategories();
            assertThat(allCategories).containsKeys("Fruits", "Vegetables");
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
            categoryManager.addItemToCategory("Fruits", "Apple");
            assertThat(categoryManager.categoryExists("Fruits")).isTrue();
            assertThat(categoryManager.categoryExists("NonExistent")).isFalse();
        }
    }
} 