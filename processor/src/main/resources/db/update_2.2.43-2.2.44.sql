--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.44
-- Добавлены колонки "Время последнего входа", "IP-адрес входа" и "Заблокирован".
ALTER TABLE cf_users ADD COLUMN LastEntryIP varchar(15);
ALTER TABLE cf_users ADD COLUMN LastEntryTime bigint;
ALTER TABLE cf_users ADD COLUMN IsBlocked boolean NOT NULL DEFAULT false;

--! ФИНАЛИЗИРОВАН (Кадыров, 130826) НЕ МЕНЯТЬ