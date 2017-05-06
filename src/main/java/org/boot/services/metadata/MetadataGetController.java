package org.boot.services.metadata;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@RestController
public class MetadataGetController {

    @Value("${application.cacheControl.control.max.age}")
    private Integer maxAge;

    @Autowired
    private MetadataRepository repository;

    @GetMapping("/metadata/{group}/{name}")
    public MetadataView getMetadata(@PathVariable String group, @PathVariable String name, HttpServletResponse response) {
        Metadata metadata = repository.findByGroupAndName(group, name)
                .orElseThrow(() -> new ObjectNotFoundException("metadata", String.format("%s:%s", group, name)));

        response.setHeader("Cache-Control", cacheControl().getHeaderValue());
        return new MetadataView(metadata);
    }


    @GetMapping("/metadata/{id}")
    public MetadataView getMetadata(@PathVariable ObjectId id, HttpServletResponse response) {
        Metadata metadata = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("metadata", id.toString()));

        response.setHeader("Cache-Control", cacheControl().getHeaderValue());
        return new MetadataView(metadata);
    }


    private CacheControl cacheControl() {
        return CacheControl.maxAge(maxAge, TimeUnit.MINUTES).cachePublic();
    }
}
