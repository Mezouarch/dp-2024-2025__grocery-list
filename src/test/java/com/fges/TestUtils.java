package com.fges;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Crée un fichier JSON vide avec une structure valide.
     *
     * @param file le fichier à créer
     * @throws IOException en cas d'erreur lors de la création du fichier
     */
    public static void createEmptyJsonFile(File file) throws IOException {
        Map<String, Map<String, List<String>>> emptyStructure = new HashMap<>();
        emptyStructure.put("default", new HashMap<>());
        OBJECT_MAPPER.writeValue(file, emptyStructure);
    }
} 