package org.boot.services.metadata;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "metadata")
@CompoundIndexes({
        @CompoundIndex(name = "group_name", def = "{'group': 1, 'name': 1}")
})
public class Metadata {

    @Id private ObjectId id;
    @NotNull @Indexed private String group;
    @NotNull @Indexed private String name;
    @NotNull private Object value;
    @Indexed private LocalDateTime lastUpdatedTs = LocalDateTime.now();
    @Indexed private List<String> tags;

    public Metadata(String id, String group, String name, Object value, List<String> tags) {
        if (id != null) this.id = new ObjectId(id);
        this.group = group;
        this.name = name;
        this.value = value;
        this.tags = tags;
    }

    public Metadata() {
    }

    public ObjectId getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public List<String> getTags() {
        return tags;
    }

    public LocalDateTime getLastUpdatedTs() {
        return lastUpdatedTs;
    }

    public void setLastUpdatedTs(Clock clock) {
        this.lastUpdatedTs = LocalDateTime.now(clock);
    }
}
