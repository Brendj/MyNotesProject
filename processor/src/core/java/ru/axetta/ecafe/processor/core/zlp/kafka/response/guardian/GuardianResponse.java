package ru.axetta.ecafe.processor.core.zlp.kafka.response.guardian;

import ru.axetta.ecafe.processor.core.pull.model.AbstractPullData;

public class GuardianResponse extends AbstractPullData {
    private RelatednessCheckingResponse relatedness_checking_response;

    public RelatednessCheckingResponse getRelatedness_checking_response() {
        return relatedness_checking_response;
    }

    public void setRelatedness_checking_response(RelatednessCheckingResponse relatedness_checking_response) {
        this.relatedness_checking_response = relatedness_checking_response;
    }
}
