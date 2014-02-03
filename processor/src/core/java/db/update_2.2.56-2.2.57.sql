--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.57

-- Индекс для ссылки на cf_complexinfo
create index cf_complexinfodetail_complex_idx on cf_complexinfodetail(idofcomplexinfo);
-- Колонка, показывающая, ограничительная ли функция (добавлена в связи с ECAFE-1608)
ALTER TABLE cf_functions ADD COLUMN IsRestrict INTEGER NOT NULL DEFAULT 0;

--! ФИНАЛИЗИРОВАН (Кадыров, 140203) НЕ МЕНЯТЬ