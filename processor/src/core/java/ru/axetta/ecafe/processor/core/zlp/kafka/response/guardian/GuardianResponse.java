package ru.axetta.ecafe.processor.core.zlp.kafka.response.guardian;

import ru.axetta.ecafe.processor.core.pull.model.AbstractPullData;

public class GuardianResponse extends AbstractPullData {
    private RelatednessChecking2Response relatedness_checking_2_response;

    public RelatednessChecking2Response getRelatedness_checking_2_response() {
        return relatedness_checking_2_response;
    }

    public void setRelatedness_checking_2_response(RelatednessChecking2Response relatedness_checking_2_response) {
        this.relatedness_checking_2_response = relatedness_checking_2_response;
    }
}
