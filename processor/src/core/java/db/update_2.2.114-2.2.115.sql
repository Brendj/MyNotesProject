--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.114

ALTER TABLE cf_clients ADD COLUMN gender integer,
ADD COLUMN birthDate bigint,
ADD COLUMN benefitOnAdmission character varying (3000);

--Флаг "Признак получения из синхронизации дружественной организации" у заказа
ALTER TABLE cf_orders ADD COLUMN isfromfriendlyorg boolean NOT NULL DEFAULT false;

--Дата присваивания последнего пароля
ALTER TABLE cf_users ADD COLUMN passworddate bigint NOT NULL default (extract(epoch from now()) * 1000),
ADD COLUMN attemptnumber integer default 0;

--Журнал регистрации событий запуска программ/процессов
CREATE TABLE cf_security_journal_processes
(
  idofjournalprocess bigserial not null,
  eventtype integer not null,
  eventdate bigint not null,
  idofuser bigint,
  issuccess boolean not null,
  idofprocess integer not null,

  CONSTRAINT cf_security_journal_processes_pk PRIMARY KEY (idofjournalprocess),
  CONSTRAINT cf_security_journal_processes_fk FOREIGN KEY (idofuser)
  REFERENCES cf_users (idofuser) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

--Версия для справочника организаций, передаваемого на клиент
ALTER TABLE cf_orgs ADD COLUMN orgStructureVersion BIGINT NOT NULL DEFAULT 0;