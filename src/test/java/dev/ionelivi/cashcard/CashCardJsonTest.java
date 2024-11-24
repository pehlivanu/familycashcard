package dev.ionelivi.cashcard;

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
     * serialization and deserialization.
     * 
     * @Autowired - Spring's dependency injection annotation that automatically instantiates and
     *            injects the JacksonTester instance.
     */
    @Autowired
    private JacksonTester<CashCard> json;

    /**
     * Tests the JSON serialization of a CashCard object. Verifies that: 
     * 1. The serialized JSON matches an expected JSON file 
     * 2. The JSON contains the correct ID field and value 
     * 3. The JSON contains the correct amount field and value
     *
     * @Test - JUnit annotation marking this as a test method
     * @throws IOException if there are issues reading/writing JSON
     */
    @Test
    void cashCardSerializationTest() throws IOException {
        // Create a test CashCard instance with specific ID and amount values
        CashCard cashCard = new CashCard(99L, 123.45);

        // Verify the JSON output exactly matches our expected.json file
        assertThat(json.write(cashCard)).isStrictlyEqualToJson("expected.json");

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
     * This test verifies that:
     * 1. A JSON string can be correctly parsed into a CashCard object
     * 2. The parsed object matches the expected CashCard instance
     * 3. Individual fields (id and amount) are correctly deserialized
     * 
     * The test uses a multi-line JSON string (text block) as input data
     * and verifies both object equality and individual field values.
     *
     * @Test - JUnit annotation marking this as a test method
     * @throws IOException if there are issues parsing the JSON string
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
}
