--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.187

--поля для номера и типа карты в таблице событий от внешних систем
alter table cf_externalevents
  add column cardno bigint,
  add column cardtype integer;

CREATE TABLE cf_balancehold_transactions
(
  idoftransaction bigserial NOT NULL,
  idofclientbalancehold bigint NOT NULL,
  transactionsum bigint NOT NULL,
  transactiondate bigint NOT NULL,
  balancebefore bigint,
  CONSTRAINT cf_balancehold_transactions_pk PRIMARY KEY (idoftransaction),
  CONSTRAINT cf_balancehold_transactions_idofclientbalancehold_fk FOREIGN KEY (idofclientbalancehold)
  REFERENCES cf_clientbalance_hold (idofclientbalancehold) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

--новое поле id OO, которая внесла изменения в заявление на блокировку средств
alter table cf_clientbalance_hold add column idOfOrgLastChange bigint,
add column lastChangeStatus integer not null default 0;

alter table cf_clientbalance_hold add CONSTRAINT cf_clientbalance_hold_orglastchange_fk
FOREIGN KEY (idOfOrgLastChange) REFERENCES cf_orgs(idoforg);

--97
ALTER TABLE cf_card_signs ADD COLUMN publickeyprovider bytea;
ALTER TABLE cf_card_signs ADD COLUMN privatekeycard bytea;
ALTER TABLE cf_card_signs ADD COLUMN signtypeprov int4;
ALTER TABLE cf_card_signs ADD COLUMN newtypeprovider bool;

--! ФИНАЛИЗИРОВАН 11.07.2019, НЕ МЕНЯТЬ