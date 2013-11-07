package ru.axetta.ecafe.processor.web.partner.acquiropay.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 25.10.13
 * Time: 13:53
 */

@XmlRootElement(name = "ParametersList")
@XmlAccessorType(XmlAccessType.FIELD)
public class ParametersList {

    @XmlElement(name = "Parameter")
    private List<Parameter> list;

    public List<Parameter> getList() {
        return list;
    }

    public void setList(List<Parameter> list) {
        this.list = list;
    }

    @XmlRootElement(name = "Parameter")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Parameter {

        @XmlElement(name = "name")
        private String name;
        @XmlElement(name = "value")
        private String value;

        public Parameter() {
        }

        public Parameter(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
