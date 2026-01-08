 Backend (Javalin)

 
 Présentation du projet
Ce dépôt contient le **backend** de l’application *Borne de commande pour restauration asiatique*.

Le backend est une **API REST développée en Java avec Javalin**, permettant :
- la récupération des catégories et des plats,
- la gestion des commandes,
- l’enregistrement des commandes côté serveur.

Il est consommé par un **client JavaFX** simulant une borne de commande tactile.

---

##  Architecture technique
- **Langage** : Java
- **Framework serveur** : Javalin
- **Format des échanges** : JSON
- **Build** : Maven
- **Stockage** : en mémoire (List / Map Java)
  
 ## Commandes
 mvn clean install
 mvn exec:java
  

  backend
├── src
│ ├── main
│ │ └── java
│ │ └── com.example
│ │ ├── model
│ │ ├── service
│ │ └── App.java
│ └── test
│ └── java
│ └── com.sysn0
│ └── AppTest.java
├── pom.xml
└── README.md



Frontend (JavaFX)

##  Présentation


Ce dépôt contient le **client JavaFX** simulant une borne de commande tactile pour un restaurant asiatique.

L’application permet au client :
- de parcourir les catégories,
- de consulter les plats,
- d’ajouter des articles au panier,
- de valider une commande.

Elle consomme une **API REST Javalin** en local.

---

##  Fonctionnalités principales
- Écran d’accueil
- Sélection par catégorie
- Liste des plats
- Détail d’un plat
- Panier avec modification des quantités
- Confirmation de commande
  
 ##  Architecture
- **Langage** : Java
- **UI** : JavaFX
- **Communication** : API REST (JSON)
- **Pattern** : séparation Vue / Métier

  ## Commandes
  
  mvn clean javafx:run

frontend
├── src
│ └── main
│ └── java
│ └── com.example
│ ├── views
│ └── App.java
├── styles.css
└── README.md
