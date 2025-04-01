package com.fges;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests unitaires pour le système de gestion de liste de courses.
 */
class SmokeTest {
    
    @Test
    @DisplayName("Test de base qui devrait toujours passer")
    void should_allways_pass() {
        assertThat(true).isTrue();
    }
    
    /**
     * Tests spécifiques à la commande Remove.
     */
    @Nested
    @DisplayName("Tests pour RemoveCommand")
    class RemoveCommandTests {
        private RemoveCommand removeCommand;
        private GroceryManager groceryManager;
        private ByteArrayOutputStream outContent;
        private String category;

        @BeforeEach
        void setUp() {
            removeCommand = new RemoveCommand();
            // Utiliser JsonStorageManager par défaut pour les tests
            StorageManager storageManager = new JsonStorageManager();
            groceryManager = new GroceryManager(storageManager);
            outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            // Initialiser la catégorie par défaut
            category = "Produits laitiers";
        }

        @Test
        @DisplayName("Devrait supprimer une quantité spécifique d'un article")
        void shouldRemoveSpecificQuantityOfItem() throws Exception {
            // Ajouter d'abord un article
            groceryManager.addItem("Milk", 3, category);
            
            List<String> args = Arrays.asList("remove", "Milk", "1");
            removeCommand.execute(args, groceryManager, category);

            assertThat(groceryManager.getGroceryList())
                .containsExactly("Milk: 2");
        }

        @Test
        @DisplayName("Devrait supprimer entièrement un article si aucune quantité n'est spécifiée")
        void shouldRemoveEntireItemWhenNoQuantitySpecified() throws Exception {
            // Ajouter d'abord un article
            groceryManager.addItem("Milk", 3, category);
            
            List<String> args = Arrays.asList("remove", "Milk");
            removeCommand.execute(args, groceryManager, category);

            assertThat(groceryManager.getGroceryList())
                .isEmpty();
        }

        @Test
        @DisplayName("Devrait lancer une exception lors de la suppression d'un article inexistant")
        void shouldThrowExceptionWhenRemovingNonExistentItem() {
            List<String> args = Arrays.asList("remove", "Milk");
            
            assertThatThrownBy(() -> removeCommand.execute(args, groceryManager, category))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("n'existe pas dans la liste");
        }

        @Test
        @DisplayName("Devrait lancer une exception avec des arguments insuffisants")
        void shouldThrowExceptionWithInsufficientArguments() {
            assertThatThrownBy(() -> removeCommand.execute(Arrays.asList("remove"), groceryManager, category))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Arguments manquants");
        }

        @Test
        @DisplayName("Devrait afficher un message lors de la suppression complète")
        void shouldOutputMessageWhenCompletingRemoving() throws Exception {
            // Ajouter d'abord un article
            groceryManager.addItem("Milk", 3, category);
            
            List<String> args = Arrays.asList("remove", "Milk");
            removeCommand.execute(args, groceryManager, category);

            assertThat(outContent.toString().trim())
                .contains("L'article Milk a été complètement supprimé");
        }
    }
    
    /**
     * Tests spécifiques à la commande Add.
     */
    @Nested
    @DisplayName("Tests pour AddCommand")
    class AddCommandTests {
        private AddCommand addCommand;
        private GroceryManager groceryManager;
        private ByteArrayOutputStream outContent;
        private String category;

        @BeforeEach
        void setUp() {
            addCommand = new AddCommand();
            StorageManager storageManager = new JsonStorageManager();
            groceryManager = new GroceryManager(storageManager);
            outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            category = "Produits laitiers";
        }

        @Test
        @DisplayName("Devrait ajouter un nouvel article")
        void shouldAddNewItem() throws Exception {
            List<String> args = Arrays.asList("add", "Milk", "3");
            addCommand.execute(args, groceryManager, category);

            assertThat(groceryManager.getGroceryList())
                .containsExactly("Milk: 3");
        }

        @Test
        @DisplayName("Devrait augmenter la quantité d'un article existant")
        void shouldIncreaseQuantityOfExistingItem() throws Exception {
            // Ajouter d'abord un article
            groceryManager.addItem("Milk", 2, category);
            
            List<String> args = Arrays.asList("add", "Milk", "3");
            addCommand.execute(args, groceryManager, category);

            assertThat(groceryManager.getGroceryList())
                .containsExactly("Milk: 5");
        }

        @Test
        @DisplayName("Devrait lancer une exception avec une quantité invalide")
        void shouldThrowExceptionWithInvalidQuantity() {
            List<String> args = Arrays.asList("add", "Milk", "abc");
            
            assertThatThrownBy(() -> addCommand.execute(args, groceryManager, category))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("La quantité doit être un entier");
        }

        @Test
        @DisplayName("Devrait lancer une exception avec une quantité nulle")
        void shouldThrowExceptionWithZeroQuantity() {
            List<String> args = Arrays.asList("add", "Milk", "0");
            
            assertThatThrownBy(() -> addCommand.execute(args, groceryManager, category))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("La quantité doit être différente de zéro");
        }

        @Test
        @DisplayName("Devrait lancer une exception avec des arguments insuffisants")
        void shouldThrowExceptionWithInsufficientArguments() {
            assertThatThrownBy(() -> addCommand.execute(Arrays.asList("add", "Milk"), groceryManager, category))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Arguments manquants");
        }

        @Test
        @DisplayName("Devrait afficher un message pour les ajouts")
        void shouldOutputMessageWhenAdding() throws Exception {
            List<String> args = Arrays.asList("add", "Milk", "3");
            addCommand.execute(args, groceryManager, category);

            assertThat(outContent.toString().trim())
                .contains("3 unités de Milk ajoutées");
        }

        @Test
        @DisplayName("Devrait afficher un message pour les retraits")
        void shouldOutputMessageWhenRemoving() throws Exception {
            // Ajouter d'abord un article
            groceryManager.addItem("Milk", 5, category);
            
            List<String> args = Arrays.asList("add", "Milk", "-3");
            addCommand.execute(args, groceryManager, category);

            assertThat(outContent.toString().trim())
                .contains("3 unités de Milk supprimées");
        }
    }
    
    /**
     * Tests spécifiques à la commande List.
     */
    @Nested
    @DisplayName("Tests pour ListCommand")
    class ListCommandTests {
        private ListCommand listCommand;
        private GroceryManager groceryManager;
        private ByteArrayOutputStream outContent;

        @BeforeEach
        void setUp() {
            listCommand = new ListCommand();
            StorageManager storageManager = new JsonStorageManager();
            groceryManager = new GroceryManager(storageManager);
            outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
        }

        @Test
        @DisplayName("Devrait afficher un message pour une liste vide")
        void shouldDisplayMessageForEmptyList() throws Exception {
            List<String> args = Arrays.asList("list");
            listCommand.execute(args, groceryManager, null);

            assertThat(outContent.toString().trim())
                .contains("La liste de courses est vide");
        }

        @Test
        @DisplayName("Devrait afficher correctement les articles par catégorie")
        void shouldDisplayItemsByCategory() throws Exception {
            // Ajouter des articles dans différentes catégories
            groceryManager.addItem("Milk", 3, "Produits laitiers");
            groceryManager.addItem("Bread", 1, "Boulangerie");
            
            List<String> args = Arrays.asList("list");
            listCommand.execute(args, groceryManager, null);

            String output = outContent.toString().trim();
            assertThat(output).contains("Liste de courses");
            assertThat(output).contains("# Produits laitiers");
            assertThat(output).contains("# Boulangerie");
            assertThat(output).contains("Milk: 3");
            assertThat(output).contains("Bread: 1");
        }
    }
}