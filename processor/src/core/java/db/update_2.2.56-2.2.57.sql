
-- Индекс для ссылки на cf_complexinfo
create index cf_complexinfodetail_complex_idx on cf_complexinfodetail(idofcomplexinfo);
-- Колонка, показывающая, ограничительная ли функция (добавлена в связи с ECAFE-1608)
ALTER TABLE cf_functions ADD COLUMN IsRestrict INTEGER NOT NULL DEFAULT 0;