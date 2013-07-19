--! Пока скрипт не винализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.42
-- При базовой корзины заполнении может быть пустым товаров может и не быть
ALTER TABLE cf_good_basic_basket_price ALTER COLUMN idofgood DROP NOT NULL;
-- Добавлена колонка указывающее количество которое должно было списаться
ALTER TABLE cf_internal_disposing_document_positions ADD COLUMN totalcountmust bigint;

-- Таблица регистрации временных карт
CREATE TABLE cf_cards_temp (
  IdOfCartTemp bigserial,
  IdOfOrg bigint,                        --! идентификатор организациии
  IdOfClient bigInt,                     --! Идентификатор клиента
  IdOfVisitor bigint,                    --!  Идентификатор посетителя
  CustomerType int not null default 0,     --! Признак карты посетителя , bit, 1- карта посетителя, 0 — карта клиента, not null
  CardNo bigint NOT NULL,                --! номер карты
  CardPrintedNo character varying(24),   --! номер нанесенный на карту
  CardStation int not null default 0,    --! int16 или int8, not null, значения-  0 — свободна, 1 — выдана , 3 — заблокирована (? не уверен, что блокировка нужна)
  CreateDate bigint not null,             --! Дата и время регистрации карты
  ValidDate bigint,                      --! Дата завершения действия карты
  CONSTRAINT cf_cards_temp_pk PRIMARY KEY (IdOfCartTemp),
  CONSTRAINT cf_cards_temp_organization FOREIGN KEY (IdOfOrg) REFERENCES cf_orgs (IdOfOrg),
  CONSTRAINT CardNo_Unique UNIQUE (CardNo)
);
--
CREATE TABLE cf_card_temp_operations(
  IdOfCardTempOperation bigserial not null,      --! первичный ключ процесинга
  LocalIdOperation bigint NOT NULL,           --! первичный ключ школы
  IdOfOrg bigint not null,                    --! внешний ключ на IdOfOrg из соотв. таблицы — равен идентификатору организации, на которую зарегистрирована врем. карта или, в случае врем. карты посетителя — идентификатору организации, в которой была произведена эта операция.
  IdOfCartTemp bigint not null,               --! внешний ключ или на физ. идентификатор временной карты или на первичный ключ соотв. записи из TempCards
  IdOfClient bigint,                          --! Идентификатор клиента
  IdOfVisitor bigint,                         --! Идентификатор посетителя
  OperationType int not null,                 --! Тип операции- int16 или int8, not null, значения-  0 — регистрация, 1 — выдача ,2 – возврат, 3 — блокировка
  OperationDate bigint not null,              --! Дата и время операции
  CONSTRAINT cf_card_temp_operations_pk PRIMARY KEY (IdOfCardTempOperation),
  CONSTRAINT cf_card_temp_operations_organization FOREIGN KEY (IdOfOrg) REFERENCES cf_orgs (IdOfOrg),
  CONSTRAINT cf_card_temp_operation_org_local_id UNIQUE (IdOfOrg , LocalIdOperation )
);

--
CREATE TABLE cf_visitors(
  IdOfVisitor bigserial not null,                --! первичный ключ
  IdOfPerson BIGINT NOT NULL,                    --! внешний ключ на ФИО посетителя
  PassportNumber varchar(50),                    --! Серийный номер паспорта
  PassportDate BIGINT,                           --! Дата выдачи паспорта
  WarTicketNumber varchar(50),                   --! Серийный номер водительского удостоверения (ВУ)
  WarTicketDate BIGINT,                          --! Дата выдачи ВУ
  DriverLicenceNumber varchar(50),               --! Серийный номер военного билета (ВБ)
  DriverLicenceDate BIGINT,                      --! Дата выдачи ВБ
  CONSTRAINT cf_visitors_pk PRIMARY KEY (IdOfVisitor),
  CONSTRAINT cf_visitors_IdOfPerson_fk FOREIGN KEY (IdOfPerson) REFERENCES CF_Persons (IdOfPerson)
);

CREATE TABLE cf_synchistory_exceptions
(
  idofsynchistoryexception bigserial NOT NULL,
  idoforg bigint NOT NULL,
  idofsync bigint NOT NULL,
  message character varying(512) NOT NULL,
  CONSTRAINT cf_synchistory_exceptions_pk PRIMARY KEY (idofsynchistoryexception),
  CONSTRAINT cf_synchistory_exceptions_organization FOREIGN KEY (idoforg) REFERENCES cf_orgs (idoforg),
  CONSTRAINT cf_synchistory_exceptions_sync FOREIGN KEY (idofsync) REFERENCES cf_synchistory (idofsync)
);

--! Необходимо добавить возможность активации ручного запуска для правила
alter table CF_ReportHandleRules add AllowManualReportRun INTEGER NOT NULL default 0;


-- Добавление возможности закреплять несколько контрагентов за пользователем
--! Необходимо для отображения содержимого процессанга в контексте пользователя
alter table cf_users drop column idofcontragent;
create table CF_UserContragents (
  IdOfUser        BIGINT        NOT NULL,
  IdOfContragent  BIGINT        NOT NULL,
  CONSTRAINT CF_UserContragents_pk PRIMARY KEY (IdOfUser, IdOfContragent),
  CONSTRAINT CF_UserContragents_IdOfUser_fk FOREIGN KEY (IdOfUser) REFERENCES CF_Users (IdOfUser),
  CONSTRAINT CF_UserContragents_IdOfContragent_fk FOREIGN KEY (IdOfContragent) REFERENCES CF_Contragents (IdOfContragent)
);

create index CF_Orders_SumByCard_idx on CF_Orders(SumByCard);
create index CF_Orders_SumByCash_idx on CF_Orders(SumByCash);
create index CF_ClientSms_Price_idx on CF_ClientSms(Price);
create index CF_SubscriptionFee_SubscriptionSum_idx on CF_SubscriptionFee(SubscriptionSum);
create index CF_ClientPayments_PaySum_idx on CF_ClientPayments(PaySum);

alter table CF_Orgs add FullSyncParam INTEGER NOT NULL default 0;


-- Добавление ссылки на контрагента для Контракта
alter table cf_contracts add column IdOfContragent BIGINT DEFAULT NULL;

-- Добавлен номер правила социальной скидки в деталь заказа
alter table CF_OrderDetails add column IdOfRule  BIGINT DEFAULT NULL;


-- Таблица привязки GuardSAN и клиентов.
--! Ранее поле GuardSAN располагалось в CF_Clients, чем сильно загружало процессинг
create table CF_GuardSan  (
  IdOfGuardSan      bigserial     NOT NULL,
  IdOfClient        BIGINT        NOT NULL,
  GuardSan          VARCHAR(11)   NOT NULL,
  CONSTRAINT CF_GuardSan_pk PRIMARY KEY (IdOfGuardSan),
  CONSTRAINT CF_Client_GuardSan_IdOfClient_fk FOREIGN KEY (IdOfClient) REFERENCES CF_Clients (IdOfClient)
);
create index CF_GuardSan_GuardSan_idx on CF_GuardSan(GuardSAN);