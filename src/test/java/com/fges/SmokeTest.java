package com.fges;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

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

        @BeforeEach
        void setUp() {
            removeCommand = new RemoveCommand();
            // Utiliser JsonStorageManager par défaut pour les tests
            StorageManager storageManager = new JsonStorageManager();
            groceryManager = new GroceryManager(storageManager);
            outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
        }

        @Test
        @DisplayName("Devrait supprimer une quantité spécifique d'un article")
        void shouldRemoveSpecificQuantityOfItem() throws Exception {
            // Ajouter d'abord un article
            groceryManager.addItem("Milk", 3);
            
            List<String> args = Arrays.asList("remove", "Milk", "1");
            removeCommand.execute(args, groceryManager);

            assertThat(groceryManager.getGroceryList())
                .containsExactly("Milk: 2");
        }

        @Test
        @DisplayName("Devrait supprimer entièrement un article si aucune quantité n'est spécifiée")
        void shouldRemoveEntireItemWhenNoQuantitySpecified() throws Exception {
            // Ajouter d'abord un article
            groceryManager.addItem("Milk", 3);
            
            List<String> args = Arrays.asList("remove", "Milk");
            removeCommand.execute(args, groceryManager);

            assertThat(groceryManager.getGroceryList())
                .isEmpty();
        }

        @Test
        @DisplayName("Devrait lancer une exception lors de la suppression d'un article inexistant")
        void shouldThrowExceptionWhenRemovingNonExistentItem() {
            List<String> args = Arrays.asList("remove", "Milk");
            
            assertThatThrownBy(() -> removeCommand.execute(args, groceryManager))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("n'existe pas dans la liste");
        }

        @Test
        @DisplayName("Devrait lancer une exception avec des arguments insuffisants")
        void shouldThrowExceptionWithInsufficientArguments() {
            assertThatThrownBy(() -> removeCommand.execute(Arrays.asList("remove"), groceryManager))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Arguments manquants");
        }

        @Test
        @DisplayName("Devrait afficher un message lors de la suppression complète")
        void shouldOutputMessageWhenCompletingRemoving() throws Exception {
            // Ajouter d'abord un article
            groceryManager.addItem("Milk", 3);
            
            List<String> args = Arrays.asList("remove", "Milk");
            removeCommand.execute(args, groceryManager);

            assertThat(outContent.toString().trim())
                .contains("L'article Milk a été complètement supprimé");
        }
    }
}