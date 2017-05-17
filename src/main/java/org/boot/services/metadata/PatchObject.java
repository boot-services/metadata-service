package org.boot.services.metadata;

import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import java.util.Map;

public class PatchObject {

    private Map<String, Object> fields;

    public PatchObject(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Update getUpdateObject() {
        Update update = new Update();
        updateValuesForUpsert(update, fields, null);
        return update;
    }

    private void updateValuesForUpsert(Update update, Map<String, Object> data, String path) {
        data.forEach((String key,Object value) -> {
            if (isUnset(value.toString())) {
                update.unset(buildPath(key, path));
            } else {
                if (value instanceof Map) updateValuesForUpsert(update, (Map<String, Object>) value, buildPath(key, path));
                else update.set(buildPath(key, path), value);
            }
        });
    }

    private String buildPath(String key, String path) {
        if (StringUtils.isEmpty(path)) return key;
        else return path.concat(".").concat(key);
    }

    private boolean isUnset(String value) {
        return (StringUtils.isEmpty(value)
                || "null".equalsIgnoreCase(value)
                || "empty".equalsIgnoreCase(value));
    }
}
