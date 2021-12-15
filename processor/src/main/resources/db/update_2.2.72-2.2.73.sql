--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.73

-- Добавления полей "Детализация статуса", "БТИ УНОМ", "БТИ УНАД", "Очередь внедрения", "Доп. ид. здания"
ALTER TABLE cf_orgs ADD COLUMN btiUnom INTEGER;
ALTER TABLE cf_orgs ADD COLUMN btiUnad INTEGER;
ALTER TABLE cf_orgs ADD COLUMN introductionQueue VARCHAR(64) DEFAULT '';
ALTER TABLE cf_orgs ADD COLUMN additionalIdBuilding INTEGER;
ALTER TABLE cf_orgs ADD COLUMN statusDetailing VARCHAR(256) DEFAULT '';

--! ФИНАЛИЗИРОВАН (Сунгатов, 140925) НЕ МЕНЯТЬ
