-- Пакет обновлений v 287

CREATE SEQUENCE cf_preorder_complex_version_seq;
select setval('cf_preorder_complex_version_seq', (select coalesce(max(version), 0) + 1 from cf_preorder_complex));

CREATE SEQUENCE public.cf_foodbox_preorders_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1;

CREATE TABLE cf_foodbox_preorders (
                                      id int8 DEFAULT nextval('cf_foodbox_preorders_id_seq') NOT NULL,
                                      state int4 NULL,
                                      idofclient int8 NULL,
                                      initialdatetime int8 NULL,
                                      error varchar NULL,
                                      idoffoodbox int8 NULL,
                                      cellnumber int4 NULL,
                                      idoforder int8 NULL,
                                      cancelreason int4 NULL,
                                      "version" int8 NULL,
                                      createdate int8 NULL,
                                      updatedate int8 NULL,
                                      idoforg int8 NULL,
                                      idfoodboxexternal varchar NULL,
                                      orderprice int8 NULL,
                                      located bool NULL,
                                      posted int4 NULL
);

COMMENT ON COLUMN public.cf_foodbox_preorders.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN public.cf_foodbox_preorders.state IS 'Статус предзаказа (0 - новый, 1 - собран, 2 - загружен в ячейку, 3 - исполнен, 4 - аннулирован)';
COMMENT ON COLUMN public.cf_foodbox_preorders.idofclient IS 'Идентификатор клиента, для которого сделан заказ (ссылка на cf_clients)';
COMMENT ON COLUMN public.cf_foodbox_preorders.initialdatetime IS 'Дата создания предзаказа (не используется)';
COMMENT ON COLUMN public.cf_foodbox_preorders.error IS 'Текст ошибки (от АРМ)';
COMMENT ON COLUMN public.cf_foodbox_preorders.idoffoodbox IS 'Идентификатор фудбокса';
COMMENT ON COLUMN public.cf_foodbox_preorders.cellnumber IS 'Номер ячейки ';
COMMENT ON COLUMN public.cf_foodbox_preorders.idoforder IS 'Идентификатор заказа ';
COMMENT ON COLUMN public.cf_foodbox_preorders.cancelreason IS 'Причина отмены (0 – истек срок, 1 – Отменено кассиром, 2 отменено процессингом, не обязательное)';
COMMENT ON COLUMN public.cf_foodbox_preorders."version" IS 'Версия';
COMMENT ON COLUMN public.cf_foodbox_preorders.createdate IS 'Дата создания';
COMMENT ON COLUMN public.cf_foodbox_preorders.updatedate IS 'Дата последнего обновления';
COMMENT ON COLUMN public.cf_foodbox_preorders.idoforg IS 'Идентификатор организации, для которой заказ (ссылка на cf_orgs)';
COMMENT ON COLUMN public.cf_foodbox_preorders.idfoodboxexternal IS 'Идентификатор заказа полученный от МЭШ';
COMMENT ON COLUMN public.cf_foodbox_preorders.orderprice IS 'Общая стоймость заказа';
COMMENT ON COLUMN public.cf_foodbox_preorders.located IS 'Флаг того, что у заказа есть ячейка в фудбокс (не используется)';
COMMENT ON COLUMN public.cf_foodbox_preorders.posted IS 'Если 0 - то заказ ещё не обработан АРМ. Если 1 то заказ захватил ячейку, если 2 - то заказ освободил ячейку ';


CREATE SEQUENCE public.cf_foodbox_preorders_dish_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1;

CREATE TABLE cf_foodbox_preorders_dish (
                                           id int8 DEFAULT nextval('cf_foodbox_preorders_dish_id_seq') NOT NULL,
                                           foodboxid int8 NULL,
                                           idofdish int8 NULL,
                                           price int4 NULL,
                                           qty int4 NULL,
                                           createdate int8 NULL,
                                           updatedate int8 NULL,
                                           "name" varchar NULL,
                                           buffetcategoriesid int8 NULL,
                                           buffetcategoriesname varchar NULL
);

COMMENT ON COLUMN public.cf_foodbox_preorders_dish.id IS 'Идентификатор записи';
COMMENT ON COLUMN public.cf_foodbox_preorders_dish.foodboxid IS 'Идентификатор заказа (ссылка на cf_foodbox_preorders)';
COMMENT ON COLUMN public.cf_foodbox_preorders_dish.idofdish IS 'Идентификатор блюда (ссылка на cf_wt_dishes)';
COMMENT ON COLUMN public.cf_foodbox_preorders_dish.price IS 'Цена';
COMMENT ON COLUMN public.cf_foodbox_preorders_dish.qty IS 'Количество/Вес';
COMMENT ON COLUMN public.cf_foodbox_preorders_dish.createdate IS 'Дата создания';
COMMENT ON COLUMN public.cf_foodbox_preorders_dish.updatedate IS 'Дата последнего изменения';
COMMENT ON COLUMN public.cf_foodbox_preorders_dish."name" IS 'Наименование блюда';
COMMENT ON COLUMN public.cf_foodbox_preorders_dish.buffetcategoriesid IS 'Идентификатор категории (ссылка на cf_wt_categories)';
COMMENT ON COLUMN public.cf_foodbox_preorders_dish.buffetcategoriesname IS 'Название категории';

