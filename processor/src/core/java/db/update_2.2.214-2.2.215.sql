--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 215

--329 Флаг, что пользователю отправлено оповещение об удалении регуляра
ALTER TABLE cf_regular_preorders ADD sendeddailynotification bool NULL;

--Таблица активности карт
CREATE TABLE cf_card_activity (
  idOfCardActivity bigserial NOT NULL primary key,
  idOfCard bigint NOT NULL,
  type integer NOT NULL,
  lastUpdate bigint NOT NULL,
  CONSTRAINT cf_card_activity_idofcard_fk FOREIGN KEY (idofcard)
  REFERENCES cf_cards (idofcard) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

insert into cf_card_activity(idofcard, type, lastupdate)
  select idofcard, 0, cast((round(date_part('epoch',now())) * 1000) as bigint)
  from cf_cards where state = 0 and idofclient is not null;

CREATE INDEX cf_card_activity_idofcard_idx ON cf_card_activity USING btree (idOfCard);

--Удаляем колонку informedspecialmenu, т.к. флаг перенесен в таблицу cf_preorder_flags
alter table cf_client_guardian drop column informedspecialmenu;

--! ФИНАЛИЗИРОВАН 08.05.2020, НЕ МЕНЯТЬ