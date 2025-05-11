package com.fges;

import com.fges.util.InputValidator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class InputValidatorTest {

    @Nested
    @DisplayName("Tests pour la validation des noms d'articles")
    class ItemNameValidationTests {
        @Test
        @DisplayName("Devrait accepter un nom d'article valide")
        void shouldAcceptValidItemName() {
            assertTrue(InputValidator.isValidItemName("Apple"));
        }

        @Test
        @DisplayName("Devrait rejeter un nom d'article null")
        void shouldRejectNullItemName() {
            assertFalse(InputValidator.isValidItemName(null));
        }

        @Test
        @DisplayName("Devrait rejeter un nom d'article vide")
        void shouldRejectEmptyItemName() {
            assertFalse(InputValidator.isValidItemName(""));
        }

        @Test
        @DisplayName("Devrait rejeter un nom d'article contenant uniquement des espaces")
        void shouldRejectWhitespaceOnlyItemName() {
            assertFalse(InputValidator.isValidItemName("   "));
        }
        
        @Test
        @DisplayName("Devrait rejeter un nom d'article contenant des caractères spéciaux")
        void shouldRejectItemNameWithSpecialChars() {
            assertFalse(InputValidator.isValidItemName("Apple,"));
            assertFalse(InputValidator.isValidItemName("Apple:"));
            assertFalse(InputValidator.isValidItemName("Apple;"));
        }
    }

    @Nested
    @DisplayName("Tests pour la validation des quantités")
    class QuantityValidationTests {
        @Test
        @DisplayName("Devrait accepter une quantité positive")
        void shouldAcceptPositiveQuantity() {
            assertTrue(InputValidator.isValidQuantity(5));
        }

        @Test
        @DisplayName("Devrait rejeter une quantité négative")
        void shouldRejectNegativeQuantity() {
            assertFalse(InputValidator.isValidQuantity(-5));
        }

        @Test
        @DisplayName("Devrait rejeter une quantité nulle")
        void shouldRejectZeroQuantity() {
            assertFalse(InputValidator.isValidQuantity(0));
        }
    }

    @Nested
    @DisplayName("Tests pour la validation des noms de fichiers")
    class FileNameValidationTests {
        @Test
        @DisplayName("Devrait accepter un nom de fichier valide")
        void shouldAcceptValidFileName() {
            assertTrue(InputValidator.isValidFileName("grocery_list.json"));
        }

        @Test
        @DisplayName("Devrait rejeter un nom de fichier null")
        void shouldRejectNullFileName() {
            assertFalse(InputValidator.isValidFileName(null));
        }

        @Test
        @DisplayName("Devrait rejeter un nom de fichier vide")
        void shouldRejectEmptyFileName() {
            assertFalse(InputValidator.isValidFileName(""));
        }
        
        @Test
        @DisplayName("Devrait rejeter un nom de fichier avec caractères spéciaux")
        void shouldRejectFileNameWithSpecialChars() {
            assertFalse(InputValidator.isValidFileName("file:name"));
            assertFalse(InputValidator.isValidFileName("file/name"));
            assertFalse(InputValidator.isValidFileName("file\\name"));
        }
    }

    @Nested
    @DisplayName("Tests pour la validation des formats de stockage")
    class StorageFormatValidationTests {
        @Test
        @DisplayName("Devrait accepter le format JSON")
        void shouldAcceptJsonFormat() {
            assertTrue(InputValidator.isValidStorageFormat("json"));
        }

        @Test
        @DisplayName("Devrait accepter le format CSV")
        void shouldAcceptCsvFormat() {
            assertTrue(InputValidator.isValidStorageFormat("csv"));
        }

        @Test
        @DisplayName("Devrait rejeter un format null")
        void shouldRejectNullFormat() {
            assertFalse(InputValidator.isValidStorageFormat(null));
        }

        @Test
        @DisplayName("Devrait rejeter un format non supporté")
        void shouldRejectUnsupportedFormat() {
            assertFalse(InputValidator.isValidStorageFormat("xml"));
        }
    }
    
    @Nested
    @DisplayName("Tests pour la validation des ports")
    class PortValidationTests {
        @Test
        @DisplayName("Devrait accepter un port valide")
        void shouldAcceptValidPort() {
            assertTrue(InputValidator.isValidPort(8080));
            assertTrue(InputValidator.isValidPort(1));
            assertTrue(InputValidator.isValidPort(65535));
        }
        
        @Test
        @DisplayName("Devrait rejeter un port invalide")
        void shouldRejectInvalidPort() {
            assertFalse(InputValidator.isValidPort(0));
            assertFalse(InputValidator.isValidPort(-1));
            assertFalse(InputValidator.isValidPort(65536));
        }
    }
    
    @Nested
    @DisplayName("Tests pour la validation des catégories")
    class CategoryValidationTests {
        @Test
        @DisplayName("Devrait accepter une catégorie valide")
        void shouldAcceptValidCategory() {
            assertTrue(InputValidator.isValidCategory("fruits"));
        }
        
        @Test
        @DisplayName("Devrait rejeter une catégorie null ou vide")
        void shouldRejectNullOrEmptyCategory() {
            assertFalse(InputValidator.isValidCategory(null));
            assertFalse(InputValidator.isValidCategory(""));
            assertFalse(InputValidator.isValidCategory("   "));
        }
        
        @Test
        @DisplayName("Devrait rejeter une catégorie avec caractères spéciaux")
        void shouldRejectCategoryWithSpecialChars() {
            assertFalse(InputValidator.isValidCategory("fruits,"));
            assertFalse(InputValidator.isValidCategory("fruits:"));
            assertFalse(InputValidator.isValidCategory("fruits;"));
        }
    }
} 