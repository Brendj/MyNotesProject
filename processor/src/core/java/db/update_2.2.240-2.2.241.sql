--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 241
COMMENT ON TABLE public.cf_cr_cardactionrequests IS 'Таблица запросов от внешней системы на блокировку/разблокировку карт';
COMMENT ON TABLE public.cf_cr_cardactionclient IS 'Таблица заблокированных/разблокированных карт от внешней системы';

--784
CREATE OR REPLACE FUNCTION fix_settingtype()
  RETURNS INTEGER
LANGUAGE 'plpgsql' AS '
DECLARE
  query         RECORD;
  vers          INTEGER;
BEGIN
  FOR query IN SELECT havent.idoforg AS idoforg
               FROM
                 (SELECT co.idoforg
                  FROM cf_orgs co
                  WHERE co.idoforg NOT IN
                        (SELECT DISTINCT (os.idoforg)
                         FROM cf_orgsettings_Items osi
                           LEFT JOIN cf_orgsettings os ON os.idoforgsetting = osi.idoforgsetting
                         WHERE osi.settingtype = 11001) AND co.organizationtype IN (0, 1, 3)) AS havent
               WHERE havent.idoforg NOT IN (SELECT os.idoforg
                                            FROM cf_orgsettings os
                                            WHERE os.settinggroup = 9)
  LOOP
    INSERT INTO cf_orgsettings (idoforg, createddate, lastupdate, settinggroup, "version")
    VALUES (query.idoforg, 1605610866000, 1605610866000, 9, 0);
  END LOOP;
  FOR query IN
  SELECT coo.idoforgsetting
  FROM cf_orgsettings coo
    LEFT JOIN cf_orgs cc ON cc.idoforg = coo.idoforg
  WHERE coo.settinggroup = 9
        AND coo.idoforg NOT IN
            (SELECT DISTINCT (os.idoforg)
             FROM cf_orgsettings_Items osi
               LEFT JOIN cf_orgsettings os ON os.idoforgsetting = osi.idoforgsetting
             WHERE osi.settingtype = 11001) AND cc.organizationtype IN (0, 1, 3)
  LOOP
    INSERT INTO cf_orgsettings_items (idoforgsetting, createddate, lastupdate, settingtype, settingvalue, "version")
    VALUES (query.idoforgsetting, 1605610866000, 1605610866000, 11001, 1, 0);
  END LOOP;
  RETURN vers;
END;
';
SELECT fix_settingtype();

--271
ALTER TABLE public.cf_clientbalance_hold ALTER COLUMN idoftransaction DROP NOT NULL;

--! ФИНАЛИЗИРОВАН 17.11.2020, НЕ МЕНЯТЬ