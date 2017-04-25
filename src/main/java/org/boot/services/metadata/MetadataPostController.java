package org.boot.services.metadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MetadataPostController {


    @Autowired
    private MetadataRepository repository;

    @PostMapping("/metadata")
    public Map<String,String> getMetadata(@RequestBody @Valid Metadata metadata, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException("Validation failed in saving metadata. Group, Name and Value are mandatory fields.");
        }
        metadata.setLastUpdatedTs();
        Metadata savedMetadata = repository.save(metadata);
        return new HashMap<String,String>(){{
            put("id",savedMetadata.getId().toString());
            put("message","Successfully saved metadata.");
        }};
    }


}
