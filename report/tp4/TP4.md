## Introduction
Ce rapport présente une analyse du projet de gestion de listes de courses. Toutes les fonctionnalités prévues ont été implémentées, et le code repose sur une architecture modulaire et extensible, intégrant plusieurs modèles de conception.

## Éléments non réalisés faute de temps
Après analyse du code fourni, toutes les fonctionnalités prévues ont bien été implémentées. Toutefois, nous proposons plusieurs pistes d’amélioration pour enrichir davantage le projet :

1. Mise en place d’une synchronisation en temps réel : Le système actuel repose sur des sauvegardes manuelles, ce qui pourrait être optimisé.

2. Prise en charge de formats de stockage supplémentaires : À ce jour, seuls les formats JSON et CSV sont pris en charge.

3. Ajout de fonctionnalités avancées : Intégration de la gestion des prix, des dates de péremption ou encore d’un historique des achats.

## D'après l'analyse du code, les aspects suivants semblent avoir présenté des défis particuliers :

1. Gestion de la rétrocompatibilité : Un effort significatif est consacré à maintenir la compatibilité avec les anciens formats de fichiers, tant pour JSON que pour CSV.

2. Traitement des caractères spéciaux dans les formats CSV et JSON, nécessitant des fonctions d'échappement et de désescapeCSV personnalisées.

3. Architecture modulaire permettant l'extension tout en maintenant la cohérence du système.

4. Synchronisation entre l'interface web et le modèle de données : la classe SynchronizedGroceryShop révèle la complexité de maintenir la cohérence entre l'interface web et le stockage sous-jacent.

6. Gestion des catégories qui semble avoir été ajoutée ultérieurement et a nécessité des adaptations dans plusieurs parties du code.

7. Éviter les duplications d'articles tout en préservant l'intégrité des données lors des mises à jour

## Plusieurs modèles de conception ont été implémentés dans l'application :

1. Factory Pattern (StorageManagerFactory) :

est utilisé pour encapsuler la création d'objets dans une classe dédiée, isolant ainsi le code client des détails d'instanciation.
Avantage : Encapsule la logique de création et facilite l'ajout de nouveaux formats de stockage.

2. Strategy Pattern (interface StorageManager) :

Permet de définir une famille d'algorithmes de stockage interchangeables (JSON, CSV).
Avantage : Les stratégies peuvent être sélectionnées à l'exécution.

3. Command Pattern (interface Command et ses implémentations) :

Encapsule les différentes actions que l'utilisateur peut effectuer (ajouter, supprimer, lister...).
Avantage : Facilite l'ajout de nouvelles commandes et la séparation des responsabilités.

4. Builder Pattern (dans CommandOptions.Builder) :

Utilisé pour construire des objets complexes étape par étape.
Avantage : Code plus lisible et flexible pour la création d'objets avec de nombreux paramètres.

5. Adapter Pattern (dans SimpleGroceryShop et SynchronizedGroceryShop) :

Adapte l'interface du gestionnaire de courses pour qu'elle soit compatible avec l'interface web.
Avantage : Permet l'interopérabilité entre des interfaces incompatibles.

## Pour ajouter une nouvelle commande, il faudrait suivre ces étapes :

1. Créer une nouvelle classe qui implémente l'interface Command (par exemple NewCommand.java dans le package com.fges.commands).

2. Implémenter la méthode execute() avec la logique spécifique à cette commande.

3. Modifier la méthode getCommand() dans CLIHandler.java pour ajouter un nouveau cas dans le switch qui retourne une instance de la nouvelle commande.

4. Si nécessaire, déterminer si la commande requiert un fichier et une sauvegarde en modifiant les méthodes commandRequiresNoFile() et commandRequiresSaving().

## Pour ajouter un nouveau format de stockage (par exemple XML) :

1. Créer une nouvelle classe implémentant l'interface StorageManager (par exemple XmlStorageManager.java dans le package com.fges.storage).

2. Implémenter les méthodes saveGroceryList() et loadGroceryList() pour gérer le nouveau format.

3. Modifier la classe StorageManagerFactory pour reconnaître et créer des instances du nouveau gestionnaire de stockage.

4. Mettre à jour la méthode isValidStorageFormat() dans InputValidator.java pour accepter le nouveau format.

5. Mettre à jour les messages d'erreur dans MessageFormatter.java pour inclure le nouveau format.

## Pour ajouter la possibilité de spécifier différents magasins :

1. Modifier la classe GroceryItem pour inclure un attribut store (magasin).

2. Mettre à jour les formats de stockage (CSV et JSON) pour inclure cette nouvelle information :

2.1 Pour CSV, ajouter une colonne store dans l'en-tête et dans les lignes.
2.2 Pour JSON, ajouter un champ store dans les objets d'articles.


3. Étendre l'interface Command et ses implémentations pour accepter un paramètre de magasin.

4. Ajouter une option --store dans CLIHandler et CommandOptions.

5. Mettre à jour l'interface web pour afficher et permettre la modification des magasins.

6. Créer une nouvelle commande "store" qui permettrait de filtrer les articles par magasin.

## Conclusion 

Le projet est fonctionnel et bien structuré. Des améliorations restent possibles, notamment sur la synchronisation, les formats de stockage et de nouvelles fonctionnalités, mais la base est solide pour aller plus loin.

