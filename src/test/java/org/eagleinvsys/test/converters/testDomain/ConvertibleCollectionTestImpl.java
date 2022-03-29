package org.eagleinvsys.test.converters.testDomain;

import lombok.Builder;
import lombok.Data;
import org.eagleinvsys.test.converters.ConvertibleCollection;
import org.eagleinvsys.test.converters.ConvertibleMessage;

import java.util.Collection;

@Builder
@Data
public class ConvertibleCollectionTestImpl implements ConvertibleCollection {

    private Collection<String> headers;
    private Iterable<ConvertibleMessage> records;

    @Override
    public Collection<String> getHeaders() {
        return headers;
    }

    @Override
    public Iterable<ConvertibleMessage> getRecords() {
        return records;
    }
}
