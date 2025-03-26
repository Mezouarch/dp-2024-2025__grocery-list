package com.fges;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class RemoveCommandTest {
    private RemoveCommand removeCommand;
    private GroceryManager groceryManager;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        removeCommand = new RemoveCommand();
        groceryManager = new GroceryManager();
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void shouldRemoveSpecificQuantityOfItem() throws Exception {
        // First add an item
        groceryManager.addItem("Milk", 3);
        
        List<String> args = Arrays.asList("remove", "Milk", "1");
        removeCommand.execute(args, groceryManager);

        assertThat(groceryManager.getGroceryList())
            .containsExactly("Milk: 2");
    }

    @Test
    void shouldRemoveEntireItemWhenNoQuantitySpecified() throws Exception {
        // First add an item
        groceryManager.addItem("Milk", 3);
        
        List<String> args = Arrays.asList("remove", "Milk");
        removeCommand.execute(args, groceryManager);

        assertThat(groceryManager.getGroceryList())
            .isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenRemovingNonExistentItem() {
        List<String> args = Arrays.asList("remove", "Milk");
        
        assertThatThrownBy(() -> removeCommand.execute(args, groceryManager))
            .isInstanceOf(Exception.class)
            .hasMessageContaining("n'existe pas dans la liste");
    }

    @Test
    void shouldThrowExceptionWithInsufficientArguments() {
        assertThatThrownBy(() -> removeCommand.execute(Arrays.asList("remove"), groceryManager))
            .isInstanceOf(Exception.class)
            .hasMessageContaining("Arguments manquants");
    }

    @Test
    void shouldOutputMessageWhenCompletingRemoving() throws Exception {
        // First add an item
        groceryManager.addItem("Milk", 3);
        
        List<String> args = Arrays.asList("remove", "Milk");
        removeCommand.execute(args, groceryManager);

        assertThat(outContent.toString().trim())
            .contains("L'article Milk a été complètement supprimé");
    }
}