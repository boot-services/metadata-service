package org.boot.services.metadata;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
public class MetadataGetController {

    @Autowired
    MetadataRepository repository;

    @GetMapping("/metadata/{group}/{name}")
    public MetadataView getMetadata(@PathVariable String group, @PathVariable String name){
        Optional<Metadata> metadata = repository.findByGroupAndName(group, name);
        if (metadata.isPresent()) return new MetadataView(metadata.get());
        else throw new ObjectNotFoundException("metadata",String.format("%s:%s",group,name));
    }

    @GetMapping("/metadata/{id}")
    public MetadataView getMetadata(@PathVariable ObjectId id){
        return new MetadataView(repository.findOne(id));
    }

    @GetMapping("/metadata/groups/{group}")
    public List<MetadataView> getAllMetadataForAGroup(@PathVariable String group){
        return repository.findByGroup(group).map(MetadataView::new).collect(Collectors.toList());
    }

    public CacheControl setup(){
        return CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic();
    }
}
