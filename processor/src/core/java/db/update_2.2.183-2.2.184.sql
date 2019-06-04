--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.184

-- Таблица транзакций по заблокированным средствам
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