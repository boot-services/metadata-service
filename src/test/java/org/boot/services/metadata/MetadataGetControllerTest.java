package org.boot.services.metadata;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MetadataGetControllerTest extends BaseControllerTest{

    @Autowired
    MetadataRepository repository;

    @Before
    public void setup(){
        repository.deleteAll();
    }


    @Test
    public void shouldReturnRequestedConfigForAGroup() throws Exception {
        Metadata metadata = new MetadataBuilder().group("mygroup").name("myconfig")
                .value("key1", "value1").value("key2", Arrays.asList("One","Two","Three")).tags("tag1").build();
        String lastUpdatedTs = metadata.getLastUpdatedTs().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        metadata = repository.save(metadata);
        repository.save(new MetadataBuilder().group("mygroup").name("myconfig2").value(100).build());

        ResultActions result = mvc.perform(get("/metadata/mygroup/myconfig" ).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(header().string("Cache-Control","max-age=3600, public"))
                .andExpect(jsonPath("$.id",equalTo(metadata.getId().toString())))
                .andExpect(jsonPath("$.group",equalTo("mygroup")))
                .andExpect(jsonPath("$.tags[0]",equalTo("tag1")))
                .andExpect(jsonPath("$.name",equalTo("myconfig")))
                .andExpect(jsonPath("$.value.key1",equalTo("value1")))
                .andExpect(jsonPath("$.value.key2",equalTo(Arrays.asList("One","Two","Three"))))
                .andDo(restDoc("getMetadataByGroup"));

    }

    @Test
    public void shouldReturnConfigForId() throws Exception {
        Metadata metadata = new MetadataBuilder().group("mygroup").name("myconfig")
                .value("key1", "value1").value("key2", Arrays.asList("One","Two","Three")).build();
        metadata = repository.save(metadata);
        repository.save(new MetadataBuilder().group("mygroup").name("myconfig2").value(100).build());

        ResultActions result = mvc.perform(get("/metadata/" + metadata.getId() ).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(header().string("Cache-Control","max-age=3600, public"))
                .andExpect(jsonPath("$.id",equalTo(metadata.getId().toString())))
                .andExpect(jsonPath("$.group",equalTo("mygroup")))
                .andExpect(jsonPath("$.tags").doesNotExist())
                .andExpect(jsonPath("$.name",equalTo("myconfig")))
                .andExpect(jsonPath("$.value.key1",equalTo("value1")))
                .andExpect(jsonPath("$.value.key2",equalTo(Arrays.asList("One","Two","Three"))))
                .andDo(restDoc("getMetadataById"));;

    }

    @Test
    public void shouldReturn404WhenRequestedConfigForAGroupDoesNotExists() throws Exception {
        ResultActions result = mvc.perform(get("/metadata/mygroup/myconfig" ).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message",equalTo("Requested metadata entity having id mygroup:myconfig does not exists in the system.")))
                .andDo(restDoc("getMetadataByGroup404"));;

    }

    @Test
    public void shouldReturn404WhenRequestedConfigForIdDoesNotExists() throws Exception {
        ResultActions result = mvc.perform(get("/metadata/507f1f77bcf86cd799439011" ).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message",equalTo("Requested metadata entity having id 507f1f77bcf86cd799439011 does not exists in the system.")))
                .andDo(restDoc("getMetadataById404"));;

    }


}