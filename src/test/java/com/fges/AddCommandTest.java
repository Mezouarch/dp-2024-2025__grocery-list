package com.fges;

import com.fges.commands.AddCommand;
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

class AddCommandTest {
    private AddCommand addCommand;
    private GroceryManager groceryManager;
    
    @TempDir
    Path tempDir;
    private File testFile;

    @BeforeEach
    void setUp() throws IOException {
        addCommand = new AddCommand();
        groceryManager = new GroceryManager(new JsonStorageManager());
        testFile = tempDir.resolve("test_grocery_list.json").toFile();
        TestUtils.createEmptyJsonFile(testFile);
        groceryManager.loadGroceryList(testFile.getPath());
    }

    @Test
    @DisplayName("Devrait ajouter un nouvel article")
    void shouldAddNewItem() throws Exception {
        List<String> args = Arrays.asList("add", "Pommes", "3");
        CommandOptions options = new CommandOptions.Builder().build();
        
        String result = addCommand.execute(args, groceryManager, options);
        
        assertThat(result).contains("Ajouté 3 Pommes");
        assertThat(groceryManager.doesItemExist("Pommes")).isTrue();
        assertThat(groceryManager.getItemQuantity("Pommes")).isEqualTo(3);
    }

    @Test
    @DisplayName("Devrait ajouter un article avec catégorie")
    void shouldAddItemWithCategory() throws Exception {
        List<String> args = Arrays.asList("add", "Pommes", "3");
        CommandOptions options = new CommandOptions.Builder()
                .category("Fruits")
                .build();
        
        String result = addCommand.execute(args, groceryManager, options);
        
        assertThat(result).contains("Ajouté 3 Pommes dans la catégorie 'Fruits'");
        assertThat(groceryManager.doesItemExist("Pommes")).isTrue();
        assertThat(groceryManager.getItemCategory("Pommes")).isEqualTo("Fruits");
    }

    @Test
    @DisplayName("Devrait mettre à jour la quantité d'un article existant")
    void shouldUpdateExistingItemQuantity() throws Exception {
        // Ajouter d'abord l'article
        groceryManager.addItem("Pommes", 2, null);
        
        List<String> args = Arrays.asList("add", "Pommes", "3");
        CommandOptions options = new CommandOptions.Builder().build();
        
        String result = addCommand.execute(args, groceryManager, options);
        
        assertThat(result).contains("Ajouté 3 Pommes");
        assertThat(groceryManager.getItemQuantity("Pommes")).isEqualTo(5);
    }

    @Test
    @DisplayName("Devrait échouer pour des arguments manquants")
    void shouldFailForMissingArgs() {
        List<String> args = Arrays.asList("add", "Pommes");
        CommandOptions options = new CommandOptions.Builder().build();
        
        assertThatThrownBy(() -> addCommand.execute(args, groceryManager, options))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Arguments manquants");
    }

    @Test
    @DisplayName("Devrait échouer pour une quantité invalide")
    void shouldFailForInvalidQuantity() {
        List<String> args = Arrays.asList("add", "Pommes", "abc");
        CommandOptions options = new CommandOptions.Builder().build();
        
        assertThatThrownBy(() -> addCommand.execute(args, groceryManager, options))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Quantité invalide");
    }

    @Test
    @DisplayName("Devrait échouer pour une quantité nulle")
    void shouldFailForZeroQuantity() {
        List<String> args = Arrays.asList("add", "Pommes", "0");
        CommandOptions options = new CommandOptions.Builder().build();
        
        assertThatThrownBy(() -> addCommand.execute(args, groceryManager, options))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("La quantité ne peut pas être zéro");
    }
} 