-- Пакет обновлений issue proaktiv
ALTER TABLE cf_client_dtiszn_discount_info ADD COLUMN isActive boolean DEFAULT true;
COMMENT ON COLUMN cf_client_dtiszn_discount_info.isActive IS 'Признак активности';
