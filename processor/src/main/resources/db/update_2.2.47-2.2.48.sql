-- --! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
-- --! после финализации они уберутся
-- --! Информация для разработчика -- информация для пользователя
--
-- -- Пакет обновлений 2.2.48

--! Добавление индекса по дате отправления для таблицы с отправленными смс
CREATE INDEX cf_clientsms_servicesenddate_idx ON cf_clientsms (servicesenddate ASC NULLS LAST);

-- ECAFE-1212 Добавить параметр периода выборки в отчет ContragentPaymentReport
-- Добавлен тип периода для данного отчета По умолчанию период За предыдущий месяц

-- ECAFE-1218 При полной синхронизации обновлять время последней синхронизации балансов

-- Доработат метод DAOUtils.getClientsAndCardsForOrgs(...) - используются левый join.
-- Processor.getAccRegistry(...) - Добавлен неоьязательный параметр параметр список ид клиентов,
-- для получения информации карт толькол по выбранным клиента, в случаее пестого значения списка
-- будет выведен весь список карт организации.

-- ECAFE-1227 - Исправлен онлайн отчет "Отчет по пополнению", добавена колонка количество оплат.

-- ECAFE-1292 - Исправлена проблема в регистрации новой временной карты.

-- Фиксация падения - ключевых показателие

-- ECAFE-1209 Исправлена отправка кода активации для МПГУ

-- ECAFE-1324 Поправлен вывод результата в отчете "Онлайн отчеты / Отчет по льготам / Отчет по всем организациям"

--! ФИНАЛИЗИРОВАН (Кадыров, 131031) НЕ МЕНЯТЬ

