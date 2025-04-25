# Introduction
Dans ce TP3, l'objectif principal était d'étendre l'application de gestion de liste de courses pour intégrer une nouvelle commande : info. Cette commande permet d’afficher des informations système utiles, notamment la date actuelle, le système d’exploitation et la version de Java. Cette évolution permet de tester l’extensibilité du système de commandes et d’enrichir les fonctionnalités de l’application tout en gardant une interface simple et uniforme.

# Fonctionnalité ajoutée : commande info
## Description
La commande info est une commande informative ne prenant aucun argument. Si des arguments sont fournis, ils sont ignorés. Elle permet d’afficher :

La date du jour

Le nom du système d’exploitation

La version de Java

# Commande simple
java -jar ./target/dp-2024-2025_grocery-list-1.0-SNAPSHOT.jar info

# Commande avec options supplémentaires (arguments ignorés)
java -jar ./target/dp-2024-2025_grocery-list-1.0-SNAPSHOT.jar -c toto info
java -jar ./target/dp-2024-2025_grocery-list-1.0-SNAPSHOT.jar -s myFile info
java -jar ./target/dp-2024-2025_grocery-list-1.0-SNAPSHOT.jar -s myFile info -f csv

# Modifications apportées
1. Création de la classe InfoCommand
Une nouvelle classe a été introduite pour gérer la logique de la commande :

InfoCommand : récupère et affiche les informations système

LocalDate.now() pour la date

System.getProperty("os.name") pour le système

System.getProperty("java.version") pour Java

2. Intégration dans la logique de commande
Le parseur de commandes a été modifié pour reconnaître la commande info

Les arguments supplémentaires sont analysés mais ignorés proprement

L’application s’assure que info est traité indépendamment du fichier de stockage

3. Ajout de tests unitaires
Des tests ont été créés pour vérifier que :

L’exécution de la commande info affiche bien les trois éléments requis

La commande fonctionne même si d'autres options sont présentes

Les arguments superflus sont ignorés sans provoquer d'erreurs

# Difficultés rencontrées
Récupération d’informations système 

Gestion des différences selon l’environnement (Windows, macOS, Linux)

# Test de l'affichage :

Capturer la sortie standard pour vérifier les résultats dans les tests

# Résultats obtenus
La commande info fonctionne correctement et de manière robuste

Elle est intégrée dans la logique des autres commandes sans conflit

Le code reste modulaire et extensible

# Schema 

Voici le schema du système :

![Diagramme info](schema_tp3.png)
