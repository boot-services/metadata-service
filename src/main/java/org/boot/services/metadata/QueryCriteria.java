package org.boot.services.metadata;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryCriteria {

    private List<String> projectedFields = new ArrayList<>();
    private Map<String, Object> filters = new HashMap<>();
    private String sortBy = "lastUpdatedTs";
    private String sortOrder = "desc";
    private Integer offset = 0;
    private Integer limit = 10;


    public QueryCriteria addFilter(String fieldName, Object value) {
        if (value == null) return this;
        filters.put(fieldName, value);
        return this;
    }

    public QueryCriteria projectField(String field) {
        if (StringUtils.isEmpty(field)) return this;
        projectedFields.add(field);
        return this;
    }

    public QueryCriteria sort(String sortBy, String sortOrder) {
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
        return this;
    }

    public QueryCriteria page(Integer offset, Integer limit) {
        this.offset = offset;
        this.limit = limit;
        return this;
    }

    public Query build(){
        Query query = new Query();
        filters.forEach((key, value) -> query.addCriteria(Criteria.where(key).is(value)));
        projectedFields.forEach(field -> query.fields().include(field));
        query.skip(offset).limit(limit);
        query.with(new Sort(Sort.Direction.fromString(sortOrder), sortBy));
        return query;
    }
}
