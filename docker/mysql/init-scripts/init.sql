CREATE DATABASE IF NOT EXISTS webcanvas_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON webcanvas_db.* TO 'webcanvas_app'@'%';