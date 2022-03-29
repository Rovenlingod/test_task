package org.eagleinvsys.test.converters.mappers;

import lombok.NonNull;
import org.eagleinvsys.test.converters.ConvertibleCollection;
import org.eagleinvsys.test.converters.ConvertibleMessage;

import java.util.*;
import java.util.stream.Collectors;

public class ConvertibleCollectionMapper implements CollectionMapper {

    private static ConvertibleCollectionMapper INSTANCE;

    private ConvertibleCollectionMapper() {

    }

    public static ConvertibleCollectionMapper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConvertibleCollectionMapper();
        }

        return INSTANCE;
    }


    /**
     * Maps given {@link Map} into anonymous implementation of {@link ConvertibleMessage}
     *
     * @param record data presented as {@link Map} where key is header and value is value of the record
     * @return       wrapped data into {@link ConvertibleMessage}
     */
    public ConvertibleMessage toConvertibleMessage(@NonNull Map<String, String> record) {
        return new ConvertibleMessage() {
            private final Map<String, String> data = record;

            @Override
            public String getElement(String elementId) {
                return data.get(elementId);
            }
        };
    }

    /**
     * Maps given {@link List} into {@link Optional} anonymous implementation of {@link ConvertibleCollection}
     *
     * @param data {@link List} of records presented as {@link Map}
     * @return     {@link Optional} {@link ConvertibleCollection} representation of data
     */
    public Optional<ConvertibleCollection> toConvertibleCollection(List<Map<String, String>> data) {

        if (Objects.isNull(data) || data.isEmpty()) {
            return Optional.empty();
        }

        Set<String> resultHeaders = data.get(0).keySet();
        List<ConvertibleMessage> resultMessages = data.stream()
                .map(this::toConvertibleMessage)
                .collect(Collectors.toList());

        return Optional.of(new ConvertibleCollection() {

            private final Collection<String> headers = resultHeaders;
            private final Iterable<ConvertibleMessage> records = resultMessages;

            @Override
            public Collection<String> getHeaders() {
                return headers;
            }

            @Override
            public Iterable<ConvertibleMessage> getRecords() {
                return records;
            }
        });
    }
}
