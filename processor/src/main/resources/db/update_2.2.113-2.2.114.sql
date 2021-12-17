--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.113

--Увеличение размерности поля для условия выборки в отчетах по расписанию
ALTER TABLE cf_ruleconditions ALTER conditionconstant TYPE CHARACTER VARYING(50000);

--Новое поле для краткого наименование организации для поставщика в сверке организаций
ALTER TABLE cf_orgregistrychange_item ADD COLUMN shortnamesupplierfrom CHARACTER VARYING(128);

--Новые поля ручного реестра талонов
ALTER TABLE cf_taloon_approval RENAME COLUMN qty TO soldedqty;
ALTER TABLE cf_taloon_approval ADD COLUMN requestedqty INTEGER, ADD COLUMN shippedqty INTEGER,
  ADD COLUMN ispp_state INTEGER NOT NULL DEFAULT 0, ADD COLUMN pp_state INTEGER NOT NULL DEFAULT 0;

--Связь базовой корзины с детализацией меню
ALTER TABLE cf_good_basic_basket_price ADD COLUMN idofmenudetail BIGINT;

--внешний ключ на таблицу menudetails
ALTER TABLE cf_good_basic_basket_price ADD CONSTRAINT cf_good_basic_basket_price_menudetail_fk FOREIGN KEY (idofmenudetail)
REFERENCES cf_menudetails (idofmenudetail) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

--новый индекс на таблицу cf_ComplexInfo
--CREATE INDEX cf_complexinfo_menudate_idx ON cf_complexinfo USING btree (menudate);

CREATE TABLE cf_interactive_report_data (idofrecord BIGINT, idoforg BIGINT, value VARCHAR(255),
  CONSTRAINT cf_interactivereport_cf_orgs_pk PRIMARY KEY (idoforg, idofrecord));

--Таблица истории льгот
CREATE TABLE cf_discountchangehistory
(
  idofdiscountchange BIGSERIAL NOT NULL,
  idofclient BIGINT NOT NULL, registrationdate BIGINT NOT NULL, discountmode INTEGER NOT NULL,
  olddiscountmode INTEGER NOT NULL, categoriesdiscounts CHARACTER VARYING(60) NOT NULL DEFAULT '',
  oldcategoriesdiscounts CHARACTER VARYING(60) NOT NULL DEFAULT '',
  CONSTRAINT cf_discountchangehistory_pk PRIMARY KEY (idofdiscountchange),
  CONSTRAINT cf_discountchangehistory_idofclient_fk FOREIGN KEY (idofclient)
  REFERENCES cf_clients (idofclient) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
) WITH
(OIDS = FALSE
);

