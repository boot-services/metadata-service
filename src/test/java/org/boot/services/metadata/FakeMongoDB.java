package org.boot.services.metadata;

import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@Configuration
public class FakeMongoDB extends AbstractMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "InMemoryMongo";
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception {
        return new Fongo(getDatabaseName()).getMongo();
    }
}
