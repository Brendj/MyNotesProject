/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 245

--833
alter table cf_wt_complexes add column comment varchar(128);
create sequence cf_wt_complexes_temp_idofcomplexestemp_seq
  minvalue 1
  maxvalue 2147483647
  increment 1
  cache 1;

alter table cf_wt_complexes
  add column idofparentcomplex bigint;
alter table cf_wt_complexes
  add constraint cf_wt_complexes_parentcomplex_fk
foreign key (idofparentcomplex) references cf_wt_complexes (idofcomplex);

INSERT INTO cf_wt_categories (idofcategory, createdate, lastupdate, idofuser, guid, description, version)
VALUES ((SELECT coalesce(max(idofcategory), 0) + 1 FROM cf_wt_categories), now(), now(), 1, '911f4061-d049-4mf7-87d3-01d91316764a',
        'Категория не указана',
        (SELECT  coalesce(MAX(VERSION), 0) FROM cf_wt_categories) + 1);
UPDATE cf_wt_dishes
SET idofcategory = (SELECT coalesce(max(idofcategory), 0) FROM cf_wt_categories c)
WHERE idofcategory IS NULL ;
ALTER TABLE cf_wt_dishes
  ALTER COLUMN idofcategory SET NOT NULL;

--738
ALTER TABLE public.cf_preorder_complex ADD cancelnotification bool NULL;
ALTER TABLE public.cf_regular_preorders ADD cancelnotification bool NULL;

CREATE INDEX cf_complexinfo_regular_idx
  ON cf_complexinfo USING btree
  (idofcomplex, idoforg, menudate);

COMMENT ON COLUMN public.cf_preorder_complex.cancelnotification IS 'Флаг, которые показывает, что сообщение об отмене данного предзаказа уже было отправлено клиенту';
COMMENT ON COLUMN public.cf_regular_preorders.cancelnotification IS 'Флаг, которые показывает, что сообщение об отмене данного предзаказа уже было отправлено клиенту';

--805
CREATE SEQUENCE public.cf_clients_mobile_history_id
  INCREMENT BY 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1;

CREATE TABLE cf_clients_mobile_history
(
  ifofclientsmobilehistory int8 default nextval('cf_clients_mobile_history_id') not null,
  idofclient int8 null,
  oldmobile varchar null,
  newmobile varchar null,
  action varchar null,
  createdate int8 null,
  source varchar null,
  showing varchar null,
  idofusers int8 null,
  idoforg int8 null,
  staffguid varchar null
);

COMMENT ON COLUMN public.cf_clients_mobile_history.ifofclientsmobilehistory IS 'Идентификатор записи';
COMMENT ON COLUMN public.cf_clients_mobile_history.idofclient IS 'Идентификатор клиента (ссылка на cf_clients)';
COMMENT ON COLUMN public.cf_clients_mobile_history.oldmobile IS 'Прежний номер мобильного';
COMMENT ON COLUMN public.cf_clients_mobile_history.newmobile IS 'Новый номер мобильного';
COMMENT ON COLUMN public.cf_clients_mobile_history."action" IS 'Действие с номером телефона: "Добавление", "Удаление", "Изменение"';
COMMENT ON COLUMN public.cf_clients_mobile_history.createdate IS 'Дата создания записи';
COMMENT ON COLUMN public.cf_clients_mobile_history."source" IS 'Источник изменений (название метода и/или описание того, что сделал пользователь или АРМ)';
COMMENT ON COLUMN public.cf_clients_mobile_history.showing IS 'Что отображается в поле "Где изменено" в блоке "Изменение номера телефона" ';
COMMENT ON COLUMN public.cf_clients_mobile_history.idofusers IS 'Идентификатор пользователя, сделавшего изменения (ссылка на cf_users)';
COMMENT ON COLUMN public.cf_clients_mobile_history.staffguid IS 'guid сотрудника, вызвавшего изменения в АРМ (приходит при синхронизации)';
COMMENT ON COLUMN public.cf_clients_mobile_history.idoforg IS 'Идентификатор организации, в которой сднлано изменение (ссылка на cf_orgs)';

--! ФИНАЛИЗИРОВАН 17.12.2020, НЕ МЕНЯТЬ