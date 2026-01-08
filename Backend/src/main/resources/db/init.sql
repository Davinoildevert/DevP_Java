PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS category (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  icon TEXT
);

CREATE TABLE IF NOT EXISTS plat (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  description TEXT,
  price REAL NOT NULL,
  available INTEGER NOT NULL DEFAULT 1,
  image TEXT,
  category_id INTEGER NOT NULL,
  FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE IF NOT EXISTS order_header (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  client_name TEXT,
  total REAL NOT NULL,
  created_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS order_line (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  order_id INTEGER NOT NULL,
  plat_id INTEGER NOT NULL,
  quantity INTEGER NOT NULL,
  unit_price REAL NOT NULL,
  choices_json TEXT,
  FOREIGN KEY (order_id) REFERENCES order_header(id),
  FOREIGN KEY (plat_id) REFERENCES plat(id)
);
