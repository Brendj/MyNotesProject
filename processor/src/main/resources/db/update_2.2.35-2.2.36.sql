--! Пока скрипт не винализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.36

-- Добавлен веб сервис по выдаче файлов отчета [http|https]://hostname/processor/soap/integro?wsdl
-- Доработана обработка базовой корзины и отображение сторимости базовой корзины в АРМ Мониторинг во вкладке "Мониторинг / Отчет по показателям цен и продаж"
--! Добавлена дата отображения опросника если пусто то отобразится во все дни
-- В опросник добален не обязательный параметр дата не обходим для работы с опросником типа "Меню"
ALTER TABLE cf_qa_questionaries ADD COLUMN viewdate bigint;

--! нет необходимости даной таблицы
DROP TABLE cf_qa_questionaryresultbyorg;

-- Добавлена возможность просмотра в личном кабинете (вкладка "Покупки и платежи") заказы которые подтвердил взрослый клиент за ребенка в случае если ребенок ушел в минус
--! доавлены таблицы учета платежей за счет учителей за детей которые ушли в минус
CREATE TABLE CF_Group_Payment_Confirm
(
  IdOfGroupPaymentConfirm bigserial NOT NULL,
  GlobalVersion bigint,
  GlobalVersionOnCreate bigint,
  GUID character varying(36) NOT NULL,
  DeletedState boolean DEFAULT FALSE,
  DeleteDate bigint,
  LastUpdate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 1,
  OrgOwner bigint,
  ConfirmerId bigint,
  CONSTRAINT CF_Group_Payment_Confirm_PK                  PRIMARY KEY (IdOfGroupPaymentConfirm),
  CONSTRAINT CF_Group_Payment_Confirm_GUID_UNIQUE UNIQUE      (Guid)
);

CREATE TABLE CF_Group_Payment_Confirm_Position
(
  IdOfGroupPaymentConfirmPosition bigserial NOT NULL,
  IdOfGroupPaymentConfirm bigserial NOT NULL,
  GlobalVersion bigint,
  GlobalVersionOnCreate bigint,
  GUID character varying(36) NOT NULL,
  DeletedState boolean DEFAULT FALSE,
  DeleteDate bigint,
  LastUpdate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 1,
  OrgOwner bigint,
  idOfOrder bigint,
  CONSTRAINT CF_Group_Payment_Confirm_Position_PK                  PRIMARY KEY (IdOfGroupPaymentConfirmPosition),
  CONSTRAINT CF_Group_Payment_Confirm_Position_GUID_UNIQUE UNIQUE      (Guid)
);

ALTER TABLE CF_ClientSms ALTER COLUMN IdOfSms type CHAR(40);

CREATE index cf_enterevents_org_event_idx ON CF_EnterEvents (idOfOrg, idOfEnterEvent);

--! ФИНАЛИЗИРОВАН (Кадыров, 130222) НЕ МЕНЯТЬ


