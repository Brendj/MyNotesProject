package ru.axetta.ecafe.processor.core.zlp.kafka.request;

import org.codehaus.jackson.map.ObjectMapper;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.push.model.AbstractPushData;
import ru.axetta.ecafe.processor.core.zlp.kafka.BenefitKafkaService;
import ru.axetta.ecafe.processor.core.zlp.kafka.GuardianshipValidationData;

import java.util.UUID;

public class GuardianshipValidationRequest extends AbstractPushData {
    private RelatednessChecking2Request relatedness_checking_2_request;

    public GuardianshipValidationRequest() {

    }

    public GuardianshipValidationRequest(GuardianshipValidationData data) {
        RelatednessChecking2Request request = new RelatednessChecking2Request();
        request.setRequest_id(UUID.randomUUID().toString());
        request.setRequesting_system(BenefitKafkaService.REQUEST_SYSTEM);
        request.setRequested_method(BenefitKafkaService.REQUEST_METHOD);
        request.setState_service_variety_code(RuntimeContext.getInstance()
                .getPropertiesValue(BenefitKafkaService.STATE_SERVICE_VARIETY_CODE_PROPERTY, BenefitKafkaService.STATE_SERVICE_VARIETY_CODE_DEFAULT));
        request.setLearnerInfo(new LearnerInfo(data));
        request.setLearner_document_info(new LearnerDocumentInfo(data));
        request.setParent_info(new ParentInfo(data));
        request.setParent_passport_info(new ParentPassportInfo(data));

        this.relatedness_checking_2_request = request;
    }

    public RelatednessChecking2Request getRelatedness_checking_2_request() {
        return relatedness_checking_2_request;
    }

    public void setRelatedness_checking_2_request(RelatednessChecking2Request relatedness_checking_2_request) {
        this.relatedness_checking_2_request = relatedness_checking_2_request;
    }
}
