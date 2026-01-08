INSERT INTO category (id, name, icon) VALUES
(1, 'Entrées', null),
(2, 'Plats', null),
(3, 'Desserts', null),
(4, 'Boissons', null);

INSERT INTO plat (id, name, description, price, available, image, category_id) VALUES
(1, 'Nems poulet', 'Nems croustillants au poulet', 6.90, 1, 'nems.jpg', 1),
(2, 'Soupe miso', 'Soupe miso traditionnelle', 4.50, 1, 'soupe_miso.jpg', 1),
(3, 'Gyozas', 'Raviolis japonais grillés', 7.90, 1, 'gyozas.jpg', 1),

(4, 'Ramen miso', 'Ramen au bouillon miso', 12.90, 1, 'ramen.jpg', 2),
(5, 'Pad Thai', 'Nouilles sautées façon thaï', 13.50, 1, 'padthay.jpg', 2),
(6, 'Curry rouge', 'Curry rouge lait de coco', 14.50, 1, 'curry.jpg', 2),

(7, 'Mochi', 'Mochi japonais', 4.90, 1, 'mochi.jpg', 3),
(8, 'Dorayaki', 'Pancake japonais fourré', 4.80, 1, 'dorayaki.jpg', 3),

(9, 'Boba thé', 'Thé au lait et perles', 4.50, 1, 'boba.jpg', 4),
(10,'Thé vert', 'Thé vert chaud', 2.90, 1, 'the_vert.jpg', 4);
