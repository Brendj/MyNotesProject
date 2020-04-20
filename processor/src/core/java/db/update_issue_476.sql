--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 476

CREATE TABLE cf_card_activity (
  idOfCardActivity bigserial NOT NULL primary key,
  idOfCard bigint NOT NULL,
  lastEnterEvent bigint,
  lastOrder bigint,
  lastUpdate bigint NOT NULL,
  CONSTRAINT cf_card_activity_idofcard_fk FOREIGN KEY (idofcard)
  REFERENCES cf_cards (idofcard) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

insert into cf_card_activity(idofcard, lastenterevent, lastorder, lastupdate)
  select idofcard, cast((round(date_part('epoch',now())) * 1000) as bigint),
    cast((round(date_part('epoch',now())) * 1000) as bigint),
    cast((round(date_part('epoch',now())) * 1000) as bigint)
  from cf_cards where state = 0 and idofclient is not null;

CREATE INDEX cf_card_activity_lastupdate_idx ON cf_card_activity USING btree (lastupdate);
