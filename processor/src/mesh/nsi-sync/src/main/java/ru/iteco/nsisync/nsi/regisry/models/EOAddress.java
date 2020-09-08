package ru.iteco.nsisync.nsi.regisry.models;

import ru.iteco.nsisync.nsi.utils.JsonFieldDescriptor;

import javax.persistence.*;

@Entity
@Table(name = "cf_kf_eo_address")
public class EOAddress extends AbstractRegistry {

    @Column(name = "eo_id")
    private Long eoId;

    @Column(name = "unique_address_id")
    private Long uniqueAddressId;

    @Column(name = "unom")
    private Long unom;

    @Column(name = "unad")
    private Long unad;

    @Column(name = "address", length = 512)
    private String address;

    @Column(name = "address_asur", length = 512)
    private String addressAsur;

    @Column(name = "area", length = 100)
    private String area;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "is_bti")
    private Boolean isBti;

    @Column(name = "address_description", length = 1024)
    private String addressDescription;

    @Column(name = "queue_from_BTI", length = 128)
    private String queueFromBTI;

    @Column(name = "queue_not_from_BTI", length = 128)
    private String queueNotFromBTI;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "global_object_id")
    private OrganizationRegistry organizationRegistry;

    public EOAddress(Long globalId, String systemObjectId, Long eoId, Long uniqueAddressId, Long unom, Long unad,
                     String address, String addressAsur, String area, String district, Boolean isBti,
                     String addressDescription, OrganizationRegistry organizationRegistry, String queueFromBTI,
                     String queueNotFromBTI) {
        this.globalId = globalId;
        this.systemObjectId = systemObjectId;
        this.eoId = eoId;
        this.uniqueAddressId = uniqueAddressId;
        this.unom = unom;
        this.unad = unad;
        this.address = address;
        this.addressAsur = addressAsur;
        this.area = area;
        this.district = district;
        this.isBti = isBti;
        this.addressDescription = addressDescription;
        this.organizationRegistry = organizationRegistry;
        this.queueFromBTI = queueFromBTI;
        this.queueNotFromBTI = queueNotFromBTI;
    }

    public EOAddress() {
    }

    public Long getEoId() {
        return eoId;
    }

    public void setEoId(Long eoId) {
        this.eoId = eoId;
    }

    public Long getUniqueAddressId() {
        return uniqueAddressId;
    }

    public void setUniqueAddressId(Long uniqueAddressId) {
        this.uniqueAddressId = uniqueAddressId;
    }

    public Long getUnom() {
        return unom;
    }

    public void setUnom(Long unom) {
        this.unom = unom;
    }

    public Long getUnad() {
        return unad;
    }

    public void setUnad(Long unad) {
        this.unad = unad;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressAsur() {
        return addressAsur;
    }

    public void setAddressAsur(String addressAsur) {
        this.addressAsur = addressAsur;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Boolean getIsBti() {
        return isBti;
    }

    public void setIsBti(Boolean bti) {
        isBti = bti;
    }

    public String getAddressDescription() {
        return addressDescription;
    }

    public void setAddressDescription(String addressDescription) {
        this.addressDescription = addressDescription;
    }

    public String getQueueFromBTI() {
        return queueFromBTI;
    }

    public void setQueueFromBTI(String queueFromBTI) {
        this.queueFromBTI = queueFromBTI;
    }

    public String getQueueNotFromBTI() {
        return queueNotFromBTI;
    }

    public void setQueueNotFromBTI(String queueNotFromBTI) {
        this.queueNotFromBTI = queueNotFromBTI;
    }

    public OrganizationRegistry getOrganizationRegistry() {
        return organizationRegistry;
    }

    public void setOrganizationRegistry(OrganizationRegistry organizationRegistry) {
        this.organizationRegistry = organizationRegistry;
    }



    public enum EOAddressEnumJsonFields implements JsonFieldDescriptor {
        EO_ID("eo_id", "ЕКИС ID"),
        UNIQUE_ADDRESS_ID("unique_address_id", "Уникальный номер здания в ЕКИС"),
        UNOM("unom", "УНОМ"),
        UNAD("unad", "УНАД"),
        ADDRESS("address", "Адрес"),
        ADDRESS_ASUR("address_asur", "Краткий адрес"),
        AREA("area", "Административный округ"),
        DISTRICT("district", "Муниципальный округ"),
        IS_BTI("is_bti", "Является БТИ"),
        ADDRESS_DESCRIPTION("address_description", "Описание адреса"),
        DATA_IN_BTI("add_list_1", "Вложенная структура для зданий (БТИ)"),
        DATA_NOT_IN_BTI("add_list_2", "Вложенная структура для зданий, которых нет в справочнике БТИ (Ручной ввод)"),
        QUEUE_VALUE_IN_BTI("imp_order_1", "Очередь внедрения в DATA_IN_BTI"),
        QUEUE_VALUE_NOT_IN_BTI("imp_order_2", "Очередь внедрения в DATA_NOT_IN_BTI");

        EOAddressEnumJsonFields(String jsonFieldName, String description){
            this.jsonFieldName = jsonFieldName;
            this.description = description;
        }

        private String jsonFieldName;
        private String description;

        @Override
        public String getJsonFieldName() {
            return jsonFieldName;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }
}
