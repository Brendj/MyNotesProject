-- Пакет обновлений issue 965

CREATE INDEX cf_migrants_idoforgregistry_idx ON cf_migrants USING btree (idoforgregistry);

CREATE INDEX cf_visitreqresolutionhist_idoforgresol_idx ON cf_visitreqresolutionhist USING btree (idoforgresol);