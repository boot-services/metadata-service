package org.boot.services.metadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.io.IOException;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MetadataPostController {

    @Autowired
    private MetadataRepository repository;

    @Autowired
    private AnyObjectRepository anyObjectRepository;

    @PostMapping("/metadata")
    public Map<String,String> getMetadata(@RequestBody @Valid Metadata metadata, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException("Validation failed in saving metadata. Group, Name and Value are mandatory fields.");
        }
        metadata.setLastUpdatedTs(Clock.systemUTC());
        Metadata savedMetadata = repository.save(metadata);
        return new HashMap<String,String>(){{
            put("id",savedMetadata.getId().toString());
            put("message","Successfully saved metadata.");
        }};
    }

    @PostMapping("/metadata/{id}/patch")
    public Map<String,String> patchEntity(@PathVariable String id, @RequestBody Map<String,Object> metadata) throws IOException {
        boolean upserted = anyObjectRepository.upsert(id, new PatchObject(metadata).getUpdateObject(), Metadata.class);

        return new HashMap<String,String>(){{
            put("id",id);
            put("message","Successfully patched metadata.");
        }};
    }

}
