--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.96



CREATE TABLE cf_enterevents_y2015m05 (
  PRIMARY KEY (idofenterevent, idoforg),
  CHECK (evtdatetime >= 1430438400000 AND evtdatetime <1433116799000 )
) INHERITS (cf_enterevents);


--   DROP TABLE cf_enterevents_y2015m04


CREATE INDEX cf_enterevents_y2015m05_month1 ON cf_enterevents_y2015m05 (evtdatetime);

--   drop INDEX cf_enterevents_y2015m04_month1


CREATE OR REPLACE FUNCTION cf_enterevents_insert_trigger()
  RETURNS TRIGGER AS $$
BEGIN
 IF ( NEW.evtdatetime >= 1422748800000 AND NEW.evtdatetime < 1425167940000  ) THEN
 INSERT INTO cf_enterevents_y2015m02 VALUES (NEW.*) ;
  RETURN 1;

 ELSIF ( NEW.evtdatetime >= 1427849248000 AND NEW.evtdatetime <  1430437648000  ) THEN
 INSERT INTO cf_enterevents_y2015m04 VALUES (NEW.*);
   RETURN  1;

ELSIF ( NEW.evtdatetime >= 1430438400000 AND NEW.evtdatetime <  1433116799000  ) THEN
 INSERT INTO cf_enterevents_y2015m05 VALUES (NEW.*);
  RETURN  1;

ELSE
 RAISE EXCEPTION 'Date out of range.  Something wrong with the impressions_by_day_insert_trigger() function!';
 END IF;
 RETURN NULL;
END;
$$
LANGUAGE plpgsql;

--   DROP FUNCTION cf_enterevents_insert_trigger()



CREATE TRIGGER insert_cf_enterevents_trigger
BEFORE INSERT ON cf_enterevents
FOR EACH ROW EXECUTE PROCEDURE cf_enterevents_insert_trigger();

--   DROP TRIGGER insert_cf_enterevents_trigger ON cf_enterevents



SET constraint_exclusion = on;





-- 16:32:10,021 ERROR [ru.axetta.ecafe.processor.core.logic.Processor] (http--0.0.0.0-28443-8) Save enter event to database error: : org.hibernate.StaleStateException: Batch update returned unexpected row count from update [0]; actual row count: 0; expected: 1
-- at org.hibernate.jdbc.Expectations$BasicExpectation.checkBatched(Expectations.java:81) [hibernate-core-4.0.1.Final.jar:4.0.1.Final]
-- at org.hibernate.jdbc.Expectations$BasicExpectation.verifyOutcome(Expectations.java:73) [hibernate-core-4.0.1.Final.jar:4.0.1.Final]

-- http://www.technology-ebay.de/the-teams/mobile-de/blog/postgresql-table-partitioning-hibernate.html