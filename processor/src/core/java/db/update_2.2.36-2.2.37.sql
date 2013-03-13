--! Добавление индекса, если требуется, раскомментировать
--! CREATE index "cf_menudetails_path_price_idx" ON CF_menudetails (menupath, price);

-- Добавление колонки в AccountTransaction, указывающую на предыдущее ОУ клиента
ALTER TABLE CF_Transactions ADD COLUMN IdOfOrg bigint;