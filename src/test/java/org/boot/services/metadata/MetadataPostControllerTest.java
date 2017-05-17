package org.boot.services.metadata;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MetadataPostControllerTest extends BaseControllerTest{

    @Autowired
    MetadataRepository repository;

    @Before
    public void setup(){
        repository.deleteAll();
    }


    @Test
    public void shouldSaveMetadataAsInsert() throws Exception {
        String payload = new MetadataBuilder().group("mygroup").name("myconfig").value("key1", "value1").value("key2", Arrays.asList("One","Two","Three")).buildAsJson();

        ResultActions result = mvc.perform(post("/metadata").content(payload).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id",notNullValue()))
                .andExpect(jsonPath("$.message",equalTo("Successfully saved metadata.")))
                .andDo(restDoc("insertMetadata"));

        ResultActions savedResult = mvc.perform(get("/metadata/mygroup/myconfig" ).accept(MediaType.APPLICATION_JSON));

        savedResult.andExpect(status().isOk())
                .andExpect(header().string("Cache-Control","max-age=3600, public"))
                .andExpect(jsonPath("$.group",equalTo("mygroup")))
                .andExpect(jsonPath("$.name",equalTo("myconfig")))
                .andExpect(jsonPath("$.value.key1",equalTo("value1")))
                .andExpect(jsonPath("$.value.key2",equalTo(Arrays.asList("One","Two","Three"))));
    }


    @Test
    public void shouldSaveMetadataAsUpdate() throws Exception {
        Metadata metadata = new MetadataBuilder().group("mygroup").name("myconfig").value("key1", "value1").value("key2", Arrays.asList("One", "Two", "Three")).build();
        metadata = repository.save(metadata);
        String payload = new MetadataBuilder().id(metadata.getId().toString()).group("mygroup").name("myconfig").value("key1", "newValue").buildAsJson();

        ResultActions result = mvc.perform(post("/metadata").content(payload).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id",notNullValue()))
                .andExpect(jsonPath("$.message",equalTo("Successfully saved metadata.")))
                .andDo(restDoc("updateMetadata"));

        ResultActions savedResult = mvc.perform(get("/metadata/mygroup/myconfig" ).accept(MediaType.APPLICATION_JSON));

        savedResult.andExpect(status().isOk())
                .andExpect(header().string("Cache-Control","max-age=3600, public"))
                .andExpect(jsonPath("$.group",equalTo("mygroup")))
                .andExpect(jsonPath("$.name",equalTo("myconfig")))
                .andExpect(jsonPath("$.value.key1",equalTo("newValue")));
    }

    @Test
    public void shouldThrowErrorWhenMetadataSavedWithoutGroup() throws Exception {
        String payload = new MetadataBuilder().name("myconfig").value("key1", "newValue").buildAsJson();

        ResultActions result = mvc.perform(post("/metadata").content(payload).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.message",equalTo("Validation failed in saving metadata. Group, Name and Value are mandatory fields.")))
                .andDo(restDoc("saveMetadataWithoutGroup"));
    }

    @Test
    public void shouldThrowErrorWhenMetadataSavedWithoutName() throws Exception {
        String payload = new MetadataBuilder().group("mygroup").value("key1", "newValue").buildAsJson();

        ResultActions result = mvc.perform(post("/metadata").content(payload).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.message",equalTo("Validation failed in saving metadata. Group, Name and Value are mandatory fields.")))
                .andDo(restDoc("saveMetadataWithoutName"));
    }

    @Test
    public void shouldThrowErrorWhenMetadataSavedWithoutValue() throws Exception {
        String payload = new MetadataBuilder().group("mygroup").name("myconfig").buildAsJson();

        ResultActions result = mvc.perform(post("/metadata").content(payload).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.message",equalTo("Validation failed in saving metadata. Group, Name and Value are mandatory fields.")))
                .andDo(restDoc("saveMetadataWithoutValue"));
    }


    @Test
    public void shouldPatchMetadataWithValue() throws Exception {
        Metadata metadata = new MetadataBuilder().group("mygroup").name("myconfig").value("key1", "value1").value("key2", Arrays.asList("One", "Two", "Three")).build();
        String id = repository.save(metadata).getId().toString();

        String payload = "{ \"value\" : \"updated-test\" } ";
        ResultActions result = mvc.perform(post("/metadata/" + id +  "/patch")
                .content(payload).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id",equalTo(id)))
                .andExpect(jsonPath("$.message",equalTo("Successfully patched metadata.")));

        result = mvc.perform(get("/metadata/" + id).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(id)))
                .andExpect(jsonPath("$.value", equalTo("updated-test")))
                .andDo(restDoc("patchMetadataValue"))
        ;
    }

}