--! Пока скрипт не винализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.39

-- Расширение поля source в связи с подключением платежей из РНИП (размер идентификатора = 32 символам)
ALTER TABLE cf_transactions ALTER COLUMN source TYPE VARCHAR(50);

-- Расширение таблицы contragents в связи с подключением платежей из РНИП
--! РНИП требует наличия данных полей в запросах на создание каталогов
ALTER TABLE cf_contragents ADD COLUMN kpp VARCHAR(10) NOT NULL DEFAULT '';
ALTER TABLE cf_contragents ADD COLUMN ogrn VARCHAR(15) NOT NULL DEFAULT '';

-- ECAFE-822 Добавить управление каталогами РНИП
-- ECAFE-826 Добавить новые отчеты для BI

--! ФИНАЛИЗИРОВАН (Кадыров, 130403) НЕ МЕНЯТЬ