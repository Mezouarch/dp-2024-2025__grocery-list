package com.fges;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InfoCommand implements Command {
    @Override
    public String execute(List<String> args, GroceryManager groceryManager, String category) throws Exception {
        // Get today's date
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = today.format(formatter);

        // Get OS name
        String osName = System.getProperty("os.name");

        // Get Java version
        String javaVersion = System.getProperty("java.version");

        // Build and return the information string
        StringBuilder info = new StringBuilder();
        info.append("Today's Date: ").append(formattedDate).append("\n");
        info.append("Operating System: ").append(osName).append("\n");
        info.append("Java Version: ").append(javaVersion);

        return info.toString();
    }
} 