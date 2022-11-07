package ru.axetta.ecafe.processor.core.zlp.kafka.response.benefit;

public class BenefitCategory {
    private String id;
    private String name;
    private String start_at; //"1997-03-04T00:00"
    private String end_at;

    public BenefitCategory() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart_at() {
        return start_at;
    }

    public void setStart_at(String start_at) {
        this.start_at = start_at;
    }

    public String getEnd_at() {
        return end_at;
    }

    public void setEnd_at(String end_at) {
        this.end_at = end_at;
    }
}
