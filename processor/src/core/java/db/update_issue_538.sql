/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

-- Перенос существующих категорий
ALTER TABLE cf_wt_category_items
    RENAME TO cf_wt_categories;
ALTER TABLE cf_wt_categories
    RENAME COLUMN idofcategoryitem TO idofcategory;

-- Создание подкатегорий (сгенерировано в DataGrip)
create table cf_wt_category_items
(
    idofcategoryitem bigserial         not null
        constraint cf_wt_category_items_pk_2
            primary key,
    createdate       timestamp         not null,
    lastupdate       timestamp         not null,
    version          bigint  default 0 not null,
    idofuser         bigint            not null
        constraint cf_wt_category_items_fk_2
            references cf_users,
    guid            varchar(36)       not null unique,
    description      varchar(255)      not null
        constraint cf_wt_category_items_description_check_2
            CHECK (description NOT SIMILAR TO ' *'),
    deletestate      integer default 0,
    idofcategory bigint not null references cf_wt_categories(idofcategory)
);

comment on column cf_wt_category_items.idofcategoryitem is 'ID записи';
comment on column cf_wt_category_items.createdate is 'Дата создания';
comment on column cf_wt_category_items.lastupdate is 'Дата последнего обновления';
comment on column cf_wt_category_items.version is 'Версия (для АРМ)';
comment on column cf_wt_category_items.idofuser is 'ID создателя записи';
comment on column cf_wt_category_items.guid is 'GUID (для АРМ)';
comment on column cf_wt_category_items.description is 'Описание элемента';