package org.boot.services.metadata;

import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

public class MetadataBuilder {

    private ObjectId id;
    private String group = "uno";
    private String name;
    private Map<String,Object> value = new HashMap<>();

    public MetadataBuilder group(String group){
        this.group = group;
        return this;
    }

    public MetadataBuilder name(String name){
        this.name = name;
        return this;
    }

    public MetadataBuilder value(String key, Object value){
        this.value.put(key,value);
        return this;
    }

    public Metadata build(){
        return new Metadata(group, name, value);
    }
}
