package org.boot.services.metadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MetadataGetListController {

    @Autowired
    private MetadataRepository repository;

    @Autowired
    private AnyObjectRepository anyObjectRepository;

    @GetMapping("/metadata/groups/{group}")
    public List<MetadataView> getAllMetadataForAGroup(@PathVariable String group) {
        return repository.findByGroup(group).map(MetadataView::new).collect(Collectors.toList());
    }


    @GetMapping("/metadata")
    public List<MetadataView> getMetadata(
            @RequestParam(required = false) String group,
            @RequestParam(required = false, defaultValue = "lastUpdatedTs") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {


        QueryCriteria config = new QueryCriteria();
        config.addFilter("group", group);
        config.projectField("id").projectField("group").projectField("name");
        config.sort(sortBy, sortOrder);
        config.page(offset, limit);

        return anyObjectRepository.find(config.build(),Metadata.class).map(MetadataView::new).collect(Collectors.toList());
    }


}
