package org.eagleinvsys.test.converters;

import org.eagleinvsys.test.converters.exceptions.InvalidCollectionException;
import org.eagleinvsys.test.converters.impl.CsvConverter;
import org.eagleinvsys.test.converters.impl.StandardCsvConverter;
import org.eagleinvsys.test.converters.mappers.ConvertibleCollectionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StandardCsvConverterTests {

    @Mock
    CsvConverter csvConverter;
    @InjectMocks
    StandardCsvConverter standardCsvConverter;
    @Mock
    ConvertibleCollectionMapper mockMapper;

    @Test
    public void givenNullCollection_whenConvert_thenThrowInvalidCollectionException() {
        Throwable actualException = assertThrows(InvalidCollectionException.class,
                () -> standardCsvConverter.convert(null, OutputStream.nullOutputStream()));
        assertEquals("Provided collectionToConvert can't be empty or null.", actualException.getMessage());
    }

    @Test
    public void givenEmptyCollection_whenConvert_thenThrowInvalidCollectionException() {
        Throwable actualException = assertThrows(InvalidCollectionException.class,
                () -> standardCsvConverter.convert(Collections.emptyList(), OutputStream.nullOutputStream()));
        assertEquals("Provided collectionToConvert can't be empty or null.", actualException.getMessage());
    }

    @Test
    public void givenCollectionWithNullMap_whenConvert_thenThrowInvalidCollectionException() {
        List<Map<String, String>> listWithNullAsFirstParameter = new ArrayList<>();
        listWithNullAsFirstParameter.add(null);
        Throwable exceptionWithNullAsFirstParameter = assertThrows(InvalidCollectionException.class,
                () -> standardCsvConverter.convert(listWithNullAsFirstParameter, OutputStream.nullOutputStream()));
        assertEquals("One of the maps is empty or null.", exceptionWithNullAsFirstParameter.getMessage());

        List<Map<String, String>> listWithNull = new ArrayList<>();
        listWithNull.add(Map.of("testKey", "testValue"));
        listWithNull.add(null);
        Throwable actualException = assertThrows(InvalidCollectionException.class,
                () -> standardCsvConverter.convert(listWithNull, OutputStream.nullOutputStream()));
        assertEquals("One of the maps is empty or null.", actualException.getMessage());
    }

    @Test
    public void givenCollectionWithEmptyMap_whenConvert_thenThrowInvalidCollectionException() {
        List<Map<String, String>> listWithEmptyMapsFirstParameter = new ArrayList<>();
        listWithEmptyMapsFirstParameter.add(Collections.emptyMap());
        Throwable exceptionWithNullAsFirstParameter = assertThrows(InvalidCollectionException.class,
                () -> standardCsvConverter.convert(listWithEmptyMapsFirstParameter, OutputStream.nullOutputStream()));
        assertEquals("One of the maps is empty or null.", exceptionWithNullAsFirstParameter.getMessage());

        List<Map<String, String>> listWithEmptyMap = new ArrayList<>();
        listWithEmptyMap.add(Map.of("testKey", "testValue"));
        listWithEmptyMap.add(Collections.emptyMap());
        Throwable actualException = assertThrows(InvalidCollectionException.class,
                () -> standardCsvConverter.convert(listWithEmptyMap, OutputStream.nullOutputStream()));
        assertEquals("One of the maps is empty or null.", actualException.getMessage());
    }

    @Test
    public void givenCollectionWithMapsWithDifferentKeySets_whenConvert_thenThrowInvalidCollectionException() {
        List<Map<String, String>> testCollection = List.of(
                Map.of("testKey1", "firstTestValue1", "testKey2", "firstTestValue2"),
                Map.of("testKey1", "secondTestValue1", "randomTestKey", "secondTestValue2"));
        Throwable actualException = assertThrows(InvalidCollectionException.class,
                () -> standardCsvConverter.convert(testCollection, OutputStream.nullOutputStream()));
        assertEquals("All maps must have the same set of keys.", actualException.getMessage());
    }

    @Test
    public void givenCollectionWithMapsWithDifferentKeySetsSizes_whenConvert_thenThrowInvalidCollectionException() {
        List<Map<String, String>> testCollection = List.of(
                Map.of("testKey1", "firstTestValue1", "testKey2", "firstTestValue2"),
                Map.of("testKey1", "secondTestValue1", "testKey", "secondTestValue2", "randomTestKey", "secondTestValue2"));
        Throwable actualException = assertThrows(InvalidCollectionException.class,
                () -> standardCsvConverter.convert(testCollection, OutputStream.nullOutputStream()));
        assertEquals("All maps must have the same set of keys.", actualException.getMessage());
    }

    @Test
    public void givenCollectionWithValidMaps_whenConvert_thenOutputStreamContainsValidCSV() {
        List<Map<String, String>> testCollection = List.of(
                Map.of("testKey1", "firstTestValue1", "testKey2", "firstTestValue2"),
                Map.of("testKey1", "secondTestValue1", "testKey2", "secondTestValue2"));
        ByteArrayOutputStream testBaos = new ByteArrayOutputStream();
        ConvertibleCollection testConvertibleCollection = new ConvertibleCollection() {
            private final Collection<String> headers = List.of("testKey1", "testKey2");
            private final Iterable<ConvertibleMessage> records = List.of(new ConvertibleMessage() {
                private final Map<String, String> data = Map.of("testKey1", "firstTestValue1", "testKey2", "firstTestValue2");

                @Override
                public String getElement(String elementId) {
                    return data.get(elementId);
                }
            }, new ConvertibleMessage() {
                private final Map<String, String> data = Map.of("testKey1", "secondTestValue1", "testKey2", "secondTestValue2");

                @Override
                public String getElement(String elementId) {
                    return data.get(elementId);
                }
            });

            @Override
            public Collection<String> getHeaders() {
                return headers;
            }

            @Override
            public Iterable<ConvertibleMessage> getRecords() {
                return records;
            }
        };
        String expectedResult = "testKey1,testKey2" + "\n"
                + "firstTestValue1,firstTestValue2" + "\n"
                + "secondTestValue1,secondTestValue2";
        when(mockMapper.toConvertibleCollection(testCollection)).thenReturn(Optional.of(testConvertibleCollection));
        try (MockedStatic<ConvertibleCollectionMapper> staticMapper = mockStatic(ConvertibleCollectionMapper.class)) {
            staticMapper.when(ConvertibleCollectionMapper::getInstance).thenReturn(mockMapper);
            doAnswer((invocation) -> {
                testBaos.write(expectedResult.getBytes());
                return null;
            }).when(csvConverter).convert(testConvertibleCollection, testBaos);
            standardCsvConverter.convert(testCollection, testBaos);
            String actualResult = testBaos.toString();
            assertEquals(expectedResult, actualResult);
        }
    }



}