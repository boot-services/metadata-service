package org.boot.services.metadata;

public class MetadataView {
    private Metadata metadata;

    public MetadataView(Metadata metadata) {
        this.metadata = metadata;
    }

    public String getId() {
        return metadata.getId().toString();
    }

    public String getGroup() {
        return metadata.getGroup();
    }

    public String getName() {
        return metadata.getName();
    }

    public Object getValue() {
        return metadata.getValue();
    }

}
