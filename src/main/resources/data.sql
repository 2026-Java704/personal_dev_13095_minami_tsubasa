-- カテゴリーテーブルデータ
INSERT INTO genres(name) VALUES('収入');
INSERT INTO genres(name) VALUES('固定費');
INSERT INTO genres(name) VALUES('変動費');
INSERT INTO genres(name) VALUES('その他');

-- ユーザテーブルデータ
INSERT INTO users(name,email,password) VALUES("田中太郎", "tanaka@aaa.com","himitu");
INSERT INTO users(name,email,password) VALUES("鈴木一郎", "suzuki@aaa.com","himitu");

-- 項目テーブルデータ
INSERT INTO items(name, user_id, genre_id, price,add_on) VALUES("食事代", 1, 3, 1200, "2026/05/01");
INSERT INTO items(name, user_id, genre_id, price,add_on) VALUES("収入", 2, 1, 200000, "2026/05/15");