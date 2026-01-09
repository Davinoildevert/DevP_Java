# Backend – Borne de commande restaurant (Javalin / Java)

Ce dépôt contient le **backend REST** du projet de borne de commande de restaurant.  
Il est développé en **Java avec Javalin** et expose une API consommée par un client **JavaFX**.

Le backend gère :
- le menu (catégories et plats),
- la création et la consultation des commandes,
- la simulation du paiement (CB / sans contact),
- le stockage des données via une base SQLite embarquée.

---

## Technologies utilisées

- **Java 17+**
- **Javalin** (serveur HTTP REST)
- **SQLite** (base de données embarquée)
- **Jackson** (JSON ↔ objets Java)
- **Swagger / OpenAPI** (documentation de l’API)
- **JDBC** (accès base de données)

---

## Architecture générale

Le projet suit une architecture simple et lisible :

- `App.java` : point d’entrée de l’application, configuration du serveur et des routes
- `model` : classes représentant les données métier (Plat, Categorie, OrderRequest…)
- `db` : gestion de la base SQLite (connexion, création du schéma, seed)
- `service` : logique applicative (ex : utilisateurs)
- **Pas de framework lourd** (Spring, Hibernate…), volontairement simple pour la pédagogie

La base de données est **initialisée automatiquement au démarrage**.

---

## Lancement du serveur

### Prérequis
- Java installé (Java 17 recommandé)

### Démarrage

```bash
java -jar backend.jar

## Lancement du serveur

Le backend peut être lancé de deux façons :

### Via un IDE (IntelliJ / VS Code)

- Ouvrir le projet backend
- Lancer la classe **App**

Le serveur démarre alors à l’adresse suivante :

http://localhost:7070


---

## Documentation API (Swagger / OpenAPI)

Une documentation automatique de l’API est fournie.

### Swagger UI
👉 http://localhost:7070/swagger

### Spécification OpenAPI (JSON)
👉 http://localhost:7070/openapi

Ces pages permettent :
- de visualiser l’ensemble des endpoints disponibles,
- de tester les requêtes directement depuis le navigateur,
- de vérifier les formats de requêtes et de réponses attendus.

---

## Endpoints principaux

### Catégories

- `GET /api/categories`  
  → retourne la liste des catégories

---

### Plats

- `GET /api/plats`  
  → retourne tous les plats

- `GET /api/plats?categorieId=1`  
  → retourne les plats d’une catégorie donnée

- `GET /api/plats/{id}`  
  → retourne le détail d’un plat

---

### Commandes

- `POST /api/commande`  
  → création d’une commande

- `GET /api/commande/{id}`  
  → consultation d’une commande par identifiant

---

### Paiement (simulation)

- `POST /api/paiement`  
  → simulation d’un paiement par carte bancaire ou sans contact (NFC)

---

## Gestion des données

- Les données sont stockées dans une **base SQLite locale**
- Les tables sont **créées automatiquement au démarrage**
- Un **seed** insère des catégories et des plats si la base est vide

⚠️ **Important**  
En mode API, le frontend affiche **exactement ce que contient la base de données**.  
La différence avec le mode *mock* du frontend vient du fait que :
- le mode mock utilise des données fixes,
- le mode API dépend du contenu réel de la base SQLite.

---

## Codes HTTP et validation

L’API respecte les bonnes pratiques REST :

- `200 / 201` : succès
- `400` : données invalides
- `404` : ressource inexistante
- `500` : erreur serveur

Les données sont validées côté backend :
- quantités strictement positives,
- commande existante avant paiement,
- méthode de paiement valide (`CARD` ou `NFC`).

---

## Modélisation UML (Modelio)

Une modélisation UML a été réalisée avec **Modelio** :

- diagramme de classes basé sur le backend réel,
- classes métier principales :
  - `Categorie`
  - `Plat`
  - `Commande`
  - `LigneCommande`
  - `Paiement`
- associations et multiplicités cohérentes avec la base de données et l’API.

Le projet Modelio est fourni dans le dépôt **au format ZIP**.

---

## Choix de conception

- Backend volontairement **simple et lisible**
- Pas de sur-abstraction :
  - pas de DAO complexes,
  - pas d’ORM (Hibernate, JPA…)
- Priorité à la clarté pour une **démo pédagogique**
- Projet **portable**, fonctionnant sur toute machine et tout IDE
- Aucun service externe requis (base embarquée)

---
