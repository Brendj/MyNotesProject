--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.78

-- Добавление генератора идентифиактора для оборудования корпусов
ALTER TABLE CF_Generators ADD COLUMN IdOfAccessory BIGINT NOT NULL DEFAULT 0;

--! ФИНАЛИЗИРОВАН (Сунгатов, 141021) НЕ МЕНЯТЬ