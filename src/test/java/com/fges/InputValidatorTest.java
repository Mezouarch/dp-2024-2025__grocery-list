package com.fges;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

class InputValidatorTest {

    @Nested
    @DisplayName("Tests pour la validation des noms d'articles")
    class ItemNameValidationTests {
        @Test
        @DisplayName("Devrait accepter un nom d'article valide")
        void shouldAcceptValidItemName() {
            assertThatCode(() -> InputValidator.validateItemName("Apple"))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Devrait rejeter un nom d'article null")
        void shouldRejectNullItemName() {
            assertThatThrownBy(() -> InputValidator.validateItemName(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Le nom de l'article ne peut pas être vide.");
        }

        @Test
        @DisplayName("Devrait rejeter un nom d'article vide")
        void shouldRejectEmptyItemName() {
            assertThatThrownBy(() -> InputValidator.validateItemName(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Le nom de l'article ne peut pas être vide.");
        }

        @Test
        @DisplayName("Devrait rejeter un nom d'article contenant uniquement des espaces")
        void shouldRejectWhitespaceOnlyItemName() {
            assertThatThrownBy(() -> InputValidator.validateItemName("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Le nom de l'article ne peut pas être vide.");
        }
    }

    @Nested
    @DisplayName("Tests pour la validation des quantités")
    class QuantityValidationTests {
        @Test
        @DisplayName("Devrait accepter une quantité positive")
        void shouldAcceptPositiveQuantity() {
            assertThatCode(() -> InputValidator.validateQuantity(5))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Devrait accepter une quantité négative")
        void shouldAcceptNegativeQuantity() {
            assertThatCode(() -> InputValidator.validateQuantity(-5))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Devrait rejeter une quantité nulle")
        void shouldRejectZeroQuantity() {
            assertThatThrownBy(() -> InputValidator.validateQuantity(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La quantité doit être différente de zéro.");
        }

        @ParameterizedTest
        @ValueSource(strings = {"1", "-1", "100", "-100"})
        @DisplayName("Devrait parser et valider des quantités valides")
        void shouldParseAndValidateValidQuantities(String quantityStr) {
            assertThatCode(() -> InputValidator.parseAndValidateQuantity(quantityStr))
                .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(strings = {"0", "abc", "1.5", ""})
        @DisplayName("Devrait rejeter des quantités invalides")
        void shouldRejectInvalidQuantities(String quantityStr) {
            assertThatThrownBy(() -> InputValidator.parseAndValidateQuantity(quantityStr))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Tests pour la validation des arguments de commande")
    class CommandArgsValidationTests {
        @Test
        @DisplayName("Devrait accepter des arguments valides")
        void shouldAcceptValidArgs() {
            List<String> args = Arrays.asList("add", "Apple", "5");
            assertThatCode(() -> InputValidator.validateCommandArgs(args, 3, "add"))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Devrait rejeter des arguments insuffisants")
        void shouldRejectInsufficientArgs() {
            List<String> args = Arrays.asList("add", "Apple");
            assertThatThrownBy(() -> InputValidator.validateCommandArgs(args, 3, "add"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Arguments manquants pour la commande 'add'.");
        }

        @Test
        @DisplayName("Devrait rejeter une liste d'arguments null")
        void shouldRejectNullArgs() {
            assertThatThrownBy(() -> InputValidator.validateCommandArgs(null, 1, "list"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Arguments manquants pour la commande 'list'.");
        }
    }

    @Nested
    @DisplayName("Tests pour la validation des noms de fichiers")
    class FileNameValidationTests {
        @Test
        @DisplayName("Devrait accepter un nom de fichier valide")
        void shouldAcceptValidFileName() {
            assertThatCode(() -> InputValidator.validateFileName("grocery_list.json"))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Devrait rejeter un nom de fichier null")
        void shouldRejectNullFileName() {
            assertThatThrownBy(() -> InputValidator.validateFileName(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Le nom du fichier ne peut pas être vide.");
        }

        @Test
        @DisplayName("Devrait rejeter un nom de fichier vide")
        void shouldRejectEmptyFileName() {
            assertThatThrownBy(() -> InputValidator.validateFileName(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Le nom du fichier ne peut pas être vide.");
        }
    }

    @Nested
    @DisplayName("Tests pour la validation des formats de stockage")
    class StorageFormatValidationTests {
        @Test
        @DisplayName("Devrait accepter le format JSON")
        void shouldAcceptJsonFormat() {
            assertThatCode(() -> InputValidator.validateStorageFormat("json"))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Devrait accepter le format CSV")
        void shouldAcceptCsvFormat() {
            assertThatCode(() -> InputValidator.validateStorageFormat("csv"))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Devrait rejeter un format null")
        void shouldRejectNullFormat() {
            assertThatThrownBy(() -> InputValidator.validateStorageFormat(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Le format de stockage ne peut pas être vide.");
        }

        @Test
        @DisplayName("Devrait rejeter un format non supporté")
        void shouldRejectUnsupportedFormat() {
            assertThatThrownBy(() -> InputValidator.validateStorageFormat("xml"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Format de stockage non supporté. Utilisez 'json' ou 'csv'.");
        }
    }
} 