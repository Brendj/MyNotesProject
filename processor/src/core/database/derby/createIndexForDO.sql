CREATE INDEX cf_publications_idofpublication_idx ON cf_publications USING btree (idofpublication );
CREATE INDEX cf_issuable_idofissuable_idx ON cf_issuable USING btree (idofissuable );
CREATE INDEX cf_instances_idofinstance_idx ON cf_instances USING btree (idofinstance );
CREATE INDEX cf_circulations_idofcirculation_idx ON cf_circulations USING btree (idofcirculation );