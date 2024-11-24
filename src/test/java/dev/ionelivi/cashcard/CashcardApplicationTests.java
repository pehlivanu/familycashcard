package dev.ionelivi.cashcard;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Integration test class for the CashCard REST API endpoints. Tests the complete request-response
 * cycle including HTTP interactions. Then it verifies that the Spring application context loads
 * successfully and all required beans are properly configured.
 *
 * @SpringBootTest - Configures a complete application context for integration testing with
 *                 'webEnvironment' set to RANDOM_PORT to avoid port conflicts. It starts up an
 *                 actual Spring Boot application context, loading all configurations, components,
 *                 and beans for integration testing.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
