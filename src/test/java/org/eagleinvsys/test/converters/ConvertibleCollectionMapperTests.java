package org.eagleinvsys.test.converters;

import org.eagleinvsys.test.converters.exceptions.InvalidCollectionException;
import org.eagleinvsys.test.converters.mappers.ConvertibleCollectionMapper;
import org.eagleinvsys.test.converters.testDomain.ConvertibleMessageTestImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.OutputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConvertibleCollectionMapperTests {

    @Spy
    ConvertibleCollectionMapper mapper = ConvertibleCollectionMapper.getInstance();

    @Test
    public void givenValidData_whenToConvertibleMessage_thenReturnValidConvertibleMessage() {
        Map<String, String> testData = Map.of("testHeader1", "testValue1", "testHeader2", "testValue2");

        ConvertibleMessage actualMessage = mapper.toConvertibleMessage(testData);

        assertEquals("testValue1", actualMessage.getElement("testHeader1"));
        assertEquals("testValue2", actualMessage.getElement("testHeader2"));
    }

    @Test
    public void givenEmptyData_whenToConvertibleMessage_thenReturnEmptyConvertibleMessage() {
        Map<String, String> testData = Collections.emptyMap();

        ConvertibleMessage actualMessage = mapper.toConvertibleMessage(testData);

        assertNull(actualMessage.getElement("testHeader1"));
        assertNull(actualMessage.getElement("testHeader2"));
    }

    @Test
    public void givenNullData_whenToConvertibleMessage_thenThrowNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> mapper.toConvertibleMessage(null));
    }

    @Test
    public void givenNullData_whenToConvertibleCollection_thenReturnEmptyOptional() {
        Optional<ConvertibleCollection> actualResult = mapper.toConvertibleCollection(null);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    public void givenEmptyData_whenToConvertibleCollection_thenReturnEmptyOptional() {
        List<Map<String, String>> testData = Collections.emptyList();

        Optional<ConvertibleCollection> actualResult = mapper.toConvertibleCollection(testData);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    public void givenValidData_whenToConvertibleCollection_thenReturnValidConvertibleCollectionOptional() {
        Map<String, String> firstRecord = Map.of("k1", "v11", "k2", "v12");
        Map<String, String> secondRecord = Map.of("k1", "v21", "k2", "v22");
        List<Map<String, String>> testData = List.of(firstRecord, secondRecord);

        when(mapper.toConvertibleMessage(firstRecord)).thenReturn(ConvertibleMessageTestImpl.builder().data(firstRecord).build());
        when(mapper.toConvertibleMessage(secondRecord)).thenReturn(ConvertibleMessageTestImpl.builder().data(secondRecord).build());

        Optional<ConvertibleCollection> actualResult = mapper.toConvertibleCollection(testData);
        LinkedList<String> expectedHeaders = new LinkedList<>(); //different type of collection on purpose
        expectedHeaders.add("k1");
        expectedHeaders.add("k2");

        assertTrue(collectionsEqual(expectedHeaders, actualResult.get().getHeaders()));

        Iterator<ConvertibleMessage> actualIterator = actualResult.get().getRecords().iterator();
        ConvertibleMessage actualFirstMessage = actualIterator.next();
        ConvertibleMessage actualSecondsMessage = actualIterator.next();

        assertEquals("v11", actualFirstMessage.getElement("k1"));
        assertEquals("v12", actualFirstMessage.getElement("k2"));
        assertEquals("v21", actualSecondsMessage.getElement("k1"));
        assertEquals("v22", actualSecondsMessage.getElement("k2"));
    }

    private <T> boolean collectionsEqual(Collection<T> firstCollection, Collection<T> secondCollection) {
        if (firstCollection == secondCollection) {
            return true;
        }
        if (firstCollection == null || secondCollection == null || firstCollection.size() != secondCollection.size()) {
            return false;
        }
        return firstCollection.containsAll(secondCollection) && secondCollection.containsAll(firstCollection);
    }

}
