package org.eagleinvsys.test.converters.testDomain;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.eagleinvsys.test.converters.ConvertibleMessage;

import java.util.Map;

@Builder
@Data
public class ConvertibleMessageTestImpl implements ConvertibleMessage {

    private Map<String, String> data;

    @Override
    public String getElement(String elementId) {
        return data.get(elementId);
    }
}
