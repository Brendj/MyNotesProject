--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.50

-- Добавление протоколирования контрагента в БД для возможности последующего фильтра
alter table CF_ReportInfo add column IdOfContragent bigint default null;
alter table CF_ReportInfo add column Contragent varchar(128) default null;

-- Добавлено поле ИНН в накладной
ALTER TABLE CF_WayBills ADD COLUMN inn character varying(32);

-- Добавлен хэшкод для сранения меню при синхронизации
ALTER TABLE CF_Menu ADD COLUMN DetailsHashCode int;

ALTER TABLE cf_complexinfo ADD COLUMN usedsubscriptionfeeding integer;


-- Добавлен флаг включения товарной конфигурации выбранной организации
ALTER TABLE cf_orgs ADD COLUMN CommodityAccounting integer NOT NULL DEFAULT 0;
--
--! Устанавливаем всем ораганизациям источникм меню тип "Поставщик"
update cf_orgs set RefectoryType=3 where idoforg in (SELECT distinct idofsourceorg FROM cf_menuexchangerules);
--
-- Добавлены "Клиентские циклограммы" в обработку распределенных объектов
CREATE TABLE cf_clients_cycle_diagrams
(
  IdOfCycleDiagram BigSerial NOT NULL,
  IdOfClient bigint NOT NULL,
  OrgOwner bigint,
  GUID character varying(36) NOT NULL,
  DeletedState boolean NOT NULL DEFAULT false,
  GlobalVersion bigint,
  GlobalVersionOnCreate BIGINT DEFAULT NULL,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  SendAll integer NOT NULL DEFAULT 0,

  DateActivationDiagram bigint NOT NULL,
  StateDiagram integer NOT NULL DEFAULT 0,
  Monday character varying(255),
  MondayPrice bigint NOT NULL,
  Tuesday character varying(255),
  TuesdayPrice bigint NOT NULL,
  Wednesday character varying(255),
  WednesdayPrice bigint NOT NULL,
  Thursday character varying(255),
  ThursdayPrice bigint NOT NULL,
  Friday character varying(255),
  FridayPrice bigint NOT NULL,
  Saturday character varying(255),
  SaturdayPrice bigint NOT NULL,
  Sunday character varying(255),
  SundayPrice bigint NOT NULL,
  CONSTRAINT cf_clients_cycle_diagrams_pk PRIMARY KEY (IdOfCycleDiagram),
  CONSTRAINT cf_clients_cycle_diagrams_clients_fk FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);
-- Добавлены "Подписки на услугу абонементного питания" в обработку распределенных объектов
CREATE TABLE cf_subscriber_feeding
(
  IdOfServiceSubscriberFeeding BigSerial NOT NULL,
  IdOfClient bigint NOT NULL,
  OrgOwner bigint,
  GUID character varying(36) NOT NULL,
  DeletedState boolean NOT NULL DEFAULT false,
  GlobalVersion bigint,
  GlobalVersionOnCreate BIGINT DEFAULT NULL,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  SendAll integer NOT NULL DEFAULT 0,

  DateActivateService bigint NOT NULL,
  LastDatePauseService bigint,
  DateDeactivateService bigint,
  ServiceState integer NOT NULL DEFAULT 0,
  wassuspended boolean NOT NULL DEFAULT false,
  CONSTRAINT cf_service_subscriber_feeding_pk PRIMARY KEY (IdOfServiceSubscriberFeeding),
  CONSTRAINT cf_service_subscriber_feeding_clients_fk FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);

-- товары добавлены в конфигурацию провайдера
ALTER TABLE cf_goods ADD COLUMN idofconfigurationprovider bigint;
ALTER TABLE cf_goods ADD CONSTRAINT cf_goods_configurationprovider_fk
FOREIGN KEY (idofconfigurationprovider) REFERENCES cf_provider_configurations (idofconfigurationprovider);

-- группы товаров добавлены в конфигурацию провайдера
ALTER TABLE cf_goods_groups ADD COLUMN idofconfigurationprovider bigint;
ALTER TABLE cf_goods_groups ADD CONSTRAINT cf_goods_groups_configurationprovider_fk
FOREIGN KEY (idofconfigurationprovider) REFERENCES cf_provider_configurations (idofconfigurationprovider);

-- товаро-материальные ценности добавлены в конфигурацию провайдера
ALTER TABLE cf_trade_material_goods ADD COLUMN idofconfigurationprovider bigint;
ALTER TABLE cf_trade_material_goods ADD CONSTRAINT cf_trade_material_goods_configurationprovider_fk
FOREIGN KEY (idofconfigurationprovider) REFERENCES cf_provider_configurations (idofconfigurationprovider);

-- Элементы базовой корзины с ценой добавлены в конфигурацию провайдера
ALTER TABLE cf_good_basic_basket_price ADD COLUMN idofconfigurationprovider bigint;
ALTER TABLE cf_good_basic_basket_price ADD CONSTRAINT cf_good_basic_basket_price_configurationprovider_fk
FOREIGN KEY (idofconfigurationprovider) REFERENCES cf_provider_configurations (idofconfigurationprovider);

-- Добавлен компазитный индекс на часто используемые поля в таблице подтверждения
CREATE INDEX cf_do_confirm_all_fields_idx ON cf_do_confirms USING btree (distributedobjectclassname, guid, orgowner);

-- Субсчет1 клиента
ALTER TABLE cf_clients ADD COLUMN SubBalance1 bigint;

-- Добавление данных по операциям субсчета в таблицу Транзакций
ALTER TABLE CF_Transactions ADD COLUMN TransactionSubBalance1Sum bigint;
ALTER TABLE CF_Transactions ADD COLUMN SubBalance1BeforeTransaction bigint;
ALTER TABLE CF_Transactions ADD COLUMN SourceBalanceNumber bigint;

-- Регистрация переводов средств между субсчетами
CREATE TABLE CF_SubAccount_Transfers (
  IdOfSubAccountTransfer BigSerial NOT NULL,
  CreatedDate bigint NOT NULL,
  IdOfClientTransfer bigint NOT NULL,
  balanceBenefactor bigint NOT NULL,
  balanceBeneficiary bigint NOT NULL,
  Reason VARCHAR(256),
  IdOfTransactionOnBenefactor bigint NOT NULL,
  IdOfTransactionOnBeneficiary bigint NOT NULL,
  TransferSum bigint NOT NULL,
  CONSTRAINT cf_subaccount_transfer_pk PRIMARY KEY (IdOfSubAccountTransfer),
  CONSTRAINT cf_subaccount_transfer_c_ctr_fk FOREIGN KEY (IdOfClientTransfer) REFERENCES cf_clients (IdOfClient),
  CONSTRAINT cf_subaccount_transfer_t_bctr_fk FOREIGN KEY (IdOfTransactionOnBenefactor) REFERENCES cf_transactions (IdOfTransaction),
  CONSTRAINT cf_subaccount_transfer_t_bcry_fk FOREIGN KEY (IdOfTransactionOnBeneficiary) REFERENCES cf_transactions (IdOfTransaction)
);

-- Поправка ошибки ECAFE-1256 по существовании ключа
SELECT setval('public.cf_clientsnotificationsettings_idofsetting_seq', (SELECT max(idofsetting)+1 FROM cf_clientsnotificationsettings), true);


