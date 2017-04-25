package org.boot.services.metadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public class AnyObjectRepository {

    @Autowired
    private MongoTemplate template;

    public <T> Stream<T> find(Query query, Class<T> klass){
        return template.find(query, klass).stream();
    }
}
