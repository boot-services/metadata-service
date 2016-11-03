package org.boot.services.metadata;

import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.preprocess.ContentModifier;

import java.nio.ByteBuffer;

public class JsonSyntaxHighlighter implements ContentModifier {

    @Override
    public byte[] modifyContent(byte[] content, MediaType mediaType) {
        byte end[] = "\n----\n\n".getBytes();
        byte start[] = ("[source,json,options=\"nowrap\"]\n" + "----\n").getBytes();
        ByteBuffer bb = ByteBuffer.allocate(start.length + content.length + end.length);
        return bb.put(end).put(start).put(content).array();
    }
}
