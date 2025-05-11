package com.fges;

import com.fges.util.MessageFormatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageFormatterTest {

    @Nested
    @DisplayName("Tests pour les messages de liste")
    class ListMessagesTests {
        @Test
        @DisplayName("Devrait formater un message pour une liste vide")
        void shouldFormatEmptyListMessage() {
            String message = MessageFormatter.formatEmptyList();
            assertEquals("La liste de courses est vide.", message);
        }

        @Test
        @DisplayName("Devrait formater un en-tête de catégorie")
        void shouldFormatCategoryHeader() {
            String message = MessageFormatter.formatCategoryHeader("Fruits");
            assertEquals("# Fruits:", message);
        }

        @Test
        @DisplayName("Devrait formater un message pour une catégorie vide")
        void shouldFormatEmptyCategoryMessage() {
            String message = MessageFormatter.formatEmptyCategory("Fruits");
            assertEquals("Aucun article dans la catégorie: Fruits", message);
        }

        @Test
        @DisplayName("Devrait formater un message pour une catégorie non trouvée")
        void shouldFormatCategoryNotFoundMessage() {
            String message = MessageFormatter.formatCategoryNotFound("Inconnu");
            assertEquals("Catégorie non trouvée : Inconnu", message);
        }
    }

    @Nested
    @DisplayName("Tests pour les messages d'article")
    class ItemMessagesTests {
        @Test
        @DisplayName("Devrait formater un message pour un article non trouvé")
        void shouldFormatItemNotFoundMessage() {
            String message = MessageFormatter.formatItemNotFound("Pomme");
            assertEquals("Article non trouvé : Pomme", message);
        }

        @Test
        @DisplayName("Devrait formater un message pour l'ajout d'un article")
        void shouldFormatItemAddedMessage() {
            String message = MessageFormatter.formatAddConfirmation("Pomme", 3, "Fruits");
            assertEquals("Ajouté 3 Pomme dans la catégorie 'Fruits'", message);
        }

        @Test
        @DisplayName("Devrait formater un message pour la suppression d'un article")
        void shouldFormatItemRemovedMessage() {
            String message = MessageFormatter.formatCompleteRemoval("Pomme");
            assertEquals("Supprimé Pomme", message);
        }
    }

    @Nested
    @DisplayName("Tests pour les messages d'erreur")
    class ErrorMessagesTests {
        @Test
        @DisplayName("Devrait formater un message pour un format de stockage invalide")
        void shouldFormatInvalidStorageFormatMessage() {
            String message = MessageFormatter.formatInvalidStorageFormat();
            assertEquals("Format de stockage non supporté. Utilisez 'json' ou 'csv'.", message);
        }

        @Test
        @DisplayName("Devrait formater un message pour un fichier non trouvé")
        void shouldFormatFileNotFoundMessage() {
            String message = MessageFormatter.formatFileNotFound("list.json");
            assertEquals("Le fichier list.json n'existe pas.", message);
        }
        
        @Test
        @DisplayName("Devrait formater un message pour une commande inconnue")
        void shouldFormatUnknownCommandMessage() {
            String message = MessageFormatter.formatUnknownCommand("unknown");
            assertEquals("Commande inconnue : unknown", message);
        }
        
        @Test
        @DisplayName("Devrait formater un message pour une quantité invalide")
        void shouldFormatInvalidQuantityMessage() {
            String message = MessageFormatter.formatInvalidQuantity("Pomme", 2, 3);
            assertEquals("Quantité invalide pour Pomme. Quantité actuelle : 2, Quantité demandée : 3", message);
        }
    }
} 