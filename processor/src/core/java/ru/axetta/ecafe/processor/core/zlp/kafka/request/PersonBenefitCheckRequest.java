package ru.axetta.ecafe.processor.core.zlp.kafka.request;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ApplicationForFood;
import ru.axetta.ecafe.processor.core.persistence.ApplicationForFoodDiscount;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.push.model.AbstractPushData;
import ru.axetta.ecafe.processor.core.zlp.kafka.BenefitKafkaService;

import java.util.UUID;

public class PersonBenefitCheckRequest extends AbstractPushData {
    private String request_id;
    private String person_id;
    private String state_service_variety_code;
    private Integer[] benefit_category_codes;

    public PersonBenefitCheckRequest() {

    }

    public PersonBenefitCheckRequest(ApplicationForFood applicationForFood) {
        Client client = DAOReadonlyService.getInstance().findClientById(applicationForFood.getClient().getIdOfClient());
        this.person_id = client.getMeshGUID();
        this.request_id = UUID.randomUUID().toString();
        this.state_service_variety_code = RuntimeContext.getInstance()
                .getPropertiesValue(BenefitKafkaService.STATE_SERVICE_VARIETY_CODE_PROPERTY, BenefitKafkaService.STATE_SERVICE_VARIETY_CODE_DEFAULT);
        this.benefit_category_codes = new Integer[applicationForFood.getDtisznCodes().size()];
        int i = 0;
        for (ApplicationForFoodDiscount discount : applicationForFood.getDtisznCodes()) {
            this.benefit_category_codes[i] = discount.getDtisznCode();
            i++;
        }
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getState_service_variety_code() {
        return state_service_variety_code;
    }

    public void setState_service_variety_code(String state_service_variety_code) {
        this.state_service_variety_code = state_service_variety_code;
    }

    public Integer[] getBenefit_category_codes() {
        return benefit_category_codes;
    }

    public void setBenefit_category_codes(Integer[] benefit_category_codes) {
        this.benefit_category_codes = benefit_category_codes;
    }
}