--Таблица с нулевыми транзакциями
CREATE TABLE cf_zerotransactions
(
  idoforg BIGINT NOT NULL,
  transactiondate BIGINT NOT NULL,
  idofcriteria INTEGER NOT NULL,
  targetlevel INTEGER NOT NULL,
  actuallevel INTEGER NOT NULL,
  criterialevel INTEGER NOT NULL,
  idofreason INTEGER,
  comment character varying(256),
  version BIGINT NOT NULL,
  CONSTRAINT cf_zero_transaction_pk PRIMARY KEY (idoforg, transactiondate, idofcriteria),
  CONSTRAINT cf_zero_transaction_idoforg_fk FOREIGN KEY (idoforg)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

--Увеличение длины полей
ALTER TABLE cf_log_infoservice
  ALTER idofsystem TYPE character varying(50),
  ALTER ssoid TYPE character varying(50);

--Таблица свежезагруженных новых непривязанных карт
CREATE TABLE cf_newcards
(
  idofnewcard BIGSERIAL NOT NULL,
  createddate BIGINT NOT NULL,
  cardno BIGINT NOT NULL,
  cardprintedno BIGINT NOT NULL,
  CONSTRAINT cf_newcards_pk PRIMARY KEY (idofnewcard)
);

--Таблица руководителей клиентских групп
CREATE TABLE cf_clientgroup_manager
(
  idofgroupmanager BIGSERIAL              NOT NULL,
  version          BIGINT                 NOT NULL,
  clientgroupname  CHARACTER VARYING(256) NOT NULL,
  idofclient       BIGINT                 NOT NULL,
  idoforg          BIGINT                 NOT NULL,
  managertype      INTEGER                NOT NULL,
  deleted          INTEGER                NOT NULL,
  CONSTRAINT cf_clientgroup_manager_pk PRIMARY KEY (idofgroupmanager),
  CONSTRAINT cf_clientgroup_manager_idofclient_fk FOREIGN KEY (idofclient) REFERENCES cf_clients (idofclient) ON DELETE NO ACTION ON UPDATE NO ACTION
) WITH (OIDS = FALSE
);

--индекс по ид. клиента для таблицы руководители группы
CREATE INDEX cf_clientgroup_manager_client_idx ON cf_clientgroup_manager USING BTREE (idofclient);
--индекс по ид. группы и ид. организации для таблицы руководители группы
CREATE INDEX cf_clientgroup_manager_org_idx ON cf_clientgroup_manager USING BTREE (idoforg);

ALTER TABLE CF_RegistryChange ADD COLUMN gender integer;
ALTER TABLE CF_RegistryChange ADD COLUMN birthDate bigint;
ALTER TABLE CF_RegistryChange ADD COLUMN benefitOnAdmission character varying (3000);

--Таблица календаря учебных дней
CREATE TABLE cf_specialdates
(
  idoforg BIGINT NOT NULL,
  date BIGINT NOT NULL,
  isweekend INTEGER NOT NULL,
  deleted INTEGER NOT NULL,
  version BIGINT NOT NULL,
  comment CHARACTER VARYING(256) NOT NULL DEFAULT '',
  idoforgowner BIGINT NOT NULL,
  CONSTRAINT cf_specialdates_pk PRIMARY KEY (idoforg, date),
  CONSTRAINT cf_specialdates_idoforg_fk FOREIGN KEY (idoforg)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_specialdates_idoforgowner_fk FOREIGN KEY (idoforgowner)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

--Создание пользователя с предопределенным именем security и ролью Админа ИБ
create or replace function inline_0() returns integer as '
declare v_exists integer;
 id bigint;
 max_id bigint;
 work_option bigint;
begin
 select into v_exists count(*) from cf_users where username = ''security'';
 if v_exists = 0 then
   select into max_id max(IdOfUser) from cf_users;
   insert into CF_Users(IdOfUser, Version, UserName, Password, LastChange, Phone, IdOfRole, Isblocked)
     values(max_id + 1, 0, ''security'', ''MTIz'', 0, '''', 4, false);
   update CF_Generators set IdOfUser = IdOfUser + 1;
 end if;

--Далее убираем у всех права на операции с пользователями и даем эти права админу ИБ
--delete from cf_permissions where IdOfFunction in (1,2,3);
select into id IdOfUser from cf_users where UserName = ''security'';
INSERT INTO CF_Permissions(IdOfUser, IdOfFunction) VALUES(id, 1);
INSERT INTO CF_Permissions(IdOfUser, IdOfFunction) VALUES(id, 2);
INSERT INTO CF_Permissions(IdOfUser, IdOfFunction) VALUES(id, 3);
  select into work_option IdOfFunction from CF_Functions where FunctionName = ''workOption'';
INSERT INTO CF_Permissions(IdOfUser, IdOfFunction) VALUES(id, work_option);

return null;
end;' language 'plpgsql';

select inline_0();
drop function inline_0();

--Удаление пользователя через установку флага, а не физически записью
ALTER TABLE cf_users ADD COLUMN deletedstate boolean NOT NULL DEFAULT false,
  ADD COLUMN deletedate bigint,
  ADD COLUMN lastsmscode character varying(10),
  ADD COLUMN smscodeenterdate bigint,
  ADD COLUMN smscodegeneratedate bigint,
  ADD COLUMN needchangepassword boolean NOT NULL DEFAULT false;

--Флаг "Уровень безопасности" у организации
ALTER TABLE cf_orgs ADD COLUMN securitylevel integer NOT NULL DEFAULT 0;
--Таблица журнала изменений баланса лицевого счета
CREATE TABLE cf_security_journal_balances
(
  idofjournalbalance bigserial not null,
  eventtype integer not null,
  eventdate bigint not null,
  eventsource integer,
  issuccess boolean not null,
  terminal character varying(256),
  protocol character varying(32),
  eventinterface character varying(128),
  idofclient bigint,
  idofclientpayment bigint,
  idoforg bigint,
  idoforder bigint,
  request character varying(1024),
  clientaddress character varying(128),
  serveraddress character varying(128),
  certificate character varying(16384),
  message character varying(128),
  CONSTRAINT cf_cf_security_journal_balances_pk PRIMARY KEY (idofjournalbalance),
  CONSTRAINT cf_security_journal_balances_idofclient_fk FOREIGN KEY (idofclient)
  REFERENCES cf_clients (idofclient) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_security_journal_balances_idofclientpayment_fk FOREIGN KEY (idofclientpayment)
  REFERENCES cf_clientpayments (idofclientpayment) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_security_journal_balances_order_fk FOREIGN KEY (idoforg, idoforder)
  REFERENCES cf_orders (idoforg, idoforder) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE INDEX cf_security_journal_balances_client_idx ON cf_security_journal_balances USING BTREE (idofclient);
CREATE INDEX cf_security_journal_balances_eventdate_idx ON cf_security_journal_balances USING BTREE (eventdate);

--Поле дата, до которой действует блокировка
--Заблокированным на момент выполнения скрипта пользователям выставляется дата = текущий момент + 50 лет
ALTER TABLE cf_users ADD COLUMN blockeduntildate bigint;
UPDATE cf_users SET blockeduntildate=((SELECT cast((extract(epoch FROM now())*1000) as bigint)) + 1577846300000) 
WHERE isblocked=true;

--Журнал входа-выхода пользоватлей в/из бэк-офиса
CREATE TABLE cf_security_journal_authenticate
(
  idofjournalauthenticate bigserial not null,
  eventtype integer not null,
  eventdate bigint not null,
  idofuser bigint,
  issuccess boolean not null,
  ipaddress character varying(15),
  idofarmtype integer,
  login character varying(64),
  denycause integer,    
  
  CONSTRAINT cf_security_journal_authenticate_pk PRIMARY KEY (idofjournalauthenticate),
  CONSTRAINT cf_security_journal_authenticate_fk FOREIGN KEY (idofuser)
  REFERENCES cf_users (idofuser) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  
);

CREATE INDEX cf_security_journal_authenticate_users_idx ON cf_security_journal_authenticate USING BTREE (idofuser);


--! ФИНАЛИЗИРОВАН (Семенов, 290416) НЕ МЕНЯТЬ