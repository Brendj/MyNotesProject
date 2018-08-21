--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.161

--индекс по дате предзаказа
CREATE INDEX cf_preorder_menudetail_preorderdate_idx ON cf_preorder_menudetail USING btree (preorderdate);

CREATE TABLE cf_linking_tokens_for_smartwatch
(
  idoflinkingtokensforsmartwatch BIGSERIAL NOT NULL,
  phonenumber CHARACTER VARYING(32),
  token VARCHAR(20) UNIQUE NOT NULL,
  CONSTRAINT cf_linking_tokens_for_smartwatch_pk PRIMARY KEY(idoflinkingtokensforsmartwatch)
);

CREATE INDEX cf_linking_tokens_for_sw_token_idx ON cf_linking_tokens_for_smartwatch(token);
CREATE INDEX cf_linking_tokens_for_sw_phone_idx ON cf_linking_tokens_for_smartwatch(phonenumber);

CREATE TABLE cf_smartwatchs
(
  idOfSmartWatch BIGSERIAL NOT NULL,
  idOfCard BIGINT NOT NULL,
  idOfClient BIGINT NOT NULL,
  trackerUid BIGINT NOT NULL,
  trackerId BIGINT NOT NULL,
  trackerActivateTime BIGINT,
  trackerActivateUserId BIGINT,
  status CHARACTER VARYING(64),
  simiccid CHARACTER VARYING(128),
  model CHARACTER VARYING(128),
  color CHARACTER VARYING(128),
  CONSTRAINT cf_smartwatchs_pk PRIMARY KEY (idOfSmartWatch)
) WITH (
OIDS = FALSE
);

CREATE INDEX cf_smartwatchs_client_card_idx ON cf_smartwatchs(idOfCard, idOfClient);
CREATE INDEX cf_smartwatchs_card_idx ON cf_smartwatchs(idOfCard);
CREATE INDEX cf_smartwatchs_tracker_ID_UID_idx ON cf_smartwatchs(trackerId, trackerUid);

-- Флаг, что клиент имеет активные часы, нет параметра по умолчанию т.к. большая таблица
ALTER TABLE cf_clients
ADD COLUMN hasActiveSmartWatch INTEGER;

-- Флаги для контроля отправки уведомлений в geoplaner, нет параметра по умолчанию т.к. большие таблицы
ALTER TABLE CF_EnterEvents_Send_Info
ADD COLUMN sendToGeoplaner INTEGER;

ALTER TABLE CF_Transactions
ADD COLUMN sendToGeoplaner INTEGER;
