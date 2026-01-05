# DevP_Java

Console de restaurant Description du projet
Cette application est un petit système de commande pour restaurant conçu pour une borne interactive ou une console. Elle permet aux clients de parcourir le menu par catégorie, de personnaliser les plats, d'ajouter des articles à leur panier, de modifier leur panier et de confirmer leur commande. Une interface JavaFX communique avec un backend Javalin REST, et la modélisation du domaine métier est réalisée avec Modelio.
Fonctionnalités
Parcourir les catégories : afficher les catégories disponibles (Entrées, Plats, Desserts, Boissons) et filtrer le menu par catégorie newtab .
Détails des plats : afficher les informations obligatoires pour chaque plat – nom, prix, description, pictogramme(s), catégorie et disponibilité newtab .
Ajouter au panier et choisir la quantité : les utilisateurs peuvent ajouter un plat à leur panier et sélectionner la quantité newtab .
Personnalisation : certains plats peuvent être personnalisés (par exemple, niveau d'épices, accompagnement, boisson) newtab .
Afficher et modifier le panier : le panier peut être consulté et modifié à tout moment pendant la navigation newtab .
Confirmation de la commande : un récapitulatif final affiche le contenu de la commande et l'utilisateur peut la confirmer, générant ainsi un numéro de commande newtab .
Association facultative au client : la commande peut être associée de manière facultative à un nom ou à un identifiant client.

Architecture
Le projet suit une architecture client/serveur :
Client – une application JavaFX fournissant l'interface utilisateur, gérant la navigation et la gestion locale du panier, et communiquant avec le serveur via l'API HTTP REST.
Serveur : une API REST basée sur Javalin qui expose des points de terminaison pour récupérer les catégories et les plats, recevoir les soumissions de panier, valider les commandes et stocker les données newtab .
Modèle : le modèle de domaine UML est conçu avec Modelio et exporté dans le répertoire /modelio. Il définit des classes telles que Categorie, Plat, Panier, LignePanier, Commande, LigneCommande et Personnalisation.
Aperçu des cas d'utilisation Rôle de l'utilisateur Cas d'utilisation Visiteur Parcourir le menu, filtrer par catégorie, afficher les détails des plats, ajouter au panier, personnaliser les options des plats, modifier les quantités, afficher le récapitulatif et confirmer la commande Personnel (facultatif) Gérer la liste des plats et des catégories, mettre à jour la disponibilité, afficher les commandes confirmées Modélisation UML
Un diagramme de classes initial doit être créé dans Modelio et contenir au moins les classes suivantes :
Catégorie – libellé et icône (par exemple, entrée, plat principal, dessert, boisson).
Plat – nom, description, prix, catégorie, pictogramme(s), disponibilité.
Personnalisation – informations sur la personnalisation (niveau d'épices, accompagnement, boisson, etc.) associée à un plat.
Panier – ensemble de LignePanier pouvant être modifié par l'utilisateur.

LignePanier – association entre un plat et sa quantité actuelle dans le panier, avec une personnalisation facultative.
Commande – numéro de commande, date, total et liste de LigneCommande. Créée lorsque le panier est validé.
LigneCommande – plat, quantité et prix unitaire fixés au moment de la commande.
Ce diagramme doit être exporté (image ou XMI) dans le répertoire /modelio. Un diagramme de cas d'utilisation simple doit illustrer les principales actions (parcourir le menu, ajouter au panier, personnaliser, consulter le panier, confirmer la commande).
Structure du référentiel /client/ # Application client JavaFX /server/ # Application serveur REST Javalin /modelio/ # Modèle UML et diagrammes exportés README.md # Ce fichier
Chaque répertoire dispose de son propre système de compilation. Le référentiel doit respecter la structure et les conventions de nommage stipulées dans les exigences de livraison newtab .
Branches et contributions
Le référentiel utilise Git pour le contrôle de version newtab . Le workflow recommandé est le suivant :
Créez une branche par membre de l'équipe (par exemple, modèle, métier, interface utilisateur, API) afin de permettre un travail parallèle sans conflit.
Ne travaillez pas directement sur la branche principale. Fusionnez les modifications via des pull requests une fois qu'elles ont été révisées.
Commitez tôt et souvent ; utilisez des messages de commit descriptifs.
Prérequis
Java 17 ou 21 JDK installé.
JavaFX 17+.
Maven ou Gradle pour construire le client et le serveur.
Modelio – la version spécifiée dans les instructions du projet pour éditer et exporter le modèle UML.

Pour commencer
Clonez le référentiel :
git clone cd
Créez votre branche de travail : git checkout -b model (ou metier, ui, api, etc.).
Modèle : ouvrez le fichier modèle dans /modelio avec Modelio, ajustez les classes si nécessaire et exportez le diagramme. Validez le modèle mis à jour dans le même répertoire.
Serveur : dans /server, initialisez un projet Javalin (Maven ou Gradle) et créez les points de terminaison REST définis dans le modèle UML. Implémentez le stockage des données, la génération des numéros de commande et la logique de validation.
Client : dans /client, initialisez un projet JavaFX. Développez les écrans (accueil, sélection de catégorie, liste des plats, détails des plats, panier/récapitulatif, confirmation) et interagissez avec le serveur via l'API REST.
Testez localement : exécutez le serveur et le client localement et vérifiez que le flux complet fonctionne (cela fait partie de l'exigence non fonctionnelle « exécution locale autonome » newtab ).
Licence
Il s'agit d'un projet académique ; une licence peut être spécifiée par votre instructeur. Jusqu'à ce moment-là, considérez-le comme interne/sans licence.
