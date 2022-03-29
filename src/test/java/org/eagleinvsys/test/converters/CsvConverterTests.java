package org.eagleinvsys.test.converters;

import org.eagleinvsys.test.converters.exceptions.InvalidCollectionException;
import org.eagleinvsys.test.converters.impl.CsvConverter;
import org.eagleinvsys.test.converters.testDomain.ConvertibleCollectionTestImpl;
import org.eagleinvsys.test.converters.testDomain.ConvertibleMessageTestImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class CsvConverterTests {

    CsvConverter csvConverter = new CsvConverter();

    @Test
    public void givenValidCollection_whenConvert_thenOutputStreamContainsValidCSV() {
        ConvertibleMessage testFirstMessage = ConvertibleMessageTestImpl.builder()
                .data(Map.of("header1", "firstRecordValue1", "header2", "firstRecordValue2", "header3", "firstRecordValue3"))
                .build();
        ConvertibleMessage testSecondMessage = ConvertibleMessageTestImpl.builder()
                .data(Map.of("header1", "secondRecordValue1", "header2", "secondRecordValue2", "header3", "secondRecordValue3"))
                .build();
        ConvertibleCollection testValidCollection = ConvertibleCollectionTestImpl.builder()
                .headers(List.of("header1", "header2", "header3"))
                .records(List.of(testFirstMessage, testSecondMessage))
                .build();
        ByteArrayOutputStream testBaos = new ByteArrayOutputStream();

        String expectedResult = "header1,header2,header3" + "\n"
                + "firstRecordValue1,firstRecordValue2,firstRecordValue3" + "\n"
                + "secondRecordValue1,secondRecordValue2,secondRecordValue3" + "\n";
        csvConverter.convert(testValidCollection, testBaos);
        String actualResult = testBaos.toString();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void givenValidCollectionWithMultipleQuotes_whenConvert_thenOutputStreamContainsValidCSVSurroundedByQuotes() {
        ConvertibleMessage testFirstMessage = ConvertibleMessageTestImpl.builder()
                .data(Map.of("header1", "validString with \"quotes\"", "header2", "firstRecordValue2", "header3", "firstRecordValue3"))
                .build();
        ConvertibleCollection testValidCollection = ConvertibleCollectionTestImpl.builder()
                .headers(List.of("header1", "header2", "header3"))
                .records(List.of(testFirstMessage))
                .build();
        ByteArrayOutputStream testBaos = new ByteArrayOutputStream();

        String expectedResult = "header1,header2,header3" + "\n"
                + "\"validString with \"\"quotes\"\"\",firstRecordValue2,firstRecordValue3" + "\n";
        csvConverter.convert(testValidCollection, testBaos);
        String actualResult = testBaos.toString();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void givenValidCollectionWithQuote_whenConvert_thenOutputStreamContainsValidCSVSurroundedByQuotes() {
        ConvertibleMessage testFirstMessage = ConvertibleMessageTestImpl.builder()
                .data(Map.of("header1", "validString with \"quote", "header2", "firstRecordValue2", "header3", "firstRecordValue3"))
                .build();
        ConvertibleCollection testValidCollection = ConvertibleCollectionTestImpl.builder()
                .headers(List.of("header1", "header2", "header3"))
                .records(List.of(testFirstMessage))
                .build();
        ByteArrayOutputStream testBaos = new ByteArrayOutputStream();

        String expectedResult = "header1,header2,header3" + "\n"
                + "\"validString with \"\"quote\",firstRecordValue2,firstRecordValue3" + "\n";
        csvConverter.convert(testValidCollection, testBaos);
        String actualResult = testBaos.toString();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void givenValidCollectionWithComma_whenConvert_thenOutputStreamContainsValidCSVSurroundedByQuotes() {
        ConvertibleMessage testFirstMessage = ConvertibleMessageTestImpl.builder()
                .data(Map.of("header1", "validString with, comma", "header2", "firstRecordValue2", "header3", "firstRecordValue3"))
                .build();
        ConvertibleCollection testValidCollection = ConvertibleCollectionTestImpl.builder()
                .headers(List.of("header1", "header2", "header3"))
                .records(List.of(testFirstMessage))
                .build();
        ByteArrayOutputStream testBaos = new ByteArrayOutputStream();

        String expectedResult = "header1,header2,header3" + "\n"
                + "\"validString with, comma\",firstRecordValue2,firstRecordValue3" + "\n";
        csvConverter.convert(testValidCollection, testBaos);
        String actualResult = testBaos.toString();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void givenValidCollectionWithMultipleCommas_whenConvert_thenOutputStreamContainsValidCSVSurroundedByQuotes() {
        ConvertibleMessage testFirstMessage = ConvertibleMessageTestImpl.builder()
                .data(Map.of("header1", "validString with, multiple, commas", "header2", "firstRecordValue2", "header3", "firstRecordValue3"))
                .build();
        ConvertibleCollection testValidCollection = ConvertibleCollectionTestImpl.builder()
                .headers(List.of("header1", "header2", "header3"))
                .records(List.of(testFirstMessage))
                .build();
        ByteArrayOutputStream testBaos = new ByteArrayOutputStream();

        String expectedResult = "header1,header2,header3" + "\n"
                + "\"validString with, multiple, commas\",firstRecordValue2,firstRecordValue3" + "\n";
        csvConverter.convert(testValidCollection, testBaos);
        String actualResult = testBaos.toString();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void givenValidCollectionWithSingleQuote_whenConvert_thenOutputStreamContainsValidCSVSurroundedByQuotes() {
        ConvertibleMessage testFirstMessage = ConvertibleMessageTestImpl.builder()
                .data(Map.of("header1", "validString with single quote can't", "header2", "firstRecordValue2", "header3", "firstRecordValue3"))
                .build();
        ConvertibleCollection testValidCollection = ConvertibleCollectionTestImpl.builder()
                .headers(List.of("header1", "header2", "header3"))
                .records(List.of(testFirstMessage))
                .build();
        ByteArrayOutputStream testBaos = new ByteArrayOutputStream();

        String expectedResult = "header1,header2,header3" + "\n"
                + "\"validString with single quote can't\",firstRecordValue2,firstRecordValue3" + "\n";
        csvConverter.convert(testValidCollection, testBaos);
        String actualResult = testBaos.toString();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void givenValidCollectionWithMultipleSingleQuotes_whenConvert_thenOutputStreamContainsValidCSVSurroundedByQuotes() {
        ConvertibleMessage testFirstMessage = ConvertibleMessageTestImpl.builder()
                .data(Map.of("header1", "validString with 'multiple single quotes'", "header2", "firstRecordValue2", "header3", "firstRecordValue3"))
                .build();
        ConvertibleCollection testValidCollection = ConvertibleCollectionTestImpl.builder()
                .headers(List.of("header1", "header2", "header3"))
                .records(List.of(testFirstMessage))
                .build();
        ByteArrayOutputStream testBaos = new ByteArrayOutputStream();

        String expectedResult = "header1,header2,header3" + "\n"
                + "\"validString with 'multiple single quotes'\",firstRecordValue2,firstRecordValue3" + "\n";
        csvConverter.convert(testValidCollection, testBaos);
        String actualResult = testBaos.toString();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void givenValidCollectionWithWhitespaceCharacter_whenConvert_thenOutputStreamContainsValidCSVWithSpaceInsteadOfWhiteSpaceCharacter() {
        ConvertibleMessage testFirstMessage = ConvertibleMessageTestImpl.builder()
                .data(Map.of("header1", "validString with\nwhitespace character", "header2", "firstRecordValue2", "header3", "firstRecordValue3"))
                .build();
        ConvertibleCollection testValidCollection = ConvertibleCollectionTestImpl.builder()
                .headers(List.of("header1", "header2", "header3"))
                .records(List.of(testFirstMessage))
                .build();
        ByteArrayOutputStream testBaos = new ByteArrayOutputStream();

        String expectedResult = "header1,header2,header3" + "\n"
                + "validString with whitespace character,firstRecordValue2,firstRecordValue3" + "\n";
        csvConverter.convert(testValidCollection, testBaos);
        String actualResult = testBaos.toString();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void givenValidCollectionWithMultipleWhitespaceCharacters_whenConvert_thenOutputStreamContainsValidCSVWithSpaceInsteadOfWhiteSpaceCharacters() {
        ConvertibleMessage testFirstMessage = ConvertibleMessageTestImpl.builder()
                .data(Map.of("header1", "validString with\na bunch of\traw string literals", "header2", "firstRecordValue2", "header3", "firstRecordValue3"))
                .build();
        ConvertibleCollection testValidCollection = ConvertibleCollectionTestImpl.builder()
                .headers(List.of("header1", "header2", "header3"))
                .records(List.of(testFirstMessage))
                .build();
        ByteArrayOutputStream testBaos = new ByteArrayOutputStream();

        String expectedResult = "header1,header2,header3" + "\n"
                + "validString with a bunch of raw string literals,firstRecordValue2,firstRecordValue3" + "\n";
        csvConverter.convert(testValidCollection, testBaos);
        String actualResult = testBaos.toString();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void givenInvalidCollectionWithNullHeader_whenConvert_thenThrowInvalidCollectionException() {
        Map<String, String> testData = new HashMap<>();
        testData.put(null, "testValue");
        List<String> testHeaders = new ArrayList<>();
        testHeaders.add(null);
        ConvertibleMessage testFirstMessage = ConvertibleMessageTestImpl.builder()
                .data(testData)
                .build();
        ConvertibleCollection testInvalidCollection = ConvertibleCollectionTestImpl.builder()
                .headers(testHeaders)
                .records(List.of(testFirstMessage))
                .build();

        Throwable actualException = assertThrows(InvalidCollectionException.class,
                () -> csvConverter.convert(testInvalidCollection, OutputStream.nullOutputStream()));
        assertEquals("Header cannot be null", actualException.getMessage());
    }

    @Test
    public void givenCollectionWithNullRecord_whenConvert_thenReturnEmptyString() {
        Map<String, String> testData = new HashMap<>();
        testData.put("testHeader1", null);
        testData.put("testHeader2", "testValue");
        List<String> testHeaders = new ArrayList<>();
        testHeaders.add("testHeader1");
        testHeaders.add("testHeader2");
        ConvertibleMessage testFirstMessage = ConvertibleMessageTestImpl.builder()
                .data(testData)
                .build();
        ConvertibleCollection testCollection = ConvertibleCollectionTestImpl.builder()
                .headers(testHeaders)
                .records(List.of(testFirstMessage))
                .build();

        ByteArrayOutputStream testBaos = new ByteArrayOutputStream();

        String expectedResult = "testHeader1,testHeader2" + "\n"
                + ",testValue" + "\n";
        csvConverter.convert(testCollection, testBaos);
        String actualResult = testBaos.toString();
        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void givenInvalidCollectionWithNullHeaders_whenConvert_thenThrowInvalidCollectionException() {
        Map<String, String> testData = new HashMap<>();
        ConvertibleMessage testFirstMessage = ConvertibleMessageTestImpl.builder()
                .data(testData)
                .build();
        ConvertibleCollection testInvalidCollection = ConvertibleCollectionTestImpl.builder()
                .headers(null)
                .records(List.of(testFirstMessage))
                .build();

        Throwable actualException = assertThrows(InvalidCollectionException.class,
                () -> csvConverter.convert(testInvalidCollection, OutputStream.nullOutputStream()));
        assertEquals("Provided headers can't be empty or null.", actualException.getMessage());
    }

    @Test
    public void givenInvalidCollectionWithNullRecords_whenConvert_thenThrowInvalidCollectionException() {
        List<String> testHeaders = new ArrayList<>();
        testHeaders.add("testHeader");
        ConvertibleCollection testInvalidCollection = ConvertibleCollectionTestImpl.builder()
                .headers(testHeaders)
                .records(null)
                .build();

        Throwable actualException = assertThrows(InvalidCollectionException.class,
                () -> csvConverter.convert(testInvalidCollection, OutputStream.nullOutputStream()));
        assertEquals("Provided records can't be null.", actualException.getMessage());
    }
}