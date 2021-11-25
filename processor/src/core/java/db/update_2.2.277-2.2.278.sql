/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

-- Пакет обновлений v 278

--102 АРМ
CREATE SEQUENCE public.cf_wa_journal_card_idofoperation_seq INCREMENT BY 32 MINVALUE 1 MAXVALUE 9223372036854775807 START 1;

CREATE TABLE cf_wa_journal_card ( idOfOperation int8 DEFAULT nextval('cf_wa_journal_card_idofoperation_seq') NOT NULL, operationType int4 not null, idofuser int8 null, cardNo int8 null, longcardno int8 null, createddate timestamp not null, idofclient int8 NULL, CONSTRAINT cf_wa_journal_card_pkey PRIMARY KEY (idofoperation), CONSTRAINT cf_wa_journal_card_idofclient_fk FOREIGN KEY (idofclient) REFERENCES cf_clients(idofclient), CONSTRAINT cf_wa_journal_card_idofuser_fk FOREIGN KEY (idofuser) REFERENCES cf_users(idofuser) );

COMMENT ON TABLE cf_wa_journal_card IS 'Логирование операций по картам на Web Администраторе';
COMMENT ON COLUMN cf_wa_journal_card.operationtype IS 'Тип операции: 0 - Зарегистрировать карту 1 - Выдать карту клиенту 2 - Воврат/разблокировка карты 3 - Блокировка карты'; COMMENT ON COLUMN cf_wa_journal_card.idofuser IS 'Идентификатор пользователя (ссылка на cf_users)'; COMMENT ON COLUMN cf_wa_journal_card.cardno IS 'Физический номер электронного идентификатора (UID)'; COMMENT ON COLUMN cf_wa_journal_card.longcardno IS 'Физический номер электронного идентификатора (UID) для карт более 4-х байт'; COMMENT ON COLUMN cf_wa_journal_card.createddate IS 'Дата создания'; COMMENT ON COLUMN cf_wa_journal_card.idofclient IS 'Клиент, к которому привязана карта';

--32 АРМ
create table cf_wt_menu_invisible_dish ( idofmenu     bigint    not null, idoforg      bigint    not null, idofdish     bigint    not null, version      bigint    not null, deletestate  int       not null, createdate   timestamp not null, create_by_id bigint    not null, lastupdate   timestamp not null, update_by_id bigint    not null, constraint cf_wt_menu_invisible_dish_pk primary key (idofmenu, idoforg, idofdish), constraint cf_wt_menu_invisible_dish_menu_fk foreign key (idofmenu) references cf_wt_menu (idofmenu), constraint cf_wt_menu_invisible_dish_org_fk foreign key (idoforg) references cf_orgs (idoforg), constraint cf_wt_menu_invisible_dish_dish_fk foreign key (idofdish) references cf_wt_dishes (idofdish), constraint cf_wt_menu_invisible_dish_create_fk foreign key (create_by_id) references cf_users (idofuser), constraint cf_wt_menu_invisible_dish_update_fk foreign key (update_by_id) references cf_users (idofuser) );
create table cf_wt_complex_invisible_dish ( idofcomplex  bigint    not null, idoforg      bigint    not null, idofdish     bigint    not null, version      bigint    not null, deletestate  int       not null, createdate   timestamp not null, create_by_id bigint    not null, lastupdate   timestamp not null, update_by_id bigint    not null, constraint cf_wt_complex_invisible_dish_pk primary key (idofcomplex, idoforg, idofdish), constraint cf_wt_complex_invisible_dish_complex_fk foreign key (idofcomplex) references cf_wt_complexes (idofcomplex), constraint cf_wt_complex_invisible_dish_org_fk foreign key (idoforg) references cf_orgs (idoforg), constraint cf_wt_complex_invisible_dish_dish_fk foreign key (idofdish) references cf_wt_dishes (idofdish), constraint cf_wt_complex_invisible_dish_create_fk foreign key (create_by_id) references cf_users (idofuser), constraint cf_wt_complex_invisible_dish_update_fk foreign key (update_by_id) references cf_users (idofuser) );

--33 АРМ
create table cf_wt_menu_priority_dish ( idofmenu     bigint    not null, idoforg      bigint    not null, idofdish     bigint    not null, version      bigint    not null, deletestate  int       not null, createdate   timestamp not null, create_by_id bigint    not null, lastupdate   timestamp not null, update_by_id bigint    not null, constraint cf_wt_menu_priority_dish_pk primary key (idofmenu, idoforg, idofdish), constraint cf_wt_menu_priority_dish_menu_fk foreign key (idofmenu) references cf_wt_menu (idofmenu), constraint cf_wt_menu_priority_dish_org_fk foreign key (idoforg) references cf_orgs (idoforg), constraint cf_wt_menu_priority_dish_dish_fk foreign key (idofdish) references cf_wt_dishes (idofdish), constraint cf_wt_menu_priority_dish_create_fk foreign key (create_by_id) references cf_users (idofuser), constraint cf_wt_menu_priority_dish_update_fk foreign key (update_by_id) references cf_users (idofuser) );

--987
ALTER TABLE cf_prohibitions ADD COLUMN idofdish BIGINT REFERENCES cf_wt_dishes(idofdish), ADD COLUMN idofcategory BIGINT REFERENCES cf_wt_categories(idofcategory), ADD COLUMN idofcategoryitem BIGINT REFERENCES cf_wt_category_items(idofcategoryitem), ALTER COLUMN filtertext DROP NOT NULL, ALTER COLUMN prohibitionfiltertype DROP NOT NULL;
comment on column cf_prohibitions.idofdish is 'Идентификатор запрещенного блюда (ссылка на cf_wt_dishes)'; comment on column cf_prohibitions.idofcategory is 'Идентификатор запрещенной категории (ссылка на cf_wt_categories)'; comment on column cf_prohibitions.idofcategoryitem is 'Идентификатор запрещенной подкатегории (ссылка на cf_wt_category_items)';

--! ФИНАЛИЗИРОВАН 04.10.2021, НЕ МЕНЯТЬ