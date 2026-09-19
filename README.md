# Backend — Borne de commande restaurant

Backend REST développé en **Java avec Javalin** pour une borne de commande de restaurant, consommé par un client JavaFX.

## Ma contribution

J’étais **chef de projet** : j’ai supervisé l’ensemble du projet tout en participant directement au développement.

- **Supervisé** l’avancement global et la cohérence technique du projet.
- **Codé** sur le backend et participé aux corrections nécessaires à l’intégration.
- **Réalisé** la liaison entre le frontend JavaFX et le backend REST.
- **Mis en place** Swagger / OpenAPI pour documenter et tester les endpoints.
- **Participé** à la validation des échanges front↔back et à l’intégration de la persistance SQLite.

## Stack technique

- **Java 17+**
- **Javalin**
- **SQLite**
- **JDBC**
- **Jackson**
- **Swagger / OpenAPI**
- **JavaFX** côté client

## Architecture

- `App.java` — démarrage du serveur et configuration des routes
- `model/` — objets métier
- `db/` — connexion SQLite, schéma et seed
- `service/` — logique applicative

Le projet reste volontairement léger, sans ORM ni framework lourd.

## Fonctionnalités

### Menu
- liste des catégories ;
- liste et détail des plats ;
- filtrage par catégorie.

### Commandes
- création d’une commande ;
- consultation d’une commande ;
- validation des quantités.

### Paiement
- simulation carte bancaire ;
- simulation sans contact / NFC ;
- vérification de l’existence de la commande.

## Endpoints principaux

```text
GET  /api/categories
GET  /api/plats
GET  /api/plats?categorieId=1
GET  /api/plats/{id}
POST /api/commande
GET  /api/commande/{id}
POST /api/paiement
```

## Documentation API

Swagger UI :

```text
http://localhost:7070/swagger
```

Spécification OpenAPI :

```text
http://localhost:7070/openapi
```

## Lancement

### Via IDE
Lancer la classe `App`.

### Via JAR

```bash
java -jar backend.jar
```

Serveur :

```text
http://localhost:7070
```

## Persistance

- SQLite locale ;
- création automatique des tables ;
- seed automatique si la base est vide.

## Compétences démontrées

**Java • REST API • Javalin • SQLite • JDBC • validation backend • OpenAPI • modélisation de données**

## Modélisation

Une modélisation UML a également été réalisée avec Modelio pour représenter les principales classes métier et leurs associations.
