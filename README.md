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

frontend
├── src
│ └── main
│ └── java
│ └── com.example
│ ├── views
│ └── App.java
├── styles.css
└── README.md


---

### README FRONTEND 

```md
#  Console de restaurant – Frontend (JavaFX)

##  Description
Ce dépôt contient le **client JavaFX** simulant une borne de commande tactile pour un restaurant asiatique.

L’application permet au client de :
- parcourir le menu par catégorie
- consulter les plats
- ajouter et modifier un panier
- confirmer une commande

Le frontend communique avec une **API REST Javalin**.

---

##  Fonctionnalités
- Écran d’accueil
- Sélection des catégories
- Liste et détail des plats
- Panier avec modification des quantités
- Confirmation de commande avec numéro

---

##  Technologies
- Java 17
- JavaFX
- CSS JavaFX
- API REST (HTTP / JSON)

---

##  Lancement

### Prérequis
- Java 17+
- Backend lancé sur `localhost:7070`

### Exécution
Lancer la classe `App.java` depuis l’IDE.

---

##  Conception
- Interface adaptée à une borne tactile
- Résolution cible : **1920×1080**
- Séparation vue / logique métier

---

##  Projet pédagogique
Groupe : **DevP_Java**

Commandes
mvn clean javafx:run




 



