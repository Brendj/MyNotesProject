package ru.axetta.ecafe.processor.core.zlp.kafka.request;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.push.model.AbstractPushData;
import ru.axetta.ecafe.processor.core.zlp.kafka.BenefitKafkaService;
import ru.axetta.ecafe.processor.core.zlp.kafka.RequestValidationData;

import java.util.UUID;

public class RequestHeader extends AbstractPushData {
    protected String request_id;
    protected String requesting_system;
    protected String requested_method;
    protected String state_service_variety_code;

    public RequestHeader() {

    }

    public RequestHeader(RequestValidationData data) {
        this.request_id = UUID.randomUUID().toString();
        this.requesting_system = RuntimeContext.getInstance()
                .getPropertiesValue(BenefitKafkaService.REQUEST_SYSTEM_PROPERTY, BenefitKafkaService.REQUEST_SYSTEM_DEFAULT);
        this.requested_method = getRequestMethod();
        this.state_service_variety_code = RuntimeContext.getInstance()
                .getPropertiesValue(BenefitKafkaService.STATE_SERVICE_VARIETY_CODE_PROPERTY, BenefitKafkaService.STATE_SERVICE_VARIETY_CODE_DEFAULT);
    }

    private String getRequestMethod() {
        if (this instanceof ActiveBenefitCategoriesGettingRequest) return BenefitKafkaService.REQUEST_METHOD_BENEFIT;
        if (this instanceof RelatednessChecking2Request) return BenefitKafkaService.REQUEST_METHOD_GUARDIANSHIP;
        if (this instanceof PassportBySerieNumberValidityCheckingRequest) return BenefitKafkaService.REQUEST_METHOD_DOC;
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
