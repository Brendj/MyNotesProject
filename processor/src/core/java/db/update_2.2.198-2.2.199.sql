--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 199

-- 211

-- Таблица для хранения заявок от ЭЖД
CREATE TABLE cf_goods_requests_ezd (
  idrequestsezd   bigserial,		-- первичный ключ
  idoforg         int8,       -- идентификатор организации
  groupName 		varchar,	-- наименование группы обучающихся
  dateappointment int8,		-- дата предварительной заявки
  idOfComplex   	int8,		-- идентификатор комплекса
  complexName     varchar, 	-- наименование комплекса
  complexcount	int4, 		-- количество комплексов
  userName 		varchar, 	-- ФИО пользователя, который подал предварительную заявку
  createddate 	int8,		-- дата создания записи
  lastupdate 		int8, 		-- дата изменения записи
  versionrecord	int4,		-- версия записи
  guid 			varchar 	-- guid заявки
);

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

ALTER TABLE public.cf_orgs ADD preorderlp bool NULL DEFAULT true;
COMMENT ON COLUMN public.cf_orgs.preorderlp IS 'Предварительные заявки по ЛП';

ALTER TABLE public.cf_orgs ADD havenewlp bool NULL DEFAULT false;
COMMENT ON COLUMN public.cf_orgs.havenewlp IS 'Имеются заявки, не отправленные в АРМ';

ALTER TABLE public.cf_goods_requests_ezd ADD guid varchar NULL;


--277

ALTER TABLE public.cf_card_signs ADD deleted bool NULL DEFAULT false;
COMMENT ON COLUMN public.cf_card_signs.deleted IS 'Метка, что запись удалена';