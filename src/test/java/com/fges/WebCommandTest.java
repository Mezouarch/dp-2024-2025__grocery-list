package com.fges;

import com.fges.commands.WebCommand;
import com.fges.model.GroceryManager;
import com.fges.model.CommandOptions;
import com.fges.storage.JsonStorageManager;

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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WebCommandTest {
    private WebCommand webCommand;
    private GroceryManager groceryManager;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        webCommand = new WebCommand();
        groceryManager = new GroceryManager(new JsonStorageManager());
        File testFile = tempDir.resolve("test_grocery_list.json").toFile();
        TestUtils.createEmptyJsonFile(testFile);
        groceryManager.loadGroceryList(testFile.getPath());
    }

    @Nested
    @DisplayName("Tests pour la validation des arguments")
    class ArgumentValidationTests {
        @Test
        @DisplayName("Devrait rejeter quand le port n'est pas spécifié")
        void shouldRejectWhenPortNotSpecified() {
            List<String> args = List.of("web");
            CommandOptions options = new CommandOptions.Builder().build();
            
            assertThatThrownBy(() -> webCommand.execute(args, groceryManager, options))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Port non spécifié. Usage: web <port>");
        }

        @Test
        @DisplayName("Devrait rejeter quand le port n'est pas un nombre")
        void shouldRejectWhenPortIsNotANumber() {
            List<String> args = Arrays.asList("web", "invalidPort");
            CommandOptions options = new CommandOptions.Builder().build();
            
            assertThatThrownBy(() -> webCommand.execute(args, groceryManager, options))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Port invalide. Le port doit être un nombre entier.");
        }

        @Test
        @DisplayName("Devrait rejeter quand le port est hors limites (trop petit)")
        void shouldRejectWhenPortIsTooSmall() {
            List<String> args = Arrays.asList("web", "0");
            CommandOptions options = new CommandOptions.Builder().build();
            
            assertThatThrownBy(() -> webCommand.execute(args, groceryManager, options))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Port invalide. Le port doit être compris entre 0 et 65535.");
        }

        @Test
        @DisplayName("Devrait rejeter quand le port est hors limites (trop grand)")
        void shouldRejectWhenPortIsTooLarge() {
            List<String> args = Arrays.asList("web", "65536");
            CommandOptions options = new CommandOptions.Builder().build();
            
            assertThatThrownBy(() -> webCommand.execute(args, groceryManager, options))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Port invalide. Le port doit être compris entre 0 et 65535.");
        }
    }
} 