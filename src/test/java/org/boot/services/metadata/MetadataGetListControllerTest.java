package org.boot.services.metadata;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Arrays;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MetadataGetListControllerTest extends BaseControllerTest {

    @Autowired
    MetadataRepository repository;

    @Before
    public void setup() {
        repository.deleteAll();
    }


    @Test
    public void shouldReturnAllConfigsForAGroup() throws Exception {
        repository.save(new MetadataBuilder().group("mygroup").name("myconfig").value("key1", "value1").value("key2", Arrays.asList("One", "Two", "Three")).build());
        repository.save(new MetadataBuilder().group("mygroup").name("myconfig2").value(100).build());
        repository.save(new MetadataBuilder().group("mygroup").name("myconfig3").value(Arrays.asList("first", "second")).build());

        ResultActions result = mvc.perform(get("/metadata/groups/mygroup").accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].group", equalTo("mygroup")))
                .andExpect(jsonPath("$[0].name", equalTo("myconfig")))
                .andExpect(jsonPath("$[0].value.key1", equalTo("value1")))
                .andExpect(jsonPath("$[0].value.key2", equalTo(Arrays.asList("One", "Two", "Three"))))
                .andExpect(jsonPath("$[1].group", equalTo("mygroup")))
                .andExpect(jsonPath("$[1].name", equalTo("myconfig2")))
                .andExpect(jsonPath("$[1].value", equalTo(100)))
                .andExpect(jsonPath("$[2].group", equalTo("mygroup")))
                .andExpect(jsonPath("$[2].name", equalTo("myconfig3")))
                .andExpect(jsonPath("$[2].value", equalTo(Arrays.asList("first", "second"))))
                .andDo(restDoc("getAllMetadataForGroup"));

    }

    @Test
    public void shouldReturnEmptyListForAGroupThatDoesNotExists() throws Exception {

        ResultActions result = mvc.perform(get("/metadata/groups/mygroup").accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andDo(restDoc("getAllMetadataForGroupEmpty"));
        ;

    }


    @Test
    public void shouldReturnAllMetadataForAGroupSortedByName() throws Exception {
        repository.save(new MetadataBuilder().group("mygroup").name("myconfig1").value("key1", "value1").value("key2", Arrays.asList("One", "Two", "Three")).build());
        repository.save(new MetadataBuilder().group("notmygroup").name("myconfig2").value(100).build());
        repository.save(new MetadataBuilder().group("mygroup").name("myconfig3").value(Arrays.asList("first", "second")).build());

        ResultActions result = mvc.perform(get("/metadata?group=mygroup&sortBy=name").accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].group", equalTo("mygroup")))
                .andExpect(jsonPath("$[0].name", equalTo("myconfig3")))
                .andExpect(jsonPath("$[1].group", equalTo("mygroup")))
                .andExpect(jsonPath("$[1].name", equalTo("myconfig1")))
                .andDo(restDoc("getAllMetadataForAGroupSortedByName"));

    }


    @Test
    public void shouldReturnAllMetadataUpdatedBetwweenSpecificTime() throws Exception {
        LocalDateTime time10AM = LocalDateTime.of(2017, 4, 28, 10, 0);
        repository.save(new MetadataBuilder(clock(time10AM)).group("mygroup").name("myconfig1").value("key1", "value1").value("key2", Arrays.asList("One", "Two", "Three")).build());
        repository.save(new MetadataBuilder(clock(time10AM.minusMinutes(10))).group("mygroup").name("myconfig2").value(100).build());
        repository.save(new MetadataBuilder(clock(time10AM.plusMinutes(15))).group("mygroup").name("myconfig3").value(Arrays.asList("first", "second")).build());

        String receivedSince10AM = time10AM.format(ISO_LOCAL_DATE_TIME);
        String receivedUntil11AM = time10AM.plusHours(1).format(ISO_LOCAL_DATE_TIME);
        ResultActions result = mvc.perform(get("/metadata?group=mygroup&sortBy=name&since=" + receivedSince10AM + "&until=" + receivedUntil11AM).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].group", equalTo("mygroup")))
                .andExpect(jsonPath("$[0].name", equalTo("myconfig3")))
                .andExpect(jsonPath("$[1].group", equalTo("mygroup")))
                .andExpect(jsonPath("$[1].name", equalTo("myconfig1")))
                .andDo(restDoc("getAllMetadataForAGroupSortedByName"));

    }


    @Test
    public void shouldReturnAllMetadataMatchingTags() throws Exception {
        repository.save(new MetadataBuilder().group("mygroup").name("myconfig1")
                .value("key1", "value1").value("key2", Arrays.asList("One", "Two", "Three")).tags("tag1","tag2").build());
        repository.save(new MetadataBuilder().group("notmygroup").name("myconfig2")
                .value(100).tags("tag3","tag4").build());
        repository.save(new MetadataBuilder().group("mygroup").name("myconfig3")
                .value(Arrays.asList("first", "second")).tags("tag1","tag4").build());

        ResultActions result = mvc.perform(get("/metadata?tags=tag1&tags=tag2").accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].group", equalTo("mygroup")))
                .andExpect(jsonPath("$[0].name", equalTo("myconfig3")))
                .andExpect(jsonPath("$[1].group", equalTo("mygroup")))
                .andExpect(jsonPath("$[1].name", equalTo("myconfig1")))
                .andDo(restDoc("getAllMetadataForMatchingTags"));

    }

}