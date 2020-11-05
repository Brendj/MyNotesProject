--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.71
ALTER TABLE cf_orgs ADD COLUMN DisableEditingClientsFromAISReestr  integer NOT NULL DEFAULT 0;
update cf_orgs set DisableEditingClientsFromAISReestr =0;

ALTER TABLE cf_orgs ADD COLUMN UsePaydableSubscriptionFeeding  integer NOT NULL DEFAULT 0;
update cf_orgs set UsePaydableSubscriptionFeeding =0;


ALTER TABLE cf_orgs ADD COLUMN MainBuilding integer ;

--! Добавляем параметр главное здание для организаций не имеюших friendly org.
update cf_orgs
set MainBuilding = 1 where idOfOrg  in (SELECT friendlyorg from cf_friendly_organization group by friendlyorg having count(*) < 2 );

update cf_orgs
set MainBuilding = 1 where idOfOrg not in (select friendlyorg from cf_friendly_organization  );

--! ФИНАЛИЗИРОВАН (Сунгатов, 140819) НЕ МЕНЯТЬ
