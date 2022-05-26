-- Пакет обновлений issue 907

CREATE SEQUENCE cf_preorder_complex_version_seq;
select setval('cf_preorder_complex_version_seq', (select coalesce(max(version), 0) + 1 from cf_preorder_complex));
