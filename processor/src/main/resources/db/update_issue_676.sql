-- Пакет обновлений issue 676

ALTER TABLE cf_preorder_complex
    ADD COLUMN toPay integer;
COMMENT ON COLUMN cf_preorder_complex.topay
  IS 'Отметка "К оплате"';

ALTER TABLE cf_preorder_menudetail
    ADD COLUMN toPay integer,
    ADD COLUMN amountToPay integer;
COMMENT ON COLUMN cf_preorder_menudetail.topay IS 'Отметка "К оплате"';
COMMENT ON COLUMN cf_preorder_menudetail.amounttopay IS 'Количество "К оплате"';

alter table cf_orgs
    add column newСashiermode boolean default false;
COMMENT ON COLUMN cf_orgs.newСashiermode
  IS 'Новый режим выдачи (предзаказ)';
