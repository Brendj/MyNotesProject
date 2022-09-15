package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class AppMezhvedErrorSendKafka {
    private Long idofmezhvedkafkaerror;
    private String msg;
    private String topic;
    private String error;
    private ApplicationForFood applicationForFood;
    private Integer type;
    private Date createdate;
    private Date updatedate;

    public AppMezhvedErrorSendKafka() {

    }

    public AppMezhvedErrorSendKafka(String msg, String topic, Integer type, String error, ApplicationForFood applicationForFood) {
        this.msg = msg;
        this.topic = topic;
        this.type = type;
        this.error = error;
        this.applicationForFood = applicationForFood;
        this.createdate = new Date();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ApplicationForFood getApplicationForFood() {
        return applicationForFood;
    }

    public void setApplicationForFood(ApplicationForFood applicationForFood) {
        this.applicationForFood = applicationForFood;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public Date getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(Date updatedate) {
        this.updatedate = updatedate;
    }

    public Long getIdofmezhvedkafkaerror() {
        return idofmezhvedkafkaerror;
    }

    public void setIdofmezhvedkafkaerror(Long idofmezhvedkafkaerror) {
        this.idofmezhvedkafkaerror = idofmezhvedkafkaerror;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
