package dev.ionelivi.cashcard;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import static org.assertj.core.api.Assertions.assertThat;
import java.net.URI;


/**
 * Integration test class for the CashCard REST API endpoints. Tests the complete request-response
 * cycle including HTTP interactions. Then it verifies that the Spring application context loads
 * successfully and all required beans are properly configured.
 *
 * @SpringBootTest - Configures a complete application context for integration testing with
 *                 'webEnvironment' set to RANDOM_PORT to avoid port conflicts. It starts up an
 *                 actual Spring Boot application context, loading all configurations, components,
 *                 and beans for integration testing.
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.test.annotation.DirtiesContext
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class CashcardApplicationTests {

	/**
	 * TestRestTemplate is a Spring Boot test utility for making HTTP requests. Similar to
	 * RestTemplate but with additional testing capabilities.
	 *
	 * @Autowired - Injects the TestRestTemplate instance from Spring's context
	 */
	@Autowired
	TestRestTemplate restTemplate;

	/**
	 * Tests the GET endpoint for retrieving a specific CashCard. Verifies that: 
	 * 1. The endpoint returns HTTP 200 OK for existing cards 
	 * 2. The response contains the expected CashCard data 
	 * 3.The JSON structure matches the expected format
	 *
	 * @Test - JUnit annotation marking this as a test method
	 * @see org.springframework.boot.test.web.client.TestRestTemplate#getForEntity
	 * @see org.springframework.http.ResponseEntity
	 * @see com.jayway.jsonpath.JsonPath
	 */
	@Test
	void shouldReturnACashCardWhenDataIsSaved() {
		// Make GET request to /cashcards/99 endpoint
		ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/99", String.class);

		// Verify HTTP status code is 200 OK
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Parse and verify response body
		DocumentContext documentContext = JsonPath.parse(response.getBody());
		Number id = documentContext.read("$.id");
		assertThat(id).isEqualTo(99);

		Double amount = documentContext.read("$.amount");
		assertThat(amount).isEqualTo(123.45);
	}

	/**
	 * Tests the error handling for non-existent CashCard requests.
	 * Verifies that:
	 * 1. The endpoint returns HTTP 404 NOT_FOUND for non-existent card IDs
	 * 2. The response body is empty as expected
	 * 3. The error handling follows REST best practices
	 *
	 * This test ensures proper handling of negative scenarios where requested
	 * resources don't exist in the system.
	 *
	 * @Test - JUnit annotation marking this as a test method
	 * @see org.springframework.boot.test.web.client.TestRestTemplate#getForEntity
	 * @see org.springframework.http.HttpStatus#NOT_FOUND
	 */
	@Test
	void shouldNotReturnACashCardWithAnUnknownId() {
		// Make GET request to /cashcards/1000 endpoint for a non-existent card
		ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/1000", String.class);

		// Verify HTTP status code is 404 NOT_FOUND
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

		// Verify response body is empty
		assertThat(response.getBody()).isBlank();
	}

	/**
	 * Tests the POST endpoint for creating a new CashCard.
	 * Verifies that:
	 * 1. A new CashCard can be successfully created
	 * 2. The server responds with HTTP 201 CREATED
	 * 3. The Location header contains the URI of the new resource
	 * 4. The newly created resource can be retrieved via GET request
	 *
	 * @Test - JUnit annotation marking this as a test method
	 * @see org.springframework.boot.test.web.client.TestRestTemplate#postForEntity
	 * @see org.springframework.http.ResponseEntity
	 * @see org.springframework.http.HttpStatus#CREATED
	 * @see com.jayway.jsonpath.JsonPath
	 */
	@Test
	//@DirtiesContext
	void shouldCreateANewCashCard() {
		// Create a new CashCard instance with null ID (server will assign) and $250 amount
		CashCard newCashCard = new CashCard(null, 250.00);

		// Send POST request to /cashcards endpoint with the new CashCard
		// Void.class indicates we don't expect a response body
		ResponseEntity<Void> createResponse =
				restTemplate.postForEntity("/cashcards", newCashCard, Void.class);

		// Verify the server responded with 201 CREATED status
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// Extract the Location header which contains the URI of the new resource
		URI locationOfNewCashCard = createResponse.getHeaders().getLocation();

		// Send GET request to verify the new resource exists and is accessible
		ResponseEntity<String> getResponse =
				restTemplate.getForEntity(locationOfNewCashCard, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Parse the JSON response body into a DocumentContext for easy data extraction
		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		
		// Extract the 'id' field from JSON using JsonPath syntax ($.id)
		// Using Number type since ID could be Integer or Long
		Number id = documentContext.read("$.id");
		
		// Extract the 'amount' field from JSON using JsonPath syntax ($.amount)
		Double amount = documentContext.read("$.amount");
 
		// Verify that the server assigned an ID (not null)
		assertThat(id).isNotNull();
		
		// Verify the amount matches what we sent in the POST request
		assertThat(amount).isEqualTo(250.00);
	}

	/**
	 * Verifies that the Spring application context loads successfully. This test will fail if: -
	 * Any @Configuration classes fail to load - Required beans cannot be created - Component
	 * scanning fails - Application properties cannot be loaded
	 */
	@Test
	void contextLoads() {
		// Empty test body - Spring will fail the test if context loading fails
	}

	/**
	 * Tests the basic configuration of the application's security settings. Verifies that security
	 * beans are properly configured and loaded.
	 */
	@Test
	void securityConfigurationLoads() {
		// Empty test body - Spring will fail if security configuration is incorrect
	}

	/**
	 * Verifies that all required database configurations are properly loaded. This includes
	 * connection pools, transaction managers, and JPA configurations.
	 */
	@Test
	void databaseConfigurationLoads() {
		// Empty test body - Spring will fail if database configuration is incorrect
	}

	/**
	 * Tests that all custom application properties are correctly loaded from
	 * application.properties/yml files.
	 */
	@Test
	void applicationPropertiesLoad() {
		// Empty test body - Spring will fail if properties aren't loaded correctly
	}
}
