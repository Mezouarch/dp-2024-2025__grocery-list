package com.fges;

import java.util.List;
import java.util.Map;

import fr.anthonyquere.GroceryShopServer;

/**
 * Commande pour démarrer le serveur web.
 */
public class WebCommand implements Command {

    @Override
    public String execute(List<String> args, GroceryManager groceryManager, String category) {
        if (args.size() < 2) {
            throw new IllegalArgumentException("Port non spécifié. Usage: web <port>");
        }

        int port;
        try {
            port = Integer.parseInt(args.get(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Port invalide. Le port doit être un nombre entier.");
        }

        // Vérifier que le port est valide
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Port invalide. Le port doit être entre 1 et 65535.");
        }

        try {
            // Créer une instance de SimpleGroceryShop avec les données de GroceryManager
            SimpleGroceryShop groceryShop = new SimpleGroceryShop();
            
            // Récupérer les articles par catégorie
            Map<String, List<String>> itemsByCategory = groceryManager.getGroceryListByCategory();
            
            // Parcourir les articles et les ajouter au SimpleGroceryShop
            for (Map.Entry<String, List<String>> categoryEntry : itemsByCategory.entrySet()) {
                String categoryName = categoryEntry.getKey();
                List<String> items = categoryEntry.getValue();
                
                for (String itemString : items) {
                    // Analyser la chaîne au format "nom: quantité"
                    String[] parts = itemString.split(":");
                    if (parts.length >= 2) {
                        String itemName = parts[0].trim();
                        int quantity = Integer.parseInt(parts[1].trim());
                        
                        // Ajouter l'article au SimpleGroceryShop
                        groceryShop.addGroceryItem(itemName, quantity, categoryName);
                    }
                }
            }
            
            // Démarrer le serveur web
            GroceryShopServer server = new GroceryShopServer(groceryShop);
            server.start(port);
            
            return "Serveur web démarré sur http://localhost:" + port;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du démarrage du serveur web: " + e.getMessage(), e);
        }
    }
}