/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 889

--Создаем отдельные сиквенсы для каждой записи из таблицы cf_do_versions и инициализируем сиквенсы значениями версий из этой таблицы

CREATE OR REPLACE FUNCTION create_do_versions_sequences() RETURNS INTEGER
LANGUAGE 'plpgsql' AS '
DECLARE
  cname varchar;
  query_str varchar;
  seq_name varchar;
BEGIN

  for cname in select distributedobjectclassname FROM cf_do_versions
  loop
    seq_name = ''DO_VERSION_'' || cname || ''_seq'';
	query_str = ''create sequence '' || seq_name;
	execute query_str;

	query_str = E''select setval(\'''' || seq_name || E''\'', (select coalesce(max(currentversion), 0) + 1 from cf_do_versions where upper(distributedobjectclassname) = \'''' || upper(cname) || E''\'')) '';
	execute query_str;
  end loop;

return 0;
END ';

select create_do_versions_sequences();