--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 199

-- 211

-- Таблица для хранения заявок от ЭЖД
CREATE TABLE cf_goods_requests_ezd (
  idrequestsezd   bigserial,
  idoforg         int8,
  groupName 		varchar,
  dateappointment int8,
  idOfComplex   	int8,
  complexName     varchar,
  complexcount	int4,
  userName 		varchar,
  createddate 	int8,
  lastupdate 		int8,
  versionrecord	int4,
  guid 			varchar
);

ALTER TABLE cf_orgs ADD preorderlp bool NULL DEFAULT true;
COMMENT ON COLUMN cf_orgs.preorderlp IS 'Предварительные заявки по ЛП';

-- Представление для удобства отображения данных для отправки в ЭЖД
CREATE view cf_ezd_request
  AS
    select distinct ROW_NUMBER() over () as id, co.idoforg, co.guid, cgto.groupname, cci.idofcomplex, cci.complexname, cci.menudate
    FROM cf_orgs co
      left join cf_groupnames_to_orgs cgto on cgto.idoforg = co.idoforg
      left join cf_complexinfo cci on cci.idoforg = co.idoforg
      left join cf_clientgroups cg on cg.idoforg = co.idoforg and cg.groupname = cgto.groupname
    where co.state = 1 and co.organizationtype <> 2 and cci.modefree = 1 and cgto.groupname is not null
          and cg.idofclientgroup < 1100000000
          and cci.menudate > cast((round(date_part('epoch',now())) * 1000) as bigint)
          and co.preorderlp is true
          and co.guid is not null order by co.guid, cgto.groupname, cci.menudate ;

-- Представление для удобства работы с специальными датами в проекции организации и группы
create view cf_ezd_request_special_date
  AS
    select ROW_NUMBER() over () as id,
      resulted.date,
      resulted.groupname,
      resulted.idoforg,
      resulted.isweekend
    from
      (
        select
          distinct
          res.date,
          res.groupname,
          res.idoforg,
          res.isweekend
        from (select sda.date, cgto.groupname, sda.idoforg, sda.isweekend
              from cf_specialdates sda
                left join cf_orgs co on co.idoforg = sda.idoforg
                left join cf_groupnames_to_orgs cgto on cgto.idoforg = sda.idoforg
              where sda.idoforg is not null and co.organizationtype <> 2 and sda.deleted = 0 and cgto.groupname is not null and
                    co.preorderlp is true and
                    sda.date > cast((round(date_part('epoch',now())) * 1000) as bigint) order by sda.idoforg, cgto.groupname, sda.date
             ) as res) as resulted;

ALTER TABLE cf_orgs ADD havenewlp bool NULL DEFAULT false;
COMMENT ON COLUMN cf_orgs.havenewlp IS 'Имеются заявки, не отправленные в АРМ';

--277

ALTER TABLE cf_card_signs ADD deleted bool NULL DEFAULT false;
COMMENT ON COLUMN cf_card_signs.deleted IS 'Метка, что запись удалена';

ALTER TABLE cf_categorydiscounts
  ADD COLUMN eligibletodelete integer not null default 0;

--! ФИНАЛИЗИРОВАН 30.10.2019, НЕ МЕНЯТЬ