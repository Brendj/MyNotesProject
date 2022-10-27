-- Пакет обновлений v 292

CREATE INDEX cf_wt_complexes_org_idofcomplex_idx
  ON cf_wt_complexes_org
  USING btree
  (idofcomplex);

CREATE INDEX cf_wt_complexes_idOfAgeGroupItem_idx
  ON cf_wt_complexes
  USING btree
  (idOfAgeGroupItem) where deleteState=0;

--! ФИНАЛИЗИРОВАН 25.08.2022, НЕ МЕНЯТЬ