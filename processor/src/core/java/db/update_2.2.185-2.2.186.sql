--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.186

--таблица истории взаимодействия с РНиПом
create table cf_rnip_messages (
  idofrnipmessage bigserial NOT NULL,
  version bigint,
  eventtime bigint NOT NULL,
  eventtype integer NOT NULL,
  request text,
  response text,
  messageid character varying(100) NOT NULL,
  responsemessageid character varying(100),
  idofcontragent bigint NOT NULL,
  startdate bigint,
  enddate bigint,
  paging integer,
  processed integer NOT NULL DEFAULT 0,
  acksent integer NOT NULL DEFAULT 0,
  responsemessage character varying(256),
  succeeded integer NOT NULL DEFAULT 0,
  lastupdate bigint,
  constraint cf_rnip_messages_pk PRIMARY KEY (idofrnipmessage),
  CONSTRAINT cf_rnip_messages_contragent_fk FOREIGN KEY (idofcontragent) REFERENCES cf_contragents(idofcontragent)
);

CREATE INDEX cf_rnip_messages_eventtime_idx ON cf_rnip_messages (eventtime);
CREATE INDEX cf_rnip_messages_contragent_idx ON cf_rnip_messages (idofcontragent);
CREATE INDEX cf_rnip_messages_eventtime_processed_idx ON cf_rnip_messages (eventtime, processed);

--! ФИНАЛИЗИРОВАН 27.06.2019, НЕ МЕНЯТЬ