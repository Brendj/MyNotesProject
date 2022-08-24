-- Пакет обновлений issue 1031

create table cf_applications_for_food_discount(
    idofappdiscount bigserial not null,
    idofapplicationforfood bigint not null,
    dtiszncode integer,
    confirmed integer not null default 0,
    startdate int8 NULL,
    enddate int8 NULL,
    appointedMSP integer NOT NULL DEFAULT 0,
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
comment on column cf_applications_for_food_discount.confirmed is 'Флаг подтвержденности льготы';
comment on column cf_applications_for_food_discount.startdate is 'Начало действия';
comment on column cf_applications_for_food_discount.enddate is 'Окончание действия';
comment on column cf_applications_for_food_discount.appointedMSP is 'По льготе назначена МСП';

insert into cf_applications_for_food_discount(idofapplicationforfood, dtiszncode)
select idofapplicationforfood, dtiszncode from cf_applications_for_food;

alter table cf_applications_for_food drop column dtiszncode,
    add column validdoc integer,
    add column validguardianship integer,
    add column docconfirmed integer not null default 0,
    add column guardianshipconfirmed integer not null default 0;

comment on column cf_applications_for_food.validdoc is 'Признак валидности паспорта заявителя в заявлении ServiceProperties/Validity';
comment on column cf_applications_for_food.validguardianship is 'Признак подтверждено ли родство заявителя в заявлении ServiceProperties/ValidationGuardianship';
comment on column cf_applications_for_food.docconfirmed is 'Признак валидности паспорта после взаимодействия с межведом: 0 - Не подтверждено в мэш.межвед, 1 - Отправлен запрос, 2 - Подтверждено, или не требует подтверждения';
comment on column cf_applications_for_food.guardianshipconfirmed is 'Признак подтверждения родства заявителя после взаимодействия с межведом';

CREATE INDEX cf_applications_for_food_discount_app_idx ON cf_applications_for_food_discount USING btree (idofapplicationforfood);

alter table cf_categorydiscounts_dszn
    add column etptextcode character varying(32),
    add column priority integer;

comment on column cf_categorydiscounts_dszn.etptextcode is 'Новый текстовый код льготы от ЕТП';
comment on column cf_categorydiscounts_dszn.priority is 'Приоритет льготы';

CREATE TABLE cf_app_mezhved_request
(
  idofmezhvedrequest bigserial not null,
  idofapplicationforfood bigint not null,
  requestid character varying (36) not null,
  requestpayload text,
  responsepayload text,
  createddate bigint NOT NULL,
  lastupdate bigint,
  requesttype integer NOT NULL,
  responsetype integer,
  responsedate bigint,
  CONSTRAINT cf_app_mezhved_request_pk PRIMARY KEY (idofmezhvedrequest),
  CONSTRAINT cf_applications_for_food_discount_app FOREIGN KEY (idofapplicationforfood)
        REFERENCES cf_applications_for_food (idofapplicationforfood) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE INDEX cf_app_mezhved_request_id_idx ON cf_app_mezhved_request USING btree (requestid);

comment on table cf_app_mezhved_request is 'Запросы в мэш.межвед по подтверждению документов и родства заявителя';
comment on column cf_app_mezhved_request.idofmezhvedrequest is 'Первичный ключ';
comment on column cf_app_mezhved_request.idofapplicationforfood is 'Ссылка на заявление';
comment on column cf_app_mezhved_request.requestid is 'Ид запроса';
comment on column cf_app_mezhved_request.requestpayload is 'Тело запроса';
comment on column cf_app_mezhved_request.responsepayload is 'Тело ответа';
comment on column cf_app_mezhved_request.createddate is 'Дата отправки запроса';
comment on column cf_app_mezhved_request.lastupdate is 'Последнее изменение';
comment on column cf_app_mezhved_request.requesttype is 'Тип запроса: 0 - Подтверждение паспорта, 1 - Подтверждение родства';
comment on column cf_app_mezhved_request.responsetype is 'Тип ответа: 0, - Не подтверждено, 1 - Подтверждено';
comment on column cf_app_mezhved_request.responsedate is 'Дата ответа';

-- 1080
CREATE TABLE public.cf_mezhved_response_document (
                                                     idofmezhvedresponse bigserial NOT NULL,
                                                     requestid varchar(36) NOT NULL,
                                                     benefit_category_id int8 NULL,
                                                     "name" varchar NULL,
                                                     series varchar NULL,
                                                     "number" varchar NULL,
                                                     issue_date int8 NULL,
                                                     issuer varchar NULL,
                                                     "type" int4 null,
                                                     createdate int8 NULL,
                                                     CONSTRAINT cf_mezhved_response_document_pk PRIMARY KEY (idofmezhvedresponse)
);
CREATE INDEX cf_mezhved_response_document_id_idx ON public.cf_mezhved_response_document (requestid);

alter table cf_client_dtiszn_discount_info
  add column appointedMSP integer;