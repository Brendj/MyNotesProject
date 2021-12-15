--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.61

CREATE TABLE cf_history_card
(
  IdOfHistoryCard bigserial NOT NULL,
  IdOfCard bigint NOT NULL,
  UpDatetime bigint NOT NULL,
  FormerOwner bigint,
  NewOwner bigint NOT NULL,
  InformationAboutCard CHARACTER VARYING(1024) NOT NULL,
  CONSTRAINT cf_history_card_pk PRIMARY KEY (IdOfHistoryCard),
  CONSTRAINT cf_history_card_IdOfCard_fk FOREIGN KEY (IdOfCard)
  REFERENCES cf_cards (IdOfCard),
  CONSTRAINT cf_history_card_FormerOwner_fk FOREIGN KEY (FormerOwner)
  REFERENCES cf_clients (IdOfClient),
  CONSTRAINT cf_history_card_NewOwner_fk FOREIGN KEY (NewOwner)
  REFERENCES cf_clients (IdOfClient)
);

create index cf_history_card_formerowner_idx on cf_history_card (formerowner);
create index cf_history_card_newowner_idx on cf_history_card (newowner);
create index cf_history_card_idofcard_idx on cf_history_card (idofcard);

--! ФИНАЛИЗИРОВАН (Кадыров, 140408) НЕ МЕНЯТЬ