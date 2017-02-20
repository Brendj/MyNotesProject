--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.130

--Порог снижения баланса, по достижении которого клиенту должно уйти уведомление о необходимости пополнения
alter table cf_clients add column balancetonotify bigint;

-- Таблица льгот ДСЗН
CREATE TABLE cf_categorydiscounts_dszn
(
  idofcategorydiscountdszn SERIAL NOT NULL,
  code INTEGER NOT NULL,
  description CHARACTER VARYING(512) NOT NULL DEFAULT '',
  idofcategorydiscount BIGINT,
  blockedchange INTEGER NOT NULL DEFAULT 0,
  version BIGINT NOT NULL,
  CONSTRAINT cf_categorydiscounts_dszn_pk PRIMARY KEY (idofcategorydiscountdszn),
  CONSTRAINT cf_categorydiscounts_dszn_idofcategorydiscount_fk FOREIGN KEY (idofcategorydiscount)
  REFERENCES cf_categorydiscounts (idofcategorydiscount) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_categorydiscounts_dszn_code UNIQUE (code)
);

--Блокировка изменения льгот ИСПП у клиентов в АРМ
ALTER TABLE cf_categorydiscounts ADD COLUMN blockedchange integer NOT NULL DEFAULT 0;

