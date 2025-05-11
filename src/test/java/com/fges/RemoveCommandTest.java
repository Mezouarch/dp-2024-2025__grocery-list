package com.fges;

import com.fges.commands.RemoveCommand;
import com.fges.model.CommandOptions;
import com.fges.model.GroceryManager;
import com.fges.storage.JsonStorageManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoveCommandTest {
    private RemoveCommand removeCommand;
    private GroceryManager groceryManager;
    
    @TempDir
    Path tempDir;
    private File testFile;

    @BeforeEach
    void setUp() throws IOException {
        removeCommand = new RemoveCommand();
        groceryManager = new GroceryManager(new JsonStorageManager());
        testFile = tempDir.resolve("test_grocery_list.json").toFile();
        TestUtils.createEmptyJsonFile(testFile);
        groceryManager.loadGroceryList(testFile.getPath());
        
        // Ajouter quelques articles pour les tests
        groceryManager.addItem("Pommes", 3, "Fruits");
        groceryManager.addItem("Carottes", 2, "Légumes");
    }

    @Test
    @DisplayName("Devrait supprimer un article existant")
    void shouldRemoveExistingItem() throws Exception {
        List<String> args = Arrays.asList("remove", "Pommes");
        CommandOptions options = new CommandOptions.Builder().build();
        
        String result = removeCommand.execute(args, groceryManager, options);
        
        assertThat(result).contains("Supprimé Pommes");
        assertThat(groceryManager.doesItemExist("Pommes")).isFalse();
    }

    @Test
    @DisplayName("Devrait échouer pour un article inexistant")
    void shouldFailForNonExistingItem() {
        List<String> args = Arrays.asList("remove", "Bananes");
        CommandOptions options = new CommandOptions.Builder().build();
        
        assertThatThrownBy(() -> removeCommand.execute(args, groceryManager, options))
            .isInstanceOf(Exception.class)
            .hasMessageContaining("Article non trouvé");
    }

    @Test
    @DisplayName("Devrait échouer si le nom de l'article est manquant")
    void shouldFailIfItemNameIsMissing() {
        List<String> args = List.of("remove");
        CommandOptions options = new CommandOptions.Builder().build();
        
        assertThatThrownBy(() -> removeCommand.execute(args, groceryManager, options))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Nom d'article manquant");
    }

    @Test
    @DisplayName("Devrait supprimer un article d'une catégorie spécifique")
    void shouldRemoveItemFromSpecificCategory() throws Exception {
        List<String> args = Arrays.asList("remove", "Pommes");
        CommandOptions options = new CommandOptions.Builder()
                .category("Fruits")
                .build();
        
        String result = removeCommand.execute(args, groceryManager, options);
        
        assertThat(result).contains("Supprimé Pommes");
        assertThat(groceryManager.doesItemExist("Pommes")).isFalse();
    }

    @Test
    @DisplayName("Devrait échouer si la catégorie n'existe pas")
    void shouldFailIfCategoryDoesNotExist() {
        List<String> args = Arrays.asList("remove", "Pommes");
        CommandOptions options = new CommandOptions.Builder()
                .category("NonExistent")
                .build();
        
        String result = null;
        try {
            result = removeCommand.execute(args, groceryManager, options);
        } catch (Exception e) {
            // Ignorer les exceptions
        }
        
        assertThat(result).isNotNull();
        assertThat(result).contains("Catégorie non trouvée");
        assertThat(groceryManager.doesItemExist("Pommes")).isTrue();
    }
} 