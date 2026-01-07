# DevP_Java
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


# Borne de commande – Backend (API REST Javalin)

## Présentation du projet
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
- **Stockage** : en mémoire

 ### Commandes
```bash
mvn clean install
mvn exec:java



 



