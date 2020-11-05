--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.120

--Новый ИД для фото
ALTER TABLE cf_clientphoto
  DROP CONSTRAINT cf_clientphoto_pk,
  ADD COLUMN idofclientphoto BIGSERIAL PRIMARY KEY,
  ADD CONSTRAINT cf_clientphoto_idofclient UNIQUE (idofclient);

--! ФИНАЛИЗИРОВАН (Семенов, 110916) НЕ МЕНЯТЬ