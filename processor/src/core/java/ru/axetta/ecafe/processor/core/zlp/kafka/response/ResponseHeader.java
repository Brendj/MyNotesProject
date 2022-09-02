package ru.axetta.ecafe.processor.core.zlp.kafka.response;

import ru.axetta.ecafe.processor.core.pull.model.AbstractPullData;
import ru.axetta.ecafe.processor.core.zlp.kafka.BenefitKafkaService;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.benefit.ActiveBenefitCategoriesGettingResponse;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.guardian.RelatednessChecking2Response;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.passport.PassportBySerieNumberValidityCheckingResponse;

public class ResponseHeader {
    protected String request_id;
    protected String requesting_system;
    protected String requested_method;
    protected String state_service_variety_code;

    public ResponseHeader() {

    }

    private String getReqsponseMethod() {
        if (this instanceof ActiveBenefitCategoriesGettingResponse) return BenefitKafkaService.RESPONSE_METHOD_BENEFIT;
        if (this instanceof RelatednessChecking2Response) return BenefitKafkaService.RESPONSE_METHOD_GUARDIANSHIP;
        if (this instanceof PassportBySerieNumberValidityCheckingResponse) return BenefitKafkaService.RESPONSE_METHOD_DOC;
        return "";
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getRequesting_system() {
        return requesting_system;
    }

    public void setRequesting_system(String requesting_system) {
        this.requesting_system = requesting_system;
    }

    public String getRequested_method() {
        return requested_method;
    }

    public void setRequested_method(String requested_method) {
        this.requested_method = requested_method;
    }

    public String getState_service_variety_code() {
        return state_service_variety_code;
    }

    public void setState_service_variety_code(String state_service_variety_code) {
        this.state_service_variety_code = state_service_variety_code;
    }
}
