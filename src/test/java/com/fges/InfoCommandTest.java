package com.fges;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InfoCommandTest {

    private InfoCommand infoCommand;

    @BeforeEach
    public void setUp() {
        infoCommand = new InfoCommand();
    }

    @Test
    public void testExecute_BasicFunctionality() throws Exception {
        // Arrange
        List<String> args = new ArrayList<>();
        String category = "";
        // Nous passons null pour groceryManager car InfoCommand ne l'utilise pas
        
        // Act
        String result = infoCommand.execute(args, null, category);
        
        // Assert
        // Vérifier que le résultat contient les trois informations requises
        assertTrue(result.contains("Today's Date:"));
        assertTrue(result.contains("Operating System:"));
        assertTrue(result.contains("Java Version:"));
    }
    
    @Test
    public void testExecute_IgnoresArguments() throws Exception {
        // Arrange - Créer une liste avec des arguments qui devraient être ignorés
        List<String> args = Arrays.asList("argument1", "argument2");
        String category = "someCategory";
        
        // Act
        String result = infoCommand.execute(args, null, category);
        
        // Assert
        assertTrue(result.contains("Today's Date:"));
        assertTrue(result.contains("Operating System:"));
        assertTrue(result.contains("Java Version:"));
        
        // Vérifier la structure (3 lignes)
        assertEquals(3, result.split("\n").length);
    }
    
    @Test
    public void testExecute_FormatCorrectness() throws Exception {
        // Arrange
        List<String> args = new ArrayList<>();
        String category = "";
        
        // Act
        String result = infoCommand.execute(args, null, category);
        
        // Assert
        // Vérifier que le résultat est au format exact attendu avec 3 lignes
        String[] lines = result.split("\n");
        assertEquals(3, lines.length);
        
        // Vérifier le format de chaque ligne
        assertTrue(lines[0].startsWith("Today's Date: "));
        assertTrue(lines[1].startsWith("Operating System: "));
        assertTrue(lines[2].startsWith("Java Version: "));
    }
    
    @Test
    public void testExecute_DateFormat() throws Exception {
        // Arrange
        List<String> args = new ArrayList<>();
        String category = "";
        
        // Act
        String result = infoCommand.execute(args, null, category);
        
        // Assert
        // Extraire la date du résultat
        String dateStr = result.split("\n")[0].replace("Today's Date: ", "");
        
        // Vérifier que la date est au format dd/MM/yyyy
        assertTrue(dateStr.matches("\\d{2}/\\d{2}/\\d{4}"));
        
        // Vérifier que la date est la date d'aujourd'hui
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String expectedDate = today.format(formatter);
        assertEquals(expectedDate, dateStr);
    }
}