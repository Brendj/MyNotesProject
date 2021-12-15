--! Добавление индекса, если требуется, раскомментировать
--! CREATE index "cf_menudetails_path_price_idx" ON CF_menudetails (menupath, price);

-- Добавление колонки в AccountTransaction, указывающую на предыдущее ОУ клиента
ALTER TABLE CF_Transactions ADD COLUMN IdOfOrg bigint;

-- Добавлена возможность подтверждения заказа ученика который уходит в минус
ALTER TABLE cf_orders ADD COLUMN confirmerid bigint;

--! удалены не нужные таблицы
DROP TABLE CF_Group_Payment_Confirm;
DROP TABLE CF_Group_Payment_Confirm_Position;


--! ФИНАЛИЗИРОВАН (Кадыров, 130318) НЕ МЕНЯТЬ