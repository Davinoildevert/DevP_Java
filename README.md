# Backend — Borne de commande restaurant

Backend REST développé en **Java avec Javalin** pour une borne de commande de restaurant, consommé par un client JavaFX.

## En bref — contribution & valeur

- **Développé** une API REST pour centraliser menu, catégories, commandes et paiements simulés.
- **Connecté** le backend à une base SQLite embarquée afin de conserver les données sans dépendre d’un service externe.
- **Ajouté** validation des données et codes HTTP adaptés pour fiabiliser les échanges avec le client.
- **Documenté** les endpoints avec Swagger / OpenAPI afin de faciliter leur test et leur intégration.

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
