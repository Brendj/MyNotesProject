--! Пока скрипт не винализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.40

-- Дата заказа,  чтобы оплата планов могла проходить за прошлые даты
ALTER TABLE CF_Orders ADD COLUMN OrderDate BIGINT DEFAULT null;
-- Добавлено свойство соментраии к заказу
ALTER TABLE CF_Orders ADD COLUMN Comments VARCHAR(90) DEFAULT null;
-- Добавлено свойство типа заказа
ALTER TABLE CF_Orders ADD COLUMN OrderType INT NOT NULL DEFAULT 1;

ALTER TABLE cf_discountrules ADD complexesmap varchar(512);

--! модифицировать колонку в пользу избавления кавычек
 ALTER TABLE cf_clients RENAME "Limit"  TO limits;

--! ФИНАЛИЗИРОВАН (Кадыров, 130522) НЕ МЕНЯТЬ