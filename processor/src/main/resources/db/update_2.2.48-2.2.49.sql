--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.49

-- Добавление возможности описывать причину ошибки при сверке с ИС Реестры
alter table cf_registrychange_errors add column errordetails varchar(256) default '';

-- Добавление возможности определения срока хранения отчетов для выбранного правила
alter table CF_ReportHandleRules add column StoragePeriod bigint default -1;

--! ECAFE-1188 - Реализовать функцию авто-пополнения счета по банковской карте через Банк Москвы - Acquiropay
-- Информация о подписках клиентов на автопополнение баланса с банк. карты.
-- уникальный идентификатор подписки на услугу в ИС ПП */
-- сумма пополнения */
-- пороговое значение баланса, при достижении ко/го баланс автопополняется */
-- срок действия подписки (число месяцев c даты подключения) */
-- дата, до ко\ой подписка активна (вычисляется относит-но даты подключения) */
-- дата подключения подписки */
-- дата отключения подписки */
-- флаг активности подписки */
-- статус подписки */
-- id подписки в системе МФР */
-- клиент */
-- СНИЛС клиента */
-- идентификатор системы, через ко/ую происходит автопополнение баланса */
-- дата последнего успешного платежа */
-- дата последнего неуспешного платежа */
-- количество неуспешных платежей подряд */
-- статус последнего платежа по подписке */
-- маскированный номер карты Плательщика, вида 400000|0002 */
-- имя держателя карты */
-- срок действия карты, месяц */
-- срок действия, год */
CREATE TABLE cf_bank_subscriptions (
  IdOfSubscription BIGSERIAL,
  PaymentAmount BIGINT NOT NULL,
  ThresholdAmount BIGINT NOT NULL,
  MonthsCount INTEGER NOT NULL,
  ValidToDate BIGINT,
  ActivationDate BIGINT,
  DeactivationDate BIGINT,
  IsActive INTEGER,
  Status VARCHAR(255),
  PaymentId VARCHAR(32),
  IdOfClient BIGINT NOT NULL,
  San VARCHAR(11),
  PaySystem INTEGER NOT NULL,
  LastSuccessfulPaymentDate BIGINT,
  LastUnsuccessfulPaymentDate BIGINT,
  UnsuccessfulPaymentsCount INTEGER,
  LastPaymentStatus VARCHAR(255),
  MaskedCardNumber VARCHAR(11),
  CardHolder VARCHAR(255),
  ExpMonth INTEGER,
  ExpYear INTEGER,
  CONSTRAINT cf_bank_subscriptions_pk PRIMARY KEY (IdOfSubscription),
  CONSTRAINT cf_bank_subscriptions_client_fk FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);

--!  Таблица отправленных запросов ИС ПП в МФР (на подключение подписки, ее отключение, списание средств).
-- уникальный идентификатор запроса ИС ПП в МФР */
-- уникальный идентификатор подписки на услугу в ИС ПП */
-- идентификатор системы, через ко/ую проходят платежи */
-- тип запроса (подключение, отключение, списание средств) */
-- URL запроса */
-- дата и время запроса */
-- флаг успешности запроса: 1 - на запрос пришел ответ, 0 - иначе */
-- статус ответа на запрос */
-- обрабатываемый клиент */
-- СНИЛС клиента */
-- описание ошибки в случае неудачного запроса */
CREATE TABLE cf_mfr_requests (
  IdOfRequest BIGSERIAL,
  IdOfSubscription BIGINT NOT NULL,
  PaySystem INTEGER NOT NULL,
  RequestType INTEGER NOT NULL,
  RequestURL VARCHAR(255) NOT NULL,
  RequestTime BIGINT NOT NULL,
  IsSuccess INTEGER NOT NULL,
  ResponseStatus VARCHAR(255),
  IdOfClient BIGINT NOT NULL,
  San VARCHAR(11),
  ErrorDescription VARCHAR(255),
  CONSTRAINT cf_mfr_requests_subscription_fk FOREIGN KEY (IdOfSubscription) REFERENCES cf_bank_subscriptions (IdOfSubscription),
  CONSTRAINT cf_mfr_requests_pk PRIMARY KEY (IdOfRequest),
  CONSTRAINT cf_mfr_requests_client_fk FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);

--! Таблица ежемесячных платежей, осуществляемых по банковской подписке.
-- id платежа */
-- уникальный идентификатор подписки на услугу в ИС ПП, по ко/ой совершается платеж */
-- уникальный идентификатор запроса ИС ПП в МФР */
-- сумма платежа */
-- дата и время платежа */
-- клиент */
-- баланс клиента на момент запуска платежа */
-- установленное у подписки пороговое значение баланса на момент запуска */
-- результат платежа (осуществлен или нет) */
-- статус платежа */
-- код авторизации */
-- RRN транзакции */
CREATE TABLE cf_regular_payments (
  IdOfPayment BIGSERIAL,
  IdOfSubscription BIGINT NOT NULL,
  IdOfRequest BIGINT NOT NULL,
  PaymentAmount BIGINT NOT NULL,
  PaymentDate BIGINT,
  IdOfClient BIGINT NOT NULL,
  ClientBalance BIGINT NOT NULL,
  ThresholdAmount BIGINT NOT NULL,
  IsSuccess INTEGER NOT NULL,
  Status VARCHAR(255),
  AuthCode VARCHAR(6),
  RRN BIGINT,
  CONSTRAINT cf_regular_payments_pk PRIMARY KEY (IdOfPayment),
  CONSTRAINT cf_regular_payments_subscription_fk FOREIGN KEY (IdOfSubscription) REFERENCES cf_bank_subscriptions (IdOfSubscription),
  CONSTRAINT cf_regular_payments_request_fk FOREIGN KEY (IdOfRequest) REFERENCES cf_mfr_requests (IdOfRequest),
  CONSTRAINT cf_regular_payments_client_fk FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);


CREATE TABLE CF_RegistrySms (
  IdOfRegistrySMS            BIGINT          NOT NULL,
  Version                 BIGINT          NOT NULL,
  SmsId                   CHAR(16)        NOT NULL,
  CONSTRAINT CF_RegistrySms_pk PRIMARY KEY (IdOfRegistrySMS)
);



INSERT INTO CF_RegistrySMS(IdOfRegistrySMS, version, smsid) VALUES (1, 0, (select smsid from CF_Registry where IdOfRegistry=1));

-- ECAFE-1224 	Перевести взаимодействие с ИС РНиП на формат 15.2
-- ECAFE-1329 Запрос на отчетность. Изменили ширину отчета + поправили имена при выгрузке отчета в xls
-- ECAFE-1348 В коннектор к СМС-шлюзу Альтарикс исключить генерацию messageId через базу - он не используется.
-- Генерация осталась просто значения ящерки перенесено в другую таблицу.
-- ECAFE-1347 При отправке СМС используется поле-генератор номера из класса Registry,
-- где так же хранится номер текущей версии клиентского регистра что приводит к конфиктам
-- Конфликт создается из изменения одной записи в 2-х разных транзакциях.
-- Но при отправки СМС она не повторяется после решения ECAFE-1348.

--! ФИНАЛИЗИРОВАН (Кадыров, 131108) НЕ МЕНЯТЬ