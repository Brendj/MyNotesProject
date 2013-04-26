--! Пока скрипт не винализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.40

-- Расширение поля source в связи с подключением платежей из РНИП (размер идентификатора = 32 символам)
ALTER TABLE CF_Orders ADD COLUMN OrderDate BIGINT NOT NULL DEFAULT 0;
ALTER TABLE CF_Orders ADD COLUMN Comments BIGINT DEFAULT '';
update CF_Orders set orderdate = createddate;

--! модифицировать колонку в пользу избавления кавычек
--! ALTER TABLE cf_clients RENAME "Limit"  TO limits;