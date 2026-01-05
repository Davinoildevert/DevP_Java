# DevP_Java

Restaurant Console Project
Description

This application is a small restaurant ordering system designed for an interactive kiosk or console. The application allows customers to browse the menu by category, customise dishes, add items to a cart, modify the cart, and confirm an order. A JavaFX front‑end communicates with a Javalin REST back‑end, and the modelling of the business domain is done with Modelio.

Features

Browse categories: display the available categories (Entrées, Plats, Desserts, Boissons) and filter the menu by category
newtab
.

Dish details: show mandatory information for each dish – name, price, description, pictogram(s), category and availability
newtab
.

Add to cart and choose quantity: users can add a dish to their cart and select the quantity
newtab
.

Personalisation: certain dishes can be personalised (e.g., spice level, accompaniment, drink)
newtab
.

View and modify cart: the cart can be consulted and modified at any time during navigation
newtab
.

Order confirmation: a final recap shows the contents of the order and the user can confirm it, generating an order number
newtab
.

Optional client association: the order can optionally be associated with a customer name or identifier.

Architecture

The project follows a client/server architecture:

Client – a JavaFX application providing the user interface, handling navigation and local cart management, and communicating with the server via HTTP REST API.

Server – a Javalin‑based REST API that exposes endpoints for retrieving categories and dishes, receiving cart submissions, validating orders, and storing data
newtab
.

Model – the UML domain model is designed with Modelio and exported into the /modelio directory. It defines classes such as Categorie, Plat, Panier, LignePanier, Commande, LigneCommande and Personnalisation.

Use‑case overview
User role	Use cases
Visitor	Browse the menu, filter by category, view dish details, add to cart, personalise dish options, modify quantities, view the recap and confirm the order
Staff (optional)	Manage the list of dishes and categories, update availability, view confirmed orders
UML Modélisation

An initial class diagram should be built in Modelio containing at least the following classes:

Categorie – label and icon (e.g. entrée, plat principal, dessert, boisson).

Plat – name, description, price, category, pictogram(s), availability.

Personnalisation – information about customisation (spice level, accompaniment, drink, etc.) associated with a dish.

Panier – a collection of LignePanier that can be modified by the user.

LignePanier – association between a dish and its current quantity in the cart, with an optional personalisation.

Commande – order number, date, total and list of LigneCommande. Created when the cart is validated.

LigneCommande – dish, quantity and unit price fixed at the moment of order.

This diagram should be exported (image or XMI) into the /modelio directory. A simple use‑case diagram should illustrate the main actions (browse menu, add to cart, personalise, consult cart, confirm order).

Repository structure
/client/            # JavaFX client application
/server/            # Javalin REST server application
/modelio/           # UML model and exported diagrams
README.md           # This file


Each directory has its own build system. The repository must respect the structure and naming conventions stipulated in the delivery requirements
newtab
.

Branches and contributions

The repository uses Git for version control
newtab
. The recommended workflow is:

Create one branch per team member (e.g. model, metier, ui, api) to allow parallel work without conflicts.

Do not work directly on main. Merge changes via pull requests once they have been reviewed.

Commit early and often; use descriptive commit messages.

Prerequisites

Java 17 or 21 JDK installed.

JavaFX 17+.

Maven or Gradle for building the client and server.

Modelio – the version specified in the project instructions for editing and exporting the UML model.

Getting started

Clone the repository:

git clone <repository-url>
cd <repository-folder>


Create your working branch: git checkout -b model (or metier, ui, api, etc.).

Model: open the model file in /modelio with Modelio, adjust the classes if necessary and export the diagram. Commit the updated model in the same directory.

Server: in /server, initialise a Javalin project (Maven or Gradle) and create the REST endpoints defined in the UML model. Implement data storage, order number generation and validation logic.

Client: in /client, initialise a JavaFX project. Develop the screens (home, category selection, dish list, dish details, cart/recap, confirmation) and interact with the server via the REST API.

Test locally: run both the server and the client locally and verify that the full flow works (this is part of the non‑functional requirement “exécution locale autonome”
newtab
).

Licence

This is an academic project; a licence may be specified by your instructor. Until then, treat it as internal/unlicensed.
