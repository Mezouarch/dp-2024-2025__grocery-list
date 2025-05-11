package com.fges.model.strategies;

import com.fges.model.GroceryItem;
import java.util.List;

/**
 * Interface définissant une stratégie d'opération sur les articles de la liste de courses.
 * Applique le pattern Stratégie pour découpler les différentes opérations de la classe GroceryManager.
 */
public interface ItemOperationStrategy {
    /**
     * Exécute l'opération sur la liste d'articles.
     * Cette méthode traite l'opération et retourne une nouvelle liste d'articles résultant de l'opération,
     * sans modifier la liste d'entrée.
     *
     * @param items liste actuelle d'articles (ne sera pas modifiée)
     * @param itemName nom de l'article concerné par l'opération
     * @param quantity quantité (pour ajout/mise à jour d'articles, 0 si non applicable)
     * @param category catégorie (pour ajout/mise à jour d'articles, null si non applicable)
     * @return une nouvelle liste d'articles après application de l'opération
     * @throws Exception si l'opération échoue (article introuvable, paramètres invalides, etc.)
     */
    List<GroceryItem> execute(List<GroceryItem> items, String itemName, int quantity, String category) throws Exception;
} 