package org.eagleinvsys.test.converters.mappers;

import org.eagleinvsys.test.converters.ConvertibleCollection;
import org.eagleinvsys.test.converters.ConvertibleMessage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CollectionMapper {
    ConvertibleMessage toConvertibleMessage(Map<String, String> record);
    Optional<ConvertibleCollection> toConvertibleCollection(List<Map<String, String>> data);
}
