--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.152

--Новый флаг запрета на оплату при расхождении времени
alter table cf_orgs add column allowRegistryChangeEmployee integer not null default 0;

--Очистка флагов работы ОО в летний период
update cf_orgs set isWorkInSummerTime = 0;

--Таблица с номерами карт, по которым пропускаем проверку ЦП
create table cf_cards_special (
  cardno bigint NOT NULL,
  CONSTRAINT cf_cards_special_pk PRIMARY KEY (cardno)
);

--Признак длинного идентификатора карты
alter table cf_cards add column islonguid boolean;

alter table cf_synchistory add column synctype integer;

--Флаг, что по подписке отправлено уведомление об окончании срока действия
alter table cf_bank_subscriptions add column notificationSent integer;