package ru.axetta.ecafe.processor.core.partner.mesh;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.json.And;
import ru.axetta.ecafe.processor.core.partner.mesh.json.MeshJsonFilter;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class MeshRestClient {
    public static final String MESH_REST_ADDRESS_PROPERTY = "ecafe.processing.mesh.rest.address";
    public static final String MESH_REST_PERSONS_URL = "/persons?";
    public static final String MESH_REST_PERSONS_EXPAND = "education,categories";

    private static final String FILTER_VALUE_ORG = "education.organization_id";
    private static final String FILTER_VALUE_EQUALS = "equal";
    private static final String FILTER_VALUE_LASTNAME = "lastname";
    private static final String FILTER_VALUE_FIRSTNAME = "firstname";
    private static final String FILTER_VALUE_PATRONYMIC = "patronymic";

    public void loadPersons(long idOfOrg, String fio) throws Exception {
        String parameters = String.format("filter=%s&expand=%s", getFilter(idOfOrg, fio), getExpand());
        URL url = new URL(getServiceAddress() + MESH_REST_PERSONS_URL + parameters);
    }

    private String getServiceAddress() throws Exception{
        String address = RuntimeContext.getInstance().getConfigProperties().getProperty(MESH_REST_ADDRESS_PROPERTY, "");
        if (address.equals("")) throw new Exception("MESH REST address not specified");
        return address;
    }

    private String getExpand() {
        return MESH_REST_PERSONS_EXPAND;
    }

    private String getFilter(long idOfOrg, String fio) throws Exception {
        Long meshId = DAOService.getInstance().getMeshIdByOrg(idOfOrg);
        if (meshId == null) throw new Exception("У организации не указан МЭШ ид.");
        MeshJsonFilter filter = new MeshJsonFilter();
        List<And> list = new ArrayList<>();
        And andOrg = new And();
        andOrg.setField(FILTER_VALUE_ORG);
        andOrg.setOp(FILTER_VALUE_EQUALS);
        andOrg.setValue(meshId.toString());
        list.add(andOrg);
        List<And> fioFilterList = getFIOFilter(fio);
        if (fioFilterList != null) list.addAll(fioFilterList);
        filter.setAnd(list);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(filter);
    }

    private List<And> getFIOFilter(String fio) {
        if (StringUtils.isEmpty(fio)) return null;
        String[] arr = fio.split(" ");
        if (arr.length == 0) return null;
        List<And> list = new ArrayList<>();
        And andLastname = new And();
        andLastname.setField(FILTER_VALUE_LASTNAME);
        andLastname.setOp(FILTER_VALUE_EQUALS);
        andLastname.setValue(arr[0].trim());
        list.add(andLastname);
        if (arr.length < 2) return list;
        And andFirstname = new And();
        andFirstname.setField(FILTER_VALUE_FIRSTNAME);
        andFirstname.setOp(FILTER_VALUE_EQUALS);
        andFirstname.setValue(arr[1].trim());
        list.add(andFirstname);
        if (arr.length < 3) return list;
        And andPatronymic = new And();
        andPatronymic.setField(FILTER_VALUE_PATRONYMIC);
        andPatronymic.setOp(FILTER_VALUE_EQUALS);
        andPatronymic.setValue(arr[1].trim());
        list.add(andPatronymic);
        return list;
    }
}