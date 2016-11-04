package org.boot.services.metadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

public class MetadataBuilder {

    private String id;
    private String group;
    private String name;
    private Object value;

    public MetadataBuilder id(String id) {
        this.id = id;
        return this;
    }

    public MetadataBuilder group(String group){
        this.group = group;
        return this;
    }

    public MetadataBuilder name(String name){
        this.name = name;
        return this;
    }

    public MetadataBuilder value(String key, Object value){
        if (this.value instanceof HashMap)
            ((Map)this.value).put(key,value);
        else
            this.value = new HashMap<String,Object>(){{ put(key,value);}};
        return this;
    }

    public MetadataBuilder value(Object value){
        this.value = value;
        return this;
    }

    public Metadata build(){
        return new Metadata(id, group, name, value);
    }

    public String buildAsJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> obj = new HashMap<String,Object>(){{
            if (id != null) put("id",id);
            if (group != null) put("group",group);
            if (name != null) put("name",name);
            if (value != null) put("value",value);
        }};
        return mapper.writeValueAsString(obj);
    }

}
