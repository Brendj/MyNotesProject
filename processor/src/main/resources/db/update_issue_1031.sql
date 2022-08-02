-- Пакет обновлений issue 1031

create table cf_applications_for_food_discount(
    idofappdiscount bigserial not null,
    idofapplicationforfood bigint not null,
    dtiszncode integer,
    CONSTRAINT cf_applications_for_food_discount_pk PRIMARY KEY (idofappdiscount),
    CONSTRAINT cf_applications_for_food_discount_app FOREIGN KEY (idofapplicationforfood)
        REFERENCES cf_applications_for_food (idofapplicationforfood) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT cf_applications_for_food_discount_dszn FOREIGN KEY (dtiszncode)
        REFERENCES cf_categorydiscounts_dszn (code) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

comment on table cf_applications_for_food_discount is 'Льготы по ЗЛП';
comment on column cf_applications_for_food_discount.idofappdiscount is 'Первичный ключ';
comment on column cf_applications_for_food_discount.idofapplicationforfood is 'Ссылка на заявление';
comment on column cf_applications_for_food_discount.dtiszncode is 'Код льготы ДТиСЗН';

insert into cf_applications_for_food_discount(idofapplicationforfood, dtiszncode)
select idofapplicationforfood, dtiszncode from cf_applications_for_food;

alter table cf_applications_for_food drop column dtiszncode;

CREATE INDEX cf_applications_for_food_discount_app_idx ON cf_applications_for_food_discount USING btree (idofapplicationforfood);

alter table cf_categorydiscounts_dszn
    add column etptextcode character varying(32),
    add column priority integer;

comment on column cf_categorydiscounts_dszn.etptextcode is 'Новый текстовый код льготы от ЕТП';
comment on column cf_categorydiscounts_dszn.priority is 'Приоритет льготы';