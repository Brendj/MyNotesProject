package ru.iteco.nsisync;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.iteco.nsisync.nsi.dto.CatalogInfoDTO;

import static org.junit.Assert.assertNotNull;

@JsonTest
@RunWith(SpringRunner.class)
class NsiISPPSyncApplicationTests {
    private static final Logger log = LoggerFactory.getLogger(NsiISPPSyncApplicationTests.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String EDUCATION_FORM_JSON =
                    "{\n" +
                    "    \"id\": 750683,\n" +
                    "    \"type\": \"OBJECT\",\n" +
                    "    \"catalogId\": 28,\n" +
                    "    \"name\": \"Форма обучения\",\n" +
                    "    \"technical_name\": \"EDUCATION_FORM\",\n" +
                    "    \"created\": null,\n" +
                    "    \"data\": {\n" +
                    "      \"catalog\": {\n" +
                    "        \"name\": \"Уровень образования\",\n" +
                    "        \"id\": 114,\n" +
                    "        \"technical_name\": \"EDUCATION_STAGE\",\n" +
                    "        \"data\": {\n" +
                    "          \"attribute\": [\n" +
                    "            {\n" +
                    "              \"values\": {\n" +
                    "                \"value\": [\n" +
                    "                  {\n" +
                    "                    \"value\": \"31663023\",\n" +
                    "                    \"id\": null\n" +
                    "                  }\n" +
                    "                ],\n" +
                    "                \"groupvalue\": null\n" +
                    "              },\n" +
                    "              \"name\": \"global_id\",\n" +
                    "              \"fieldId\": -1,\n" +
                    "              \"type\": \"INT\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"values\": {\n" +
                    "                \"value\": [\n" +
                    "                  {\n" +
                    "                    \"value\": \"8\",\n" +
                    "                    \"id\": null\n" +
                    "                  }\n" +
                    "                ],\n" +
                    "                \"groupvalue\": null\n" +
                    "              },\n" +
                    "              \"name\": \"system_object_id\",\n" +
                    "              \"fieldId\": -2,\n" +
                    "              \"type\": \"STRING\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"values\": {\n" +
                    "                \"value\": [\n" +
                    "                  {\n" +
                    "                    \"value\": \"\",\n" +
                    "                    \"id\": null\n" +
                    "                  }\n" +
                    "                ],\n" +
                    "                \"groupvalue\": null\n" +
                    "              },\n" +
                    "              \"name\": \"global_object_id\",\n" +
                    "              \"fieldId\": -3,\n" +
                    "              \"type\": \"INT\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"values\": {\n" +
                    "                \"value\": [\n" +
                    "                  {\n" +
                    "                    \"value\": \"8\",\n" +
                    "                    \"id\": null\n" +
                    "                  }\n" +
                    "                ],\n" +
                    "                \"groupvalue\": null\n" +
                    "              },\n" +
                    "              \"name\": \"id\",\n" +
                    "              \"fieldId\": 5239,\n" +
                    "              \"type\": \"INT\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"values\": {\n" +
                    "                \"value\": [\n" +
                    "                  {\n" +
                    "                    \"value\": \"Высшее образование - бакалавриат\",\n" +
                    "                    \"id\": null\n" +
                    "                  }\n" +
                    "                ],\n" +
                    "                \"groupvalue\": null\n" +
                    "              },\n" +
                    "              \"name\": \"name\",\n" +
                    "              \"fieldId\": 4534,\n" +
                    "              \"type\": \"STRING\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"values\": {\n" +
                    "                \"value\": [\n" +
                    "                  {\n" +
                    "                    \"value\": \"1\",\n" +
                    "                    \"id\": null\n" +
                    "                  }\n" +
                    "                ],\n" +
                    "                \"groupvalue\": null\n" +
                    "              },\n" +
                    "              \"name\": \"signature\",\n" +
                    "              \"fieldId\": -4,\n" +
                    "              \"type\": \"STRING\"\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"action\": \"MODIFIED\"\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"id\": \"750683\"\n" +
                    "    }\n" +
                    "  }";

    private final String ORG_REG_JSON =
            "{\n" +
            "    \"catalogID\": 22,\n" +
            "    \"created\": [2020,2,27,13,19,22,430000000],\n" +
            "    \"data\": {\n" +
            "        \"catalog\": {\n" +
            "            \"data\": {\n" +
            "                \"action\": \"MODIFIED\",\n" +
            "                \"attribute\": [\n" +
            "                    {\n" +
            "                        \"fieldId\": 176,\n" +
            "                        \"name\": \"status_id\",\n" +
            "                        \"type\": \"DICTIONARY\",\n" +
            "                        \"values\": {\n" +
            "                            \"groupvalue\": null,\n" +
            "                            \"value\": [\n" +
            "                                {\n" +
            "                                    \"id\": \"1\",\n" +
            "                                    \"value\": \"Действует\"\n" +
            "                                }\n" +
            "                            ]\n" +
            "                        }\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"fieldId\": 216,\n" +
            "                        \"name\": \"egisso_id\",\n" +
            "                        \"type\": \"STRING\",\n" +
            "                        \"values\": {\n" +
            "                            \"groupvalue\": null,\n" +
            "                            \"value\": [\n" +
            "                                {\n" +
            "                                    \"id\": null,\n" +
            "                                    \"value\": \"\"\n" +
            "                                }\n" +
            "                            ]\n" +
            "                        }\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"fieldId\": 5322,\n" +
            "                        \"name\": \"ou_table_873\",\n" +
            "                        \"type\": \"TABLE\",\n" +
            "                        \"values\": {\n" +
            "                            \"groupvalue\": {\n" +
            "                                \"item\": [\n" +
            "                                    {\n" +
            "                                        \"attribute\": [\n" +
            "                                            {\n" +
            "                                                \"fieldId\": -1,\n" +
            "                                                \"name\": \"global_id\",\n" +
            "                                                \"type\": \"INT\",\n" +
            "                                                \"values\": {\n" +
            "                                                    \"groupvalue\": null,\n" +
            "                                                    \"value\": [\n" +
            "                                                        {\n" +
            "                                                            \"id\": null,\n" +
            "                                                            \"value\": \"1408\"\n" +
            "                                                        }\n" +
            "                                                    ]\n" +
            "                                                }\n" +
            "                                            },\n" +
            "                                            {\n" +
            "                                                \"fieldId\": -2,\n" +
            "                                                \"name\": \"system_object_id\",\n" +
            "                                                \"type\": \"STRING\",\n" +
            "                                                \"values\": {\n" +
            "                                                    \"groupvalue\": null,\n" +
            "                                                    \"value\": [\n" +
            "                                                        {\n" +
            "                                                            \"id\": null,\n" +
            "                                                            \"value\": \"4\"\n" +
            "                                                        }\n" +
            "                                                    ]\n" +
            "                                                }\n" +
            "                                            },\n" +
            "                                            {\n" +
            "                                                \"fieldId\": -3,\n" +
            "                                                \"name\": \"global_object_id\",\n" +
            "                                                \"type\": \"INT\",\n" +
            "                                                \"values\": {\n" +
            "                                                    \"groupvalue\": null,\n" +
            "                                                    \"value\": [\n" +
            "                                                        {\n" +
            "                                                            \"id\": null,\n" +
            "                                                            \"value\": \"79\"\n" +
            "                                                        }\n" +
            "                                                    ]\n" +
            "                                                }\n" +
            "                                            },\n" +
            "                                            {\n" +
            "                                                \"fieldId\": 5324,\n" +
            "                                                \"name\": \"a_17151\",\n" +
            "                                                \"type\": \"STRING\",\n" +
            "                                                \"values\": {\n" +
            "                                                    \"groupvalue\": null,\n" +
            "                                                    \"value\": [\n" +
            "                                                        {\n" +
            "                                                            \"id\": null,\n" +
            "                                                            \"value\": \"5 - 2018\"\n" +
            "                                                        }\n" +
            "                                                    ]\n" +
            "                                                }\n" +
            "                                            }\n" +
            "                                        ]\n" +
            "                                    },\n" +
            "                                    {\n" +
            "                                        \"attribute\": [\n" +
            "                                            {\n" +
            "                                                \"fieldId\": -1,\n" +
            "                                                \"name\": \"global_id\",\n" +
            "                                                \"type\": \"INT\",\n" +
            "                                                \"values\": {\n" +
            "                                                    \"groupvalue\": null,\n" +
            "                                                    \"value\": [\n" +
            "                                                        {\n" +
            "                                                            \"id\": null,\n" +
            "                                                            \"value\": \"1407\"\n" +
            "                                                        }\n" +
            "                                                    ]\n" +
            "                                                }\n" +
            "                                            },\n" +
            "                                            {\n" +
            "                                                \"fieldId\": -2,\n" +
            "                                                \"name\": \"system_object_id\",\n" +
            "                                                \"type\": \"STRING\",\n" +
            "                                                \"values\": {\n" +
            "                                                    \"groupvalue\": null,\n" +
            "                                                    \"value\": [\n" +
            "                                                        {\n" +
            "                                                            \"id\": null,\n" +
            "                                                            \"value\": \"2\"\n" +
            "                                                        }\n" +
            "                                                    ]\n" +
            "                                                }\n" +
            "                                            },\n" +
            "                                            {\n" +
            "                                                \"fieldId\": -3,\n" +
            "                                                \"name\": \"global_object_id\",\n" +
            "                                                \"type\": \"INT\",\n" +
            "                                                \"values\": {\n" +
            "                                                    \"groupvalue\": null,\n" +
            "                                                    \"value\": [\n" +
            "                                                        {\n" +
            "                                                            \"id\": null,\n" +
            "                                                            \"value\": \"79\"\n" +
            "                                                        }\n" +
            "                                                    ]\n" +
            "                                                }\n" +
            "                                            },\n" +
            "                                            {\n" +
            "                                                \"fieldId\": 5324,\n" +
            "                                                \"name\": \"a_17151\",\n" +
            "                                                \"type\": \"STRING\",\n" +
            "                                                \"values\": {\n" +
            "                                                    \"groupvalue\": null,\n" +
            "                                                    \"value\": [\n" +
            "                                                        {\n" +
            "                                                            \"id\": null,\n" +
            "                                                            \"value\": \"1 - 2013\"\n" +
            "                                                        }\n" +
            "                                                    ]\n" +
            "                                                }\n" +
            "                                            }\n" +
            "                                        ]\n" +
            "                                    }\n" +
            "                                ]\n" +
            "                            },\n" +
            "                            \"value\": null\n" +
            "                        }\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"fieldId\": 5323,\n" +
            "                        \"name\": \"ou_table_883\",\n" +
            "                        \"type\": \"TABLE\",\n" +
            "                        \"values\": {\n" +
            "                            \"groupvalue\": {\n" +
            "                                \"item\": null\n" +
            "                            },\n" +
            "                            \"value\": null\n" +
            "                        }\n" +
            "                    }\n" +
            "                ]\n" +
            "            },\n" +
            "            \"id\": 22,\n" +
            "            \"name\": \"Реестр Образовательных Организаций\",\n" +
            "            \"technical_name\": \"ORGANIZATION_REGISTRY\"\n" +
            "        },\n" +
            "        \"id\": \"750837\"\n" +
            "    },\n" +
            "    \"ID\": 750837,\n" +
            "    \"name\": \"Реестр Образовательных Организаций\",\n" +
            "    \"technical_name\": \"ORGANIZATION_REGISTRY\",\n" +
            "    \"type\": \"OBJECT\"\n" +
            "}";

    @Test
    public void testDeserializesSimpleDTO() throws JsonProcessingException {
        CatalogInfoDTO dto = objectMapper.readValue(EDUCATION_FORM_JSON, CatalogInfoDTO.class);
        assertNotNull(dto.getCatalogId());
    }

    @Test
    public void testDeserializesDTOWithGroupValue() throws JsonProcessingException {
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        CatalogInfoDTO dto = objectMapper.readValue(ORG_REG_JSON, CatalogInfoDTO.class);
        assertNotNull(dto.getCatalogId());
    }

    @Test
    public void testDeserializesAndSerializesJSON() throws JsonProcessingException {
        CatalogInfoDTO dto1 = objectMapper.readValue(EDUCATION_FORM_JSON, CatalogInfoDTO.class);
        CatalogInfoDTO dto2 = objectMapper.readValue(ORG_REG_JSON, CatalogInfoDTO.class);
        String s1 = writeValueAsString(dto1);
        String s2 = writeValueAsString(dto2);
        log.info(s1);
        log.info(s2);
        assertNotNull(s1);
        assertNotNull(s2);
    }

    private String writeValueAsString(CatalogInfoDTO message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