CREATE SEQUENCE public.cf_foodbox_available_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1;

CREATE TABLE cf_foodbox_available (
                                      id int8 DEFAULT nextval('cf_foodbox_available_id_seq') NOT NULL,
                                      idofdish int8 NULL,
                                      availableqty int4 NULL,
                                      idoforg int8 NULL,
                                      createdate int8 NULL,
                                      updatedate int8 NULL,
                                      "version" int8 NULL
);

COMMENT ON COLUMN public.cf_foodbox_available.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN public.cf_foodbox_available.idofdish IS 'Идентификатор блюда (ссылка на cf_wt_dishes)';
COMMENT ON COLUMN public.cf_foodbox_available.availableqty IS 'Доступное количество для заказа';
COMMENT ON COLUMN public.cf_foodbox_available.idoforg IS 'Идентификатор организации, откуда пришел пакет (ссылка на cf_orgs)';
COMMENT ON COLUMN public.cf_foodbox_available.createdate IS 'Дата создания';
COMMENT ON COLUMN public.cf_foodbox_available.updatedate IS 'Дата последнего обновления';
COMMENT ON COLUMN public.cf_foodbox_available."version" IS 'Версия (пока не используется)';

CREATE SEQUENCE public.cf_foodbox_cells_foodboxesid_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1;

CREATE TABLE public.cf_foodbox_cells (
                                         foodboxesid int8 DEFAULT nextval('cf_foodbox_cells_foodboxesid_seq') NOT NULL,
                                         fbid int4 NULL,
                                         totalcellscount int4 NULL,
                                         busycells int4 NULL,
                                         idoforg int8 NULL,
                                         createdate int8 NULL,
                                         updatedate int8 NULL
);

COMMENT ON COLUMN public.cf_foodbox_cells.foodboxesid IS 'Идентификатор записи';
COMMENT ON COLUMN public.cf_foodbox_cells.fbid IS 'Идентификатор футбокса в рамках ОУ';
COMMENT ON COLUMN public.cf_foodbox_cells.totalcellscount IS 'Общее количество ячеек';
COMMENT ON COLUMN public.cf_foodbox_cells.busycells IS 'Зянятое количество ячеек';
COMMENT ON COLUMN public.cf_foodbox_cells.idoforg IS 'Идентификатор организации (ссылка на cf_orgs)';
COMMENT ON COLUMN public.cf_foodbox_cells.createdate IS 'Дата создания';
COMMENT ON COLUMN public.cf_foodbox_cells.updatedate IS 'Дата обновления';

ALTER TABLE public.cf_orgs ADD usedFoodbox bool NULL;
COMMENT ON COLUMN public.cf_orgs.usedFoodbox IS 'Флаг на использование фудбокса';


ALTER TABLE public.cf_clients ADD foodboxAvailability bool NULL;
COMMENT ON COLUMN public.cf_clients.foodboxAvailability IS 'Признак доступности использования фудбокса для ученика (задается представителем)';


CREATE SEQUENCE public.cf_foodbox_org_parallel_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1;

CREATE TABLE cf_foodbox_org_parallel (
                                         id int8 DEFAULT nextval('cf_foodbox_org_parallel_seq') NOT NULL,
                                         idoforg int8 NULL,
                                         "parallel" int4 NULL,
                                         available bool NULL
);

COMMENT ON COLUMN public.cf_foodbox_org_parallel.id IS 'Идентификатор настройки параллелей';
COMMENT ON TABLE public.cf_foodbox_org_parallel IS 'Настройка параллелей орг для фудбокса';
COMMENT ON COLUMN public.cf_foodbox_org_parallel.idoforg IS 'Идентификатор организации (ссылка на cf_orgs)';
COMMENT ON COLUMN public.cf_foodbox_org_parallel."parallel" IS 'Номер параллели';
COMMENT ON COLUMN public.cf_foodbox_org_parallel.available IS 'Флаг доступности параллели для фудбокса';


CREATE TABLE cf_foodbox_parallel_type (
                                          idType int8 null,
                                          "parallel" int4 NULL
);

INSERT INTO public.cf_foodbox_parallel_type (idtype,"parallel")
VALUES (1,8);
INSERT INTO public.cf_foodbox_parallel_type (idtype,"parallel")
VALUES (2,9);
INSERT INTO public.cf_foodbox_parallel_type (idtype,"parallel")
VALUES (3,10);
INSERT INTO public.cf_foodbox_parallel_type (idtype,"parallel")
VALUES (4,11);


ALTER TABLE public.cf_clients ADD foodboxavailabilityguardian bool NULL;
COMMENT ON COLUMN public.cf_clients.foodboxavailabilityguardian IS 'Флаг того, что значение фудбокса установлено представителем';


--! ФИНАЛИЗИРОВАН 30.05.2022, НЕ МЕНЯТЬ