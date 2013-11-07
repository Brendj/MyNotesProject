-- Добавление возможности описывать причину ошибки при сверке с ИС Реестры
alter table cf_registrychange_errors add column errordetails varchar(256) default '';

-- Добавление возможности определения срока хранения отчетов для выбранного правила
alter table CF_ReportHandleRules add column StoragePeriod bigint default -1;

-- ECAFE-1188 - Реализовать функцию авто-пополнения счета по банковской карте через Банк Москвы - Acquiropay
--! Таблица с информацией о подписках клиентов на автопополнение баланса с банк. карты.
CREATE TABLE cf_bank_subscriptions (
  IdOfSubscription BIGSERIAL,          /* уникальный идентификатор подписки на услугу в ИС ПП */
  PaymentAmount BIGINT NOT NULL,       /* сумма пополнения */
  ThresholdAmount BIGINT NOT NULL,     /* пороговое значение баланса, при достижении ко/го баланс автопополняется */
  MonthsCount INTEGER NOT NULL,        /* срок действия подписки (число месяцев c даты подключения) */
  ValidToDate BIGINT,                  /* дата, до ко\ой подписка активна (вычисляется относит-но даты подключения) */
  ActivationDate BIGINT,               /* дата подключения подписки */
  DeactivationDate BIGINT,             /* дата отключения подписки */
  IsActive INTEGER,                    /* флаг активности подписки */
  Status VARCHAR(255),                 /* статус подписки */
  PaymentId VARCHAR(32),               /* id подписки в системе МФР */
  IdOfClient BIGINT NOT NULL,          /* клиент */
  San VARCHAR(11),                     /* СНИЛС клиента */
  PaySystem INTEGER NOT NULL,          /* идентификатор системы, через ко/ую происходит автопополнение баланса */
  LastSuccessfulPaymentDate BIGINT,    /* дата последнего успешного платежа */
  LastUnsuccessfulPaymentDate BIGINT,  /* дата последнего неуспешного платежа */
  UnsuccessfulPaymentsCount INTEGER,   /* количество неуспешных платежей подряд */
  LastPaymentStatus VARCHAR(255),      /* статус последнего платежа по подписке */
  MaskedCardNumber VARCHAR(11),        /* маскированный номер карты Плательщика, вида 400000|0002 */
  CardHolder VARCHAR(255),             /* имя держателя карты */
  ExpMonth INTEGER,                    /* срок действия карты, месяц */
  ExpYear INTEGER,                     /* срок действия, год */
  CONSTRAINT cf_bank_subscriptions_pk PRIMARY KEY (IdOfSubscription),
  CONSTRAINT cf_bank_subscriptions_client_fk FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);

--!  Таблица отправленных запросов ИС ПП в МФР (на подключение подписки, ее отключение, списание средств).
CREATE TABLE cf_mfr_requests (
  IdOfRequest BIGSERIAL,              /* уникальный идентификатор запроса ИС ПП в МФР */
  IdOfSubscription BIGINT NOT NULL,   /* уникальный идентификатор подписки на услугу в ИС ПП */
  PaySystem INTEGER NOT NULL,         /* идентификатор системы, через ко/ую проходят платежи */
  RequestType INTEGER NOT NULL,       /* тип запроса (подключение, отключение, списание средств) */
  RequestURL VARCHAR(255) NOT NULL,   /* URL запроса */
  RequestTime BIGINT NOT NULL,        /* дата и время запроса */
  IsSuccess INTEGER NOT NULL,         /* флаг успешности запроса: 1 - на запрос пришел ответ, 0 - иначе */
  ResponseStatus VARCHAR(255),        /* статус ответа на запрос */
  IdOfClient BIGINT NOT NULL,         /* обрабатываемый клиент */
  San VARCHAR(11),                    /* СНИЛС клиента */
  ErrorDescription VARCHAR(255),      /* описание ошибки в случае неудачного запроса */
  CONSTRAINT cf_mfr_requests_subscription_fk FOREIGN KEY (IdOfSubscription) REFERENCES cf_bank_subscriptions (IdOfSubscription),
  CONSTRAINT cf_mfr_requests_pk PRIMARY KEY (IdOfRequest),
  CONSTRAINT cf_mfr_requests_client_fk FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);

--! Таблица ежемесячных платежей, осуществляемых по банковской подписке.
CREATE TABLE cf_regular_payments (
  IdOfPayment BIGSERIAL,              /* id платежа */
  IdOfSubscription BIGINT NOT NULL,   /* уникальный идентификатор подписки на услугу в ИС ПП, по ко/ой совершается платеж */
  IdOfRequest BIGINT NOT NULL,        /* уникальный идентификатор запроса ИС ПП в МФР */
  PaymentAmount BIGINT NOT NULL,      /* сумма платежа */
  PaymentDate BIGINT,                 /* дата и время платежа */
  IdOfClient BIGINT NOT NULL,         /* клиент */
  ClientBalance BIGINT NOT NULL,      /* баланс клиента на момент запуска платежа */
  ThresholdAmount BIGINT NOT NULL,    /* установленное у подписки пороговое значение баланса на момент запуска */
  IsSuccess INTEGER NOT NULL,         /* результат платежа (осуществлен или нет) */
  Status VARCHAR(255),                /* статус платежа */
  AuthCode VARCHAR(6),                /* код авторизации */
  RRN BIGINT,                         /* RRN транзакции */
  CONSTRAINT cf_regular_payments_pk PRIMARY KEY (IdOfPayment),
  CONSTRAINT cf_regular_payments_subscription_fk FOREIGN KEY (IdOfSubscription) REFERENCES cf_bank_subscriptions (IdOfSubscription),
  CONSTRAINT cf_regular_payments_request_fk FOREIGN KEY (IdOfRequest) REFERENCES cf_mfr_requests (IdOfRequest),
  CONSTRAINT cf_regular_payments_client_fk FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);