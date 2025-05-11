package com.fges;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestUtils {
    /**
     * Crée un fichier JSON vide avec une structure valide.
     *
     * @param file le fichier à créer
     * @throws IOException en cas d'erreur lors de la création du fichier
     */
    public static void createEmptyJsonFile(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("[]");
        }
    }
} 