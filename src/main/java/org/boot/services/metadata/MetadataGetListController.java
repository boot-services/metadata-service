package org.boot.services.metadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MetadataGetListController {
    public static final String ISO_DATE_TIME_REGEX = "^([\\+-]?\\d{4}(?!\\d{2}\\b))((-?)((0[1-9]|1[0-2])(\\3([12]\\d|0[1-9]|3[01]))?|W([0-4]\\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\\d|[12]\\d{2}|3([0-5]\\d|6[1-6])))([T\\s]((([01]\\d|2[0-3])((:?)[0-5]\\d)?|24\\:?00)([\\.,]\\d+(?!:))?)?(\\17[0-5]\\d([\\.,]\\d+)?)?([zZ]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?)?)?$";

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

            @RequestParam(required = false) List<String> tags,

            @Valid @Pattern(regexp = ISO_DATE_TIME_REGEX)
                @RequestParam(required = false) String since,
            @Valid @Pattern(regexp = ISO_DATE_TIME_REGEX)
                @RequestParam(required = false) String until,

            @RequestParam(required = false, defaultValue = "lastUpdatedTs") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {


        QueryCriteria config = new QueryCriteria()
                .addFilter("group", group)
                .addFilter("tags", tags)
                .between("lastUpdatedTs", since,until)
                .projection("id","group","name")
                .sort(sortBy, sortOrder)
                .page(offset, limit);

        return anyObjectRepository.find(config.build(),Metadata.class).map(MetadataView::new).collect(Collectors.toList());
    }


}
