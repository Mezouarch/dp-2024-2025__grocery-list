package com.fges.commands;

import com.fges.Command;
import com.fges.model.GroceryManager;
import com.fges.model.CommandOptions;
import com.fges.web.SynchronizedGroceryShop;

import fr.anthonyquere.GroceryShopServer;
import fr.anthonyquere.MyGroceryShop;

import java.util.List;

/**
 * Commande pour démarrer le serveur web avec synchronisation des modifications.
 */
public class WebCommand implements Command {

    @Override
    public String execute(List<String> args, GroceryManager groceryManager, CommandOptions options) throws Exception {
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
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Port invalide. Le port doit être compris entre 0 et 65535.");
        }

        // Création du serveur web
        createServer(groceryManager, port, options);

        return "Serveur web démarré sur le port " + port;
    }

    /**
     * Crée et démarre le serveur web
     * 
     * @param groceryManager gestionnaire de liste de courses
     * @param port port sur lequel démarrer le serveur
     * @param options options de la commande contenant le nom du fichier
     * @return l'instance du serveur créé
     */
    protected GroceryShopServer createServer(GroceryManager groceryManager, int port, CommandOptions options) {
        // Création d'une interface synchronisée pour la liste de courses
        MyGroceryShop synchronizedShop = new SynchronizedGroceryShop(groceryManager, options);
        
        // Démarrage du serveur web
        GroceryShopServer server = new GroceryShopServer(synchronizedShop);
        server.start(port);
        
        return server;
    }
} 