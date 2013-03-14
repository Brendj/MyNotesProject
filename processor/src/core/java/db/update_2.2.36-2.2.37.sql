--! Пока скрипт не винализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.36

--! Добавление индекса, если требуется, раскомментировать
--! CREATE index "cf_menudetails_path_price_idx" ON CF_menudetails (menupath, price);

-- Добавление колонки в AccountTransaction, указывающую на предыдущее ОУ клиента
ALTER TABLE CF_Transactions ADD COLUMN IdOfOrg bigint;

-- Добавлена возможность подтверждения заказа ученика который уходит в минус
ALTER TABLE cf_orders ADD COLUMN confirmerid bigint;

--! удалены не нужные таблицы
DROP TABLE CF_Group_Payment_Confirm;
DROP TABLE CF_Group_Payment_Confirm_Position;

--! скрипт не работает на казанской базе
--! ALTER TABLE cf_clients ADD CONSTRAINT cf_clients_fk_orgclientsgroup FOREIGN KEY (idoforg, idofclientgroup)
--! REFERENCES cf_clientgroups (idoforg, idofclientgroup);


--! ФИНАЛИЗИРОВАН (Кадыров, 130222) НЕ МЕНЯТЬ