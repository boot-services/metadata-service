package org.boot.services.metadata;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

//@Configuration
public class InMemoryMongoDB extends AbstractMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "InMemoryMongo";
    }

    @Override
    public MongoClient mongoClient() {
        return new Fongo(getDatabaseName()).getMongo();
    }
}
