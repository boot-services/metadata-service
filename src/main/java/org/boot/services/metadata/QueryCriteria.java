package org.boot.services.metadata;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class QueryCriteria {

    private String timeField;

    private List<String> projection = new ArrayList<>();
    private Map<String, Object> filters = new HashMap<>();
    private String sortBy;
    private String sortOrder;
    private Integer offset;
    private Integer limit;
    private LocalDateTime since;
    private LocalDateTime until;


    public QueryCriteria addFilter(String fieldName, Object value) {
        if (value == null || (value instanceof Collection && ((Collection) value).isEmpty())) return this;
        filters.put(fieldName, value);
        return this;
    }

    public QueryCriteria projection(String... fields) {
        if (StringUtils.isEmpty(fields)) return this;
        projection.addAll(Arrays.asList(fields));
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

    public QueryCriteria between(String timeField, String receivedSince, String receivedUntil) {
        if (StringUtils.isEmpty(timeField) || StringUtils.isEmpty(receivedSince) || StringUtils.isEmpty(receivedUntil)) return this;
        this.timeField = timeField;
        this.since = LocalDateTime.parse(receivedSince, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.until = LocalDateTime.parse(receivedUntil, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return this;
    }

    public Query build(){
        Query query = new Query();
        filters.forEach((key, value) -> {
            if (value instanceof List) {
                List<String> ids = (List<String>) value;
                query.addCriteria(Criteria.where(key).in(ids));
            }
            else {
                query.addCriteria(Criteria.where(key).is(value));
            }
        });
        if (this.since != null && this.until != null){
            query.addCriteria(Criteria.where(timeField).gte(since).lte(until));
        }
        projection.forEach(field -> query.fields().include(field));
        if (offset != null && limit != null) query.skip(offset).limit(limit);
        if (sortOrder != null && sortBy != null) query.with(new Sort(Sort.Direction.fromString(sortOrder), sortBy));
        return query;
    }
}
