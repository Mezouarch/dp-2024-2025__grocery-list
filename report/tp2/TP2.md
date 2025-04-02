# TP2 - Ajout des catégories et tests unitaires

## Introduction

Dans ce TP2, on a amélioré la qualité du code en ajoutant des tests unitaires complets et en réorganisant la structure du code pour une meilleure maintenabilité. Les principales améliorations incluent :

1. L'ajout de catégories (default par défaut)
2. L'ajout de tests unitaires pour toutes les classes principales
3. La réorganisation du code en classes plus spécialisées
4. L'amélioration de la gestion des erreurs
5. L'ajout de documentation détaillée

## L'ajout de catégories
Pour ajouter des catégories, l'utilisateur pourrait executer les lignes suivantes:
```bash
# Add milk to the "dairy" category
java -jar ./target/dp-2024-2025__grocery-list-1.0-SNAPSHOT.jar -s my_file.json --category dairy add "Milk" 10
java -jar ./target/dp-2024-2025__grocery-list-1.0-SNAPSHOT.jar -s my_file.json --category dairy add "Tea" 999

# Add coffee without category (goes to "default")
java -jar ./target/dp-2024-2025__grocery-list-1.0-SNAPSHOT.jar -s my_file.json add "Coffee" 2

# List all items (grouped by category)
java -jar ./target/dp-2024-2025__grocery-list-1.0-SNAPSHOT.jar -s my_file.json list
```
L'utilisateur peut choisir aussi l'extention csv
## Problèmes identifiés dans le TP1

Dans le TP1, on avait identifié plusieurs problèmes :

1. **Manque de tests** : Le code n'était pas testé, ce qui rendait difficile la détection des bugs
2. **Structure monolithique** : Le code était concentré dans quelques classes
3. **Gestion des erreurs insuffisante** : Les messages d'erreur n'étaient pas standardisés
4. **Documentation limitée** : Le code manquait de documentation

## Solutions implémentées

### 1. Tests Unitaires

On a créé des tests unitaires complets pour chaque classe principale :

- **CategoryManagerTest** : Tests pour la gestion des catégories
  - Ajout d'articles
  - Suppression d'articles
  - Récupération des catégories
  - Gestion des cas particuliers

- **InputValidatorTest** : Tests pour la validation des entrées
  - Validation des noms d'articles
  - Validation des quantités
  - Validation des arguments de commande
  - Validation des noms de fichiers
  - Validation des formats de stockage

- **MessageFormatterTest** : Tests pour le formatage des messages
  - Messages de confirmation
  - Messages d'erreur
  - Messages de liste vide
  - Messages de catégorie vide

- **GroceryManagerTest** : Tests pour la gestion principale
  - Ajout d'articles
  - Suppression d'articles
  - Gestion des catégories
  - Persistance des données
  - Sauvegarde automatique

- **CommandTests** : Tests pour les commandes
  - AddCommandTest
  - RemoveCommandTest
  - ListCommandTest

### 2. Réorganisation du Code

On a créé de nouvelles classes pour une meilleure séparation des responsabilités :

- **CategoryManager** : Gestion des catégories
- **InputValidator** : Validation des entrées
- **MessageFormatter** : Formatage des messages
- **StorageManager** : Interface pour la gestion du stockage
  - JsonStorageManager
  - CsvStorageManager

### 3. Amélioration de la Gestion des Erreurs

On a standardisé la gestion des erreurs avec :
- Des messages d'erreur cohérents
- Des validations plus strictes
- Une meilleure gestion des cas limites


## Difficultés rencontrées

1. **Création des tests** :
   - Identification des cas de test pertinents
   - Gestion des dépendances entre les tests
   - Simulation des erreurs

2. **Réorganisation du code** :
   - Identification des responsabilités
   - Gestion des dépendances circulaires
   - Maintien de la compatibilité

3. **Gestion des fichiers de test** :
   - Création de fichiers temporaires
   - Nettoyage après les tests
   - Isolation des tests

## Succès obtenus

1. **Couverture de tests** :
   - Tests unitaires complets
   - Tests des cas limites
   - Tests des cas d'erreur

2. **Qualité du code** :
   - Code plus modulaire
   - Meilleure séparation des responsabilités
   - Plus facile à maintenir

3. **Robustesse** :
   - Meilleure gestion des erreurs
   - Validation plus stricte
   - Messages d'erreur plus clairs

## Schéma du projet

Voici un diagramme de classes exhaustif du système :

![Diagramme de classes du système de gestion de liste de courses](schema.png)