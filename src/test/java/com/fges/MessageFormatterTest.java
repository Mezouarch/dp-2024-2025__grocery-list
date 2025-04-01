package com.fges;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;

class MessageFormatterTest {

    @Nested
    @DisplayName("Tests pour le formatage des messages de confirmation d'ajout")
    class AddConfirmationTests {
        @Test
        @DisplayName("Devrait formater un message de confirmation d'ajout")
        void shouldFormatAddConfirmation() {
            String message = MessageFormatter.formatAddConfirmation("Apple", 5);
            assertThat(message).isEqualTo("5 unités de Apple ajoutées.");
        }

        @Test
        @DisplayName("Devrait gérer une quantité de 1")
        void shouldHandleSingleUnit() {
            String message = MessageFormatter.formatAddConfirmation("Apple", 1);
            assertThat(message).isEqualTo("1 unités de Apple ajoutées.");
        }
    }

    @Nested
    @DisplayName("Tests pour le formatage des messages de confirmation de suppression")
    class RemoveConfirmationTests {
        @Test
        @DisplayName("Devrait formater un message de confirmation de suppression")
        void shouldFormatRemoveConfirmation() {
            String message = MessageFormatter.formatRemoveConfirmation("Apple", 5);
            assertThat(message).isEqualTo("5 unités de Apple supprimées.");
        }

        @Test
        @DisplayName("Devrait gérer une quantité de 1")
        void shouldHandleSingleUnit() {
            String message = MessageFormatter.formatRemoveConfirmation("Apple", 1);
            assertThat(message).isEqualTo("1 unités de Apple supprimées.");
        }
    }

    @Nested
    @DisplayName("Tests pour le formatage des messages de suppression complète")
    class CompleteRemovalTests {
        @Test
        @DisplayName("Devrait formater un message de suppression complète")
        void shouldFormatCompleteRemoval() {
            String message = MessageFormatter.formatCompleteRemoval("Apple");
            assertThat(message).isEqualTo("L'article Apple a été complètement supprimé.");
        }
    }

    @Nested
    @DisplayName("Tests pour le formatage des messages de liste vide")
    class EmptyListTests {
        @Test
        @DisplayName("Devrait formater un message de liste vide")
        void shouldFormatEmptyList() {
            String message = MessageFormatter.formatEmptyList();
            assertThat(message).isEqualTo("La liste de courses est vide.");
        }
    }

    @Nested
    @DisplayName("Tests pour le formatage des messages de catégorie vide")
    class EmptyCategoryTests {
        @Test
        @DisplayName("Devrait formater un message de catégorie vide")
        void shouldFormatEmptyCategory() {
            String message = MessageFormatter.formatEmptyCategory("Fruits");
            assertThat(message).isEqualTo("Aucun article dans la catégorie: Fruits");
        }
    }

    @Nested
    @DisplayName("Tests pour le formatage des messages d'erreur")
    class ErrorMessageTests {
        @Test
        @DisplayName("Devrait formater un message d'erreur pour un article inexistant")
        void shouldFormatItemNotFound() {
            String message = MessageFormatter.formatItemNotFound("Apple");
            assertThat(message).isEqualTo("L'article Apple n'existe pas dans la liste.");
        }

        @Test
        @DisplayName("Devrait formater un message d'erreur pour une quantité invalide")
        void shouldFormatInvalidQuantity() {
            String message = MessageFormatter.formatInvalidQuantity("Apple", 3, 5);
            assertThat(message).isEqualTo("Impossible de supprimer 5 unités de Apple. Quantité disponible: 3");
        }

        @Test
        @DisplayName("Devrait formater un message d'erreur pour un format de stockage invalide")
        void shouldFormatInvalidStorageFormat() {
            String message = MessageFormatter.formatInvalidStorageFormat();
            assertThat(message).isEqualTo("Format de stockage non supporté. Utilisez 'json' ou 'csv'.");
        }

        @Test
        @DisplayName("Devrait formater un message d'erreur pour un fichier manquant")
        void shouldFormatFileNotFound() {
            String message = MessageFormatter.formatFileNotFound("grocery_list.json");
            assertThat(message).isEqualTo("Le fichier grocery_list.json n'existe pas.");
        }

        @Test
        @DisplayName("Devrait formater un message d'erreur pour une commande inconnue")
        void shouldFormatUnknownCommand() {
            String message = MessageFormatter.formatUnknownCommand("unknown");
            assertThat(message).isEqualTo("Commande inconnue : unknown");
        }
    }
} 