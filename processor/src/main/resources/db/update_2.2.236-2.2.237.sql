--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 237

drop table cf_wt_dish_categoryitem_relationships;
drop table cf_wt_category_items cascade;
drop table cf_wt_categories;

create sequence cf_wt_category_items_idofcategoryitem_seq
  maxvalue 2147483648;
create sequence cf_wt_category_items_idofcategoryitem_seq1
  maxvalue 2147483648;

alter table cf_wt_dishes drop column idofcategory;

create table cf_wt_categories
(
  idofcategory bigserial not null,
  createdate   timestamp         not null,
  lastupdate   timestamp         not null,
  version      bigint  default 0 not null,
  idofuser     bigint            not null,
  guid         varchar(36)       not null,
  description  varchar(255)      not null,
  deletestate  integer default 0,
  constraint cf_wt_categories_pk
  primary key (idofcategory),
  constraint cf_wt_categories_user_fk foreign key (idofuser)
  references cf_users (idofuser),
  constraint cf_wt_category_items_description_check
  check (cast(description as text) !~ similar_escape(cast(' *' as text), cast(NULL as text))),
  constraint cf_wt_categories_guid_uk
  unique (guid)
);


comment on column cf_wt_categories.idofcategory is 'ID записи';

comment on column cf_wt_categories.createdate is 'Дата создания';

comment on column cf_wt_categories.lastupdate is 'Дата последнего обновления';

comment on column cf_wt_categories.version is 'Версия (для АРМ)';

comment on column cf_wt_categories.idofuser is 'ID создателя записи';

comment on column cf_wt_categories.guid is 'GUID (для АРМ)';

comment on column cf_wt_categories.description is 'Описание элемента';

create table cf_wt_category_items
(
  idofcategoryitem bigint not null,
  createdate       timestamp         not null,
  lastupdate       timestamp         not null,
  version          bigint  default 0 not null,
  idofuser         bigint            not null,
  guid             varchar(36)       not null
    constraint cf_wt_category_items_guid_uk
    unique,
  description      varchar(255)      not null
    constraint cf_wt_category_items_description_uk
    unique,

  deletestate      integer default 0,
  idofcategory     bigint            not null,
  constraint cf_wt_category_items_pk
  primary key (idofcategoryitem),
  constraint cf_wt_category_items_fk foreign key (idofuser)
  references cf_users (idofuser),
  constraint cf_wt_category_items_category_fk foreign key (idofcategory)
  references cf_wt_categories (idofcategory) on update cascade on delete cascade,
  constraint cf_wt_categories_guid_uk
  unique (guid),
  constraint cf_wt_category_items_description_check
  check (cast(description as text) !~ similar_escape(cast(' *' as text), cast(NULL as text)))
);

comment on column cf_wt_category_items.idofcategoryitem is 'ID записи';

comment on column cf_wt_category_items.createdate is 'Дата создания';

comment on column cf_wt_category_items.lastupdate is 'Дата последнего обновления';

comment on column cf_wt_category_items.version is 'Версия (для АРМ)';

comment on column cf_wt_category_items.idofuser is 'ID создателя записи';

comment on column cf_wt_category_items.guid is 'GUID (для АРМ)';

comment on column cf_wt_category_items.description is 'Описание элемента';

create table cf_wt_dish_categoryitem_relationships
(
  idofdish         bigint not null
    constraint cf_wt_dish_categoryitem_relationships_idofdish_fkey
    references cf_wt_dishes on update cascade on delete cascade,
  idofcategoryitem bigint not null
    constraint cf_wt_dish_categoryitem_relationships_idofcategoryitem_fkey
    references cf_wt_category_items
    on update cascade on delete cascade,
  constraint cf_wt_dish_categoryitem_relationships_pk
  primary key (idofdish, idofcategoryitem)
);

alter table cf_wt_dishes add column idofcategory bigint
  constraint cf_wt_dishes_idofcategory_fk references cf_wt_categories (idofcategory) on update cascade on delete set null;

--! ФИНАЛИЗИРОВАН 15.10.2020, НЕ МЕНЯТЬ