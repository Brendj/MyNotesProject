-- Пакет обновлений issue 676

ALTER TABLE cf_preorder_complex
    ADD COLUMN toPay integer;
COMMENT ON COLUMN cf_preorder_complex.topay
  IS 'Отметка "К оплате"';

alter table cf_orgs
    add column newСashiermode boolean default false;
COMMENT ON COLUMN cf_orgs.newСashiermode
  IS 'Новый режим выдачи (предзаказ)';
