-- Пакет обновлений 258

CREATE INDEX cf_migrants_idoforgregistry_idx ON cf_migrants USING btree (idoforgregistry);

CREATE INDEX cf_visitreqresolutionhist_idoforgresol_idx ON cf_visitreqresolutionhist USING btree (idoforgresol);

CREATE INDEX cf_card_sync_idoforg_idx ON cf_card_sync USING btree (idoforg);

--! ФИНАЛИЗИРОВАН 01.04.2021, НЕ МЕНЯТЬ