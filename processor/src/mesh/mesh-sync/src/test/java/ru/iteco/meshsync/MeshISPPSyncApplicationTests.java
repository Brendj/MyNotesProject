package ru.iteco.meshsync;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.iteco.client.api.PersonApi;
import ru.iteco.meshsync.config.RestMeshClientConfig;
import ru.iteco.meshsync.kafka.dto.EntityChangeEventDTO;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertNotNull;

@JsonTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RestMeshClientConfig.class)
class MeshISPPSyncApplicationTests {
    private static final Logger log = LoggerFactory.getLogger(MeshISPPSyncApplicationTests.class);
    private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PersonApi personApi;

    private final String MESSAGE =
            "{\n" +
            "  \"action\" : \"create\",\n" +
            "  \"entity\" : \"person\",\n" +
            "  \"timestamp\" : \"2020-02-28T07:59:36.320+0000\",\n" +
            "  \"id\" : \"person_6135077\",\n" +
            "  \"person_id\" : \"355a507a-95d7-49e6-ac8a-cf417d41d140\",\n" +
            "  \"merged_person_ids\" : [\"291d50e4-a828-4148-9960-fa240a3203ef\",\"94800a7c-5cc2-4ae6-b97b-3a1bdd7f6f66\"],\n" +
            "  \"entity_id\" : \"6135077\",\n" +
            "  \"updated_by\" : \"26f545ed-d97a-4579-81e5-1f2acefcb3ff\"\n" +
            "}";

    private final String personEntityId = "40314ac2-49fc-4532-8ca0-ba5e4baa7e7a";

    private final String expand = "education";

    @Test
    public void testDeserializesSimpleDTO() throws JsonProcessingException {
        EntityChangeEventDTO dto = objectMapper.readValue(MESSAGE, EntityChangeEventDTO.class);
        assertNotNull(dto.getEntity_id());
    }

    /*@Test void testGetPersonById() {
        try {
            log.info("Target URL: " + personApi.getApiClient().getBasePath());
            PersonInfo info = personApi.personsIdGet(personEntityId, OffsetDateTime.now(), expand);
            log.info(info.toString());
            assertNotNull(info.getId());
        } catch (ApiException e) {
            log.warn("Catch ApiException: ");
            log.warn("Code: " + e.getCode());
            log.warn("Header: " + e.getResponseHeaders());
            log.warn("Body: \n" + e.getResponseBody());
            fail();
        } catch (Exception e){
            log.error("", e);
            fail();
        }
    }

    @Test void testGetPersonByIdAndExpand() {
        try {
            log.info("Target URL: " + personApi.getApiClient().getBasePath());
            PersonInfo info = personApi.personsIdGet(personEntityId, null, expand);
            log.info(info.toString());
            assertNotNull(info.getId());
        } catch (ApiException e) {
            log.warn("Catch ApiException: ");
            log.warn("Code: " + e.getCode());
            log.warn("Header: " + e.getResponseHeaders());
            log.warn("Body: \n" + e.getResponseBody());
            fail();
        } catch (Exception e){
            log.error("", e);
            fail();
        }
    }*/
}
