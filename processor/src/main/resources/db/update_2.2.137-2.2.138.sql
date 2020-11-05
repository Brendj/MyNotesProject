--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.138

--Индекс по ид. орг в таблице карт
CREATE INDEX cf_cards_idoforg_idx ON cf_cards USING btree (idoforg);

CREATE INDEX cf_friendly_organization_currentorg_idx ON cf_friendly_organization USING btree (currentorg) ;

alter table cf_clients add column createdFrom integer not null default 0,
  add column createdFromDesc character varying(128);

--! ФИНАЛИЗИРОВАН (Семенов, 130717) НЕ МЕНЯТЬ