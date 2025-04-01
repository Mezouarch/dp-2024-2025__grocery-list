package com.fges;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageFormatterTest {

    @Nested
    @DisplayName("Tests pour le formatage des messages de confirmation d'ajout")
    class AddConfirmationTests {
        @Test
        @DisplayName("Devrait formater un message de confirmation d'ajout")
        void shouldFormatAddConfirmation() {
            String message = MessageFormatter.formatAddConfirmation("Apple", 5, "default");
            assertThat(message).isEqualTo("Ajouté 5 Apple dans la catégorie 'default'");
        }

        @Test
        @DisplayName("Devrait gérer une quantité de 1")
        void shouldHandleSingleUnit() {
            String message = MessageFormatter.formatAddConfirmation("Apple", 1, "default");
            assertThat(message).isEqualTo("Ajouté 1 Apple dans la catégorie 'default'");
        }

        @Test
        public void testFormatAddConfirmation() {
            assertEquals("Ajouté 2 pommes dans la catégorie 'default'", 
                MessageFormatter.formatAddConfirmation("pommes", 2, "default"));
            assertEquals("Ajouté 1 lait dans la catégorie 'default'", 
                MessageFormatter.formatAddConfirmation("lait", 1, "default"));
        }
    }

    @Nested
    @DisplayName("Tests pour le formatage des messages de confirmation de suppression")
    class RemoveConfirmationTests {
        @Test
        @DisplayName("Devrait formater un message de confirmation de suppression")
        void shouldFormatRemoveConfirmation() {
            String message = MessageFormatter.formatRemoveConfirmation("Apple", 5);
            assertThat(message).isEqualTo("Supprimé 5 Apple");
        }

        @Test
        @DisplayName("Devrait gérer une quantité de 1")
        void shouldHandleSingleUnit() {
            String message = MessageFormatter.formatRemoveConfirmation("Apple", 1);
            assertThat(message).isEqualTo("Supprimé 1 Apple");
        }
    }

    @Nested
    @DisplayName("Tests pour le formatage des messages de suppression complète")
    class CompleteRemovalTests {
        @Test
        @DisplayName("Devrait formater un message de suppression complète")
        void shouldFormatCompleteRemoval() {
            String message = MessageFormatter.formatCompleteRemoval("Apple");
            assertThat(message).isEqualTo("Supprimé Apple");
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
        @DisplayName("Devrait formater un message d'erreur pour un article non trouvé")
        void shouldFormatItemNotFound() {
            String message = MessageFormatter.formatItemNotFound("Apple");
            assertThat(message).isEqualTo("Article non trouvé : Apple");
        }

        @Test
        @DisplayName("Devrait formater un message d'erreur pour une quantité invalide")
        void shouldFormatInvalidQuantity() {
            String message = MessageFormatter.formatInvalidQuantity("Apple", 3, 5);
            assertThat(message).isEqualTo("Quantité invalide pour Apple. Quantité actuelle : 3, Quantité demandée : 5");
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