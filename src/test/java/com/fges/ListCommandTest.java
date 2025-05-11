package com.fges;

import com.fges.commands.ListCommand;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ListCommandTest {
    private ListCommand listCommand;
    private GroceryManager groceryManager;
    
    @TempDir
    Path tempDir;
    private File testFile;

    @BeforeEach
    void setUp() throws IOException {
        listCommand = new ListCommand();
        groceryManager = new GroceryManager(new JsonStorageManager());
        testFile = tempDir.resolve("test_grocery_list.json").toFile();
        TestUtils.createEmptyJsonFile(testFile);
        groceryManager.loadGroceryList(testFile.getPath());
    }

    @Test
    @DisplayName("Devrait retourner un message pour une liste vide")
    void shouldReturnMessageForEmptyList() throws Exception {
        List<String> args = new ArrayList<>();
        CommandOptions options = new CommandOptions.Builder().build();
        
        String result = listCommand.execute(args, groceryManager, options);
        
        assertThat(result).contains("La liste de courses est vide");
    }

    @Test
    @DisplayName("Devrait afficher les articles par catégorie")
    void shouldDisplayItemsByCategory() throws Exception {
        // Ajouter quelques articles avec différentes catégories
        groceryManager.addItem("Pommes", 3, "Fruits");
        groceryManager.addItem("Carottes", 2, "Légumes");
        groceryManager.addItem("Poulet", 1, "Viande");
        
        List<String> args = new ArrayList<>();
        CommandOptions options = new CommandOptions.Builder().build();
        
        String result = listCommand.execute(args, groceryManager, options);
        
        // Vérifier que le résultat contient toutes les catégories
        assertThat(result).contains("Fruits");
        assertThat(result).contains("Légumes");
        assertThat(result).contains("Viande");
        
        // Vérifier que le résultat contient tous les articles
        assertThat(result).contains("Pommes: 3");
        assertThat(result).contains("Carottes: 2");
        assertThat(result).contains("Poulet: 1");
    }

    @Test
    @DisplayName("Devrait filtrer les articles par catégorie")
    void shouldFilterItemsByCategory() throws Exception {
        // Ajouter quelques articles avec différentes catégories
        groceryManager.addItem("Pommes", 3, "Fruits");
        groceryManager.addItem("Bananes", 4, "Fruits");
        groceryManager.addItem("Carottes", 2, "Légumes");
        
        List<String> args = new ArrayList<>();
        CommandOptions options = new CommandOptions.Builder()
                .category("Fruits")
                .build();
        
        String result = listCommand.execute(args, groceryManager, options);
        
        // Vérifier que le résultat contient la catégorie Fruits
        assertThat(result).contains("Fruits");
        
        // Vérifier que le résultat contient les fruits
        assertThat(result).contains("Pommes: 3");
        assertThat(result).contains("Bananes: 4");
        
        // Vérifier que le résultat ne contient pas les légumes
        assertThat(result).doesNotContain("Légumes");
        assertThat(result).doesNotContain("Carottes: 2");
    }
} 