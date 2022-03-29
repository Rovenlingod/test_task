package org.eagleinvsys.test.converters.impl;

import org.eagleinvsys.test.converters.ConvertibleCollection;
import org.eagleinvsys.test.converters.ConvertibleMessage;
import org.eagleinvsys.test.converters.StandardConverter;
import org.eagleinvsys.test.converters.exceptions.InvalidCollectionException;
import org.eagleinvsys.test.converters.mappers.ConvertibleCollectionMapper;

import java.io.OutputStream;
import java.util.*;

public class StandardCsvConverter implements StandardConverter {

    private final CsvConverter csvConverter;

    public StandardCsvConverter(CsvConverter csvConverter) {
        this.csvConverter = csvConverter;
    }

    /**
     * Converts given {@link List<Map>} to CSV and outputs result as a text to the provided {@link OutputStream}
     *
     * @param collectionToConvert collection to convert to CSV format. All maps must have the same set of keys.
     *                            For stable columns order client should provide map that saves order of insertion. For example {@link LinkedHashMap}
     * @param outputStream        output stream to write CSV conversion result as text to
     */
    @Override
    public void convert(List<Map<String, String>> collectionToConvert, OutputStream outputStream) {
        validateCollection(collectionToConvert);
        Optional<ConvertibleCollection> convertibleCollection = ConvertibleCollectionMapper.getInstance().toConvertibleCollection(collectionToConvert);
        if (convertibleCollection.isEmpty()) {
            return;
        }
        csvConverter.convert(convertibleCollection.get(), outputStream);
    }

    /**
     * Validates given collection
     *
     * @param collectionToConvert         collection to convert to CSV format. All maps must have the same set of keys.
     *
     * @throws InvalidCollectionException if collectionToConvert is null or empty
     * @throws InvalidCollectionException if one of the maps is null or empty
     * @throws InvalidCollectionException if maps have different set of keys
     */
    private void validateCollection(List<Map<String, String>> collectionToConvert) {
        if (Objects.isNull(collectionToConvert) || collectionToConvert.isEmpty()) {
            throw new InvalidCollectionException("Provided collectionToConvert can't be empty or null.");
        }
        Map<String, String> firstRecord = collectionToConvert.get(0);
        if (Objects.isNull(firstRecord) || firstRecord.isEmpty()) {
            throw new InvalidCollectionException("One of the maps is empty or null.");
        }
        Set<String> headers = firstRecord.keySet();
        for (Map<String, String> map :
                collectionToConvert) {
            if (Objects.isNull(map) || map.isEmpty()) {
                throw new InvalidCollectionException("One of the maps is empty or null.");
            }
            if (!map.keySet().equals(headers)) {
                throw new InvalidCollectionException("All maps must have the same set of keys.");
            }
        }
    }

}