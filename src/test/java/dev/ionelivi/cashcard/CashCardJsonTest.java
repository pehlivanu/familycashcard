package dev.ionelivi.cashcard;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for verifying JSON serialization and deserialization of the CashCard class. This class
 * uses Spring Boot's testing framework to validate JSON mapping behavior.
 *
 * @JsonTest - A Spring Boot test annotation that configures the JSON mapper (Jackson) and provides
 *           utilities for JSON testing without loading the full application context.
 */
@JsonTest
class CashCardJsonTest {

    /**
     * JacksonTester is a Spring Boot test utility that provides a convenient way to test JSON
     * serialization and deserialization. It wraps the Jackson ObjectMapper and provides
     * assertion methods specifically designed for JSON testing.
     * 
     * @see org.springframework.boot.test.json.JacksonTester
     * @see com.fasterxml.jackson.databind.ObjectMapper
     * 
     * @Autowired - Spring's dependency injection annotation that automatically instantiates and
     *            injects the JacksonTester instance.
     * @see org.springframework.beans.factory.annotation.Autowired
     */
    @Autowired
    private JacksonTester<CashCard> json;

    /**
     * Array to hold test CashCard instances used across multiple test methods.
     */
    private CashCard[] cashCards;
  
    /**
     * JacksonTester instance specifically for testing JSON serialization/deserialization
     * of CashCard arrays. This is used for testing bulk operations and list responses.
     * 
     * @see org.springframework.boot.test.json.JacksonTester
     */
    @Autowired
    private JacksonTester<CashCard[]> jsonList;
 
    /**
     * Initializes test data before each test method execution.
     * The {@code @BeforeEach} annotation ensures this method runs before every test,
     * providing a clean set of test data for each test case.
     * 
     * Uses {@code Arrays.array()} utility method to create an array with predefined
     * CashCard instances for testing purposes.
     * 
     * @see org.junit.jupiter.api.BeforeEach
     * @see org.assertj.core.util.Arrays#array
     */
    @BeforeEach
    void setUp() {
        cashCards = Arrays.array(
                new CashCard(99L, 123.45),
                new CashCard(100L, 100.00),
                new CashCard(101L, 150.00));
    }

    /**
     * Tests the JSON serialization of a CashCard object. Verifies that: 
     * 1. The serialized JSON matches an expected JSON file 
     * 2. The JSON contains the correct ID field and value 
     * 3. The JSON contains the correct amount field and value
     *
     * Uses AssertJ assertions with JacksonTester to verify JSON structure and content.
     *
     * @throws IOException if there are issues reading/writing JSON
     * @see org.springframework.boot.test.json.JacksonTester#write
     * @see org.assertj.core.api.Assertions#assertThat
     */
    @Test
    void cashCardSerializationTest() throws IOException {
        // Create a test CashCard instance with specific ID and amount values
        CashCard cashCard = cashCards[0];

        // Verify the JSON output exactly matches our single.json file
        assertThat(json.write(cashCard)).isStrictlyEqualToJson("single.json");

        // Verify the id field exists and is a number in the JSON
        assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.id");

        // Verify the id value in the JSON is exactly 99
        assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.id").isEqualTo(99);

        // Verify the amount field exists and is a number in the JSON
        assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.amount");

        // Verify the amount value in the JSON is exactly 123.45
        assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.amount").isEqualTo(123.45);
    }

    /**
     * Tests the JSON deserialization of a CashCard object.
     * Verifies that:
     * 1. A JSON string can be correctly parsed into a CashCard object
     * 2. The parsed object matches the expected CashCard instance
     * 3. Individual fields (id and amount) are correctly deserialized
     * 
     * Uses text blocks (Java 15+) for readable JSON test data and
     * JacksonTester's parse methods for deserialization.
     *
     * @Test - JUnit annotation marking this as a test method
     * @throws IOException if there are issues parsing the JSON string
     * @see org.springframework.boot.test.json.JacksonTester#parse
     * @see org.springframework.boot.test.json.JacksonTester#parseObject
     */
    @Test
    void cashCardDeserializationTest() throws IOException {
        // Define test JSON data with known values
        String expected = """
                {
                    "id":99,
                    "amount":123.45
                }
                """;

        // Verify the JSON string deserializes into an equivalent CashCard object
        assertThat(json.parse(expected)).isEqualTo(new CashCard(99L, 123.45));

        // Verify the id field is correctly deserialized to 99
        assertThat(json.parseObject(expected).id()).isEqualTo(99);

        // Verify the amount field is correctly deserialized to 123.45
        assertThat(json.parseObject(expected).amount()).isEqualTo(123.45);
    }

    /**
     * Tests the serialization of an array of CashCard objects to JSON.
     * Verifies that the serialized JSON array matches the expected format
     * defined in the list.json resource file.
     * 
     * This test ensures proper handling of bulk CashCard data serialization,
     * which is important for REST API responses returning multiple records.
     *
     * @throws IOException if there are issues reading/writing JSON
     * @see org.springframework.boot.test.json.JacksonTester#write
     */
    @Test
    void cashCardListSerializationTest() throws IOException {
        assertThat(jsonList.write(cashCards)).isStrictlyEqualToJson("list.json");
    }

    /**
     * Tests the deserialization of a JSON array into an array of CashCard objects.
     * Verifies that:
     * 1. A JSON array string can be correctly parsed into a CashCard array
     * 2. The parsed array matches the expected CashCard instances
     * 
     * Uses text blocks for readable JSON array test data and ensures
     * proper handling of bulk data deserialization scenarios.
     *
     * @throws IOException if there are issues parsing the JSON string
     * @see org.springframework.boot.test.json.JacksonTester#parse
     */
    @Test
    void cashCardListDeserializationTest() throws IOException {
        String expected = """
                [
                   { "id": 99, "amount": 123.45 },
                   { "id": 100, "amount": 100.00 },
                   { "id": 101, "amount": 150.00 }
                ]
                """;
        assertThat(jsonList.parse(expected)).isEqualTo(cashCards);
    }
}
