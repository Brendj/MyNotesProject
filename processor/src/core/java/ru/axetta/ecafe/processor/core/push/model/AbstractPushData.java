package ru.axetta.ecafe.processor.core.push.model;


public abstract class AbstractPushData {
    public static final String BALANCE_TOPIC = "ecafe.processing.mesh.kafka.topic.balance";
    public static final String ENTRANCE_TOPIC = "ecafe.processing.mesh.kafka.topic.entrance";
    public static final String BENEFIT_TOPIC = "ecafe.processing.mesh.kafka.topic.benefit";

    public static final String GUARDIANSHIP_VALIDATION_REQUEST_TOPIC = "ecafe.processing.zlp.kafka.topic.guardianship_validation.request";
    public static final String BENEFIT_VALIDATION_REQUEST_TOPIC = "ecafe.processing.zlp.kafka.topic.benefit_validation.request";
    public static final String DOC_VALIDATION_REQUEST_TOPIC = "ecafe.processing.zlp.kafka.topic.doc_validation.request";
}
