package org.eagleinvsys.test.converters.impl;

import lombok.NonNull;
import org.eagleinvsys.test.converters.Converter;
import org.eagleinvsys.test.converters.ConvertibleCollection;
import org.eagleinvsys.test.converters.ConvertibleMessage;
import org.eagleinvsys.test.converters.exceptions.InvalidCollectionException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class CsvConverter implements Converter {

    private static final String DELIMITER = ",";

    /**
     * Converts given {@link ConvertibleCollection} to CSV and outputs result as a text to the provided {@link OutputStream}
     *
     * @param collectionToConvert   collection to convert to CSV format
     * @param outputStream          output stream to write CSV conversion result as text to
     *
     * @throws NullPointerException if collectionToConvert or outputStream is null
     */
    @Override
    public void convert(@NonNull ConvertibleCollection collectionToConvert, @NonNull OutputStream outputStream) {
        validateConvertibleCollection(collectionToConvert);
        try {
            String headersAsString = collectionToConvert.getHeaders().stream()
                    .map(this::escapeSpecialCharacters)
                    .collect(Collectors.joining(DELIMITER));
            outputStream.write(headersAsString.getBytes());
            outputStream.write("\n".getBytes());
            Iterable<ConvertibleMessage> records = collectionToConvert.getRecords();
            for (ConvertibleMessage message :
                    records) {
                String record = collectionToConvert.getHeaders().stream()
                        .map(message::getElement)
                        .map(this::escapeSpecialCharacters)
                        .collect(Collectors.joining(DELIMITER));
                outputStream.write(record.getBytes());
                outputStream.write("\n".getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Validates given collection
     *
     * @param collectionToConvert         collection to convert to CSV format.
     *
     * @throws NullPointerException       if collectionToConvert is null
     * @throws InvalidCollectionException if getHeaders() returns null or empty collection
     * @throws InvalidCollectionException if getRecords() returns null
     * @throws InvalidCollectionException if any header is null
     */
    private void validateConvertibleCollection(@NonNull ConvertibleCollection collectionToConvert) {
        if (Objects.isNull(collectionToConvert.getHeaders()) || collectionToConvert.getHeaders().isEmpty()) {
            throw new InvalidCollectionException("Headers are null.");
        }
        if (collectionToConvert.getHeaders().stream().anyMatch(Objects::isNull)) {
            throw new InvalidCollectionException("At least one of the headers is null.");
        }
        if (Objects.isNull(collectionToConvert.getRecords())) {
            throw new InvalidCollectionException("Records are null.");
        }
    }

    /**
     * Fields containing commas or quotes will be surrounded by quotes and quotes will be escaped with double quotes
     *
     * @param data data to escape special characters
     * @return     data with escaped quotes and commas or empty string if provided data is null
     */
    private String escapeSpecialCharacters(String data) {
        if (Objects.isNull(data)) {
            return "";
        }
        String escapedData = data.replaceAll("\\s", " ");
        if (escapedData.contains(",") || escapedData.contains("\"") || escapedData.contains("'")) {
            escapedData = escapedData.replace("\"", "\"\"");
            escapedData = "\"" + escapedData + "\"";
        }
        return escapedData;
    }

}