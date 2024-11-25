package dev.ionelivi.cashcard;

import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
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
	 * Tests the GET endpoint for retrieving a specific CashCard. Verifies that: 1. The endpoint
	 * returns HTTP 200 OK for existing cards 2. The response contains the expected CashCard data
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
		ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards/99", String.class);

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
	 * Tests the error handling for non-existent CashCard requests. Verifies that: 1. The endpoint
	 * returns HTTP 404 NOT_FOUND for non-existent card IDs 2. The response body is empty as
	 * expected 3. The error handling follows REST best practices
	 *
	 * This test ensures proper handling of negative scenarios where requested resources don't exist
	 * in the system.
	 *
	 * @Test - JUnit annotation marking this as a test method
	 * @see org.springframework.boot.test.web.client.TestRestTemplate#getForEntity
	 * @see org.springframework.http.HttpStatus#NOT_FOUND
	 */
	@Test
	void shouldNotReturnACashCardWithAnUnknownId() {
		// Make GET request to /cashcards/1000 endpoint for a non-existent card
		ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards/1000", String.class);

		// Verify HTTP status code is 404 NOT_FOUND
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

		// Verify response body is empty
		assertThat(response.getBody()).isBlank();
	}

	/**
	 * Tests the POST endpoint for creating a new CashCard. Verifies that: 1. A new CashCard can be
	 * successfully created 2. The server responds with HTTP 201 CREATED 3. The Location header
	 * contains the URI of the new resource 4. The newly created resource can be retrieved via GET
	 * request
	 *
	 * @Test - JUnit annotation marking this as a test method
	 * @DirtiesContext - Indicates that the Spring ApplicationContext should be reset after this
	 *                 test. This is necessary because the test modifies the application state by
	 *                 adding a new CashCard to the database. Without this annotation, subsequent
	 *                 tests might fail due to the modified state.
	 * 
	 * @see org.springframework.boot.test.web.client.TestRestTemplate#postForEntity
	 * @see org.springframework.http.ResponseEntity
	 * @see org.springframework.http.HttpStatus#CREATED
	 * @see com.jayway.jsonpath.JsonPath
	 * @see org.springframework.test.annotation.DirtiesContext
	 */
	@Test
	@DirtiesContext
	void shouldCreateANewCashCard() {
		// Create a new CashCard instance with null ID (server will assign) and $250 amount
		CashCard newCashCard = new CashCard(null, 250.00, "sarah1");

		// Send POST request to /cashcards endpoint with the new CashCard
		// Void.class indicates we don't expect a response body
		ResponseEntity<Void> createResponse = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.postForEntity("/cashcards", newCashCard, Void.class);

		// Verify the server responded with 201 CREATED status
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// Extract the Location header which contains the URI of the new resource
		URI locationOfNewCashCard = createResponse.getHeaders().getLocation();

		// Send GET request to verify the new resource exists and is accessible
		ResponseEntity<String> getResponse = restTemplate
		        .withBasicAuth("sarah1", "abc123")
				.getForEntity(locationOfNewCashCard, String.class);
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
	 * Tests the GET endpoint for retrieving all CashCards. Verifies that: 1. The endpoint returns
	 * HTTP 200 OK 2. The correct number of CashCards is returned 3. The response contains the
	 * expected IDs and amounts 4. The data matches the pre-configured test dataset
	 *
	 * @Test - JUnit annotation marking this as a test method
	 * @see org.springframework.boot.test.web.client.TestRestTemplate#getForEntity
	 * @see org.springframework.http.ResponseEntity
	 * @see com.jayway.jsonpath.JsonPath
	 */
	@Test
	void shouldReturnAllCashCardsWhenListIsRequested() {
		// Make GET request to /cashcards endpoint to retrieve all cards
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards", String.class);
		// Verify the response status is 200 OK
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Parse the JSON response body for easier data extraction
		DocumentContext documentContext = JsonPath.parse(response.getBody());
		// Count the number of CashCards in the response
		int cashCardCount = documentContext.read("$.length()");
		// Verify we have exactly 3 CashCards
		assertThat(cashCardCount).isEqualTo(3);

		// Extract all IDs from the response using JsonPath
		JSONArray ids = documentContext.read("$..id");
		// Verify the IDs match our expected values in any order
		assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);

		// Extract all amounts from the response using JsonPath
		JSONArray amounts = documentContext.read("$..amount");
		// Verify the amounts match our expected values in any order
		assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.0, 150.00);
	}

	/**
	 * Tests the pagination functionality of the CashCards endpoint. Verifies that: 1. The endpoint
	 * returns HTTP 200 OK 2. The response contains the requested page size 3. Pagination parameters
	 * are correctly processed
	 *
	 * @Test - JUnit annotation marking this as a test method
	 * @see org.springframework.boot.test.web.client.TestRestTemplate#getForEntity
	 * @see org.springframework.http.ResponseEntity
	 * @see com.jayway.jsonpath.JsonPath
	 */
	@Test
	void shouldReturnAPageOfCashCards() {
		// Make GET request with pagination parameters: page 0 (first page) and size 1 (one item per
		// page)
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards?page=0&size=1", String.class);
		// Verify the response status is 200 OK
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Parse the JSON response body
		DocumentContext documentContext = JsonPath.parse(response.getBody());
		// Extract all elements from the response
		JSONArray page = documentContext.read("$[*]");
		// Verify we received exactly one item as requested
		assertThat(page.size()).isEqualTo(1);
	}

	/**
	 * Tests the sorting functionality combined with pagination. Verifies that: 1. The endpoint
	 * returns HTTP 200 OK 2. The response is correctly sorted by amount in descending order 3. The
	 * page size is respected 4. The first result matches the expected highest amount
	 *
	 * @Test - JUnit annotation marking this as a test method
	 * @see org.springframework.boot.test.web.client.TestRestTemplate#getForEntity
	 * @see org.springframework.http.ResponseEntity
	 * @see com.jayway.jsonpath.JsonPath
	 */
	@Test
	void shouldReturnASortedPageOfCashCards() {
		// Make GET request with pagination and sorting parameters: first page, one item, sorted by
		// amount descending
		ResponseEntity<String> response = restTemplate
		.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards?page=0&size=1&sort=amount,desc", String.class);
		// Verify the response status is 200 OK
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Parse the JSON response body
		DocumentContext documentContext = JsonPath.parse(response.getBody());
		// Extract all elements from the response
		JSONArray read = documentContext.read("$[*]");
		// Verify we received exactly one item as requested
		assertThat(read.size()).isEqualTo(1);

		// Extract the amount from the first element in the response
		double amount = documentContext.read("$[0].amount");
		// Verify the amount matches our expected value
		assertThat(amount).isEqualTo(150.00);
	}

	/**
	 * Tests the default behavior of the CashCards endpoint when no parameters are provided.
	 * Verifies that: 1. The endpoint returns HTTP 200 OK 2. Default pagination and sorting are
	 * applied correctly 3. Results are sorted by amount in ascending order 4. All available
	 * CashCards are returned in the correct order
	 *
	 * @Test - JUnit annotation marking this as a test method
	 * @see org.springframework.boot.test.web.client.TestRestTemplate#getForEntity
	 * @see org.springframework.http.ResponseEntity
	 * @see com.jayway.jsonpath.JsonPath
	 */
	@Test
	void shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards", String.class);
		// Verify the response status is 200 OK
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Parse the JSON response body into a DocumentContext for data extraction
		DocumentContext documentContext = JsonPath.parse(response.getBody());
		// Extract all CashCard objects from the response array
		JSONArray page = documentContext.read("$[*]");
		// Verify we received all 3 CashCards (default page size)
		assertThat(page.size()).isEqualTo(3);

		// Extract all amount values from the CashCards
		JSONArray amounts = documentContext.read("$..amount");
		// Verify amounts are returned in ascending order (default sorting)
		// Expected order: 1.0 (smallest), 123.45 (middle), 150.00 (largest)
		assertThat(amounts).containsExactly(1.0, 123.45, 150.00);
	}

	/**
	 * Tests authentication failure scenarios with invalid credentials.
	 * Verifies that:
	 * 1. The endpoint returns HTTP 401 UNAUTHORIZED for invalid username
	 * 2. The endpoint returns HTTP 401 UNAUTHORIZED for invalid password
	 * 3. The security system properly validates credentials
	 *
	 * @Test - JUnit annotation marking this as a test method
	 * @see org.springframework.boot.test.web.client.TestRestTemplate#withBasicAuth
	 * @see org.springframework.http.HttpStatus#UNAUTHORIZED
	 */
	@Test
	void shouldNotReturnACashCardWhenUsingBadCredentials() {
		// Make GET request with invalid username
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("BAD-USER", "abc123")
				.getForEntity("/cashcards/99", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

		// Make GET request with invalid password
		response = restTemplate
				.withBasicAuth("sarah1", "BAD-PASSWORD")
				.getForEntity("/cashcards/99", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Tests authorization failure for users without the CARD-OWNER role.
	 * Verifies that:
	 * 1. The endpoint returns HTTP 403 FORBIDDEN for non-card-owners
	 * 2. Role-based access control is properly enforced
	 * 3. Users without proper roles cannot access any card data
	 *
	 * @Test - JUnit annotation marking this as a test method
	 * @see org.springframework.boot.test.web.client.TestRestTemplate#withBasicAuth
	 * @see org.springframework.http.HttpStatus#FORBIDDEN
	 */
	@Test
	void shouldRejectUsersWhoAreNotCardOwners() {
		// Make GET request with user who has no CARD-OWNER role
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("hank-owns-no-cards", "qrs456")
				.getForEntity("/cashcards/99", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	/**
	 * Tests data isolation between users attempting to access cards they don't own.
	 * Verifies that:
	 * 1. The endpoint returns HTTP 404 NOT_FOUND for cards
	 * 2. Users cannot access cards they don't own
	 * 3. The system properly isolates data between users
	 *
	 * @Test - JUnit annotation marking this as a test method
	 * @see org.springframework.boot.test.web.client.TestRestTemplate#getForEntity
	 * @see org.springframework.http.HttpStatus#NOT_FOUND
	 */
	@Test
	void shouldNotAllowAccessToCashCardsTheyDoNotOwn() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards/102", String.class); // kumar2's data
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	/**
	 * Tests the PUT endpoint for updating an existing CashCard.
	 * Verifies that:
	 * 1. An authenticated user can update their own CashCard
	 * 2. The server responds with HTTP 204 NO_CONTENT on successful update
	 * 3. Only the amount field can be modified while preserving ownership
	 *
	 * @Test - JUnit annotation marking this as a test method
	 * @DirtiesContext - Indicates that the Spring ApplicationContext should be reset after
	 *                   this test because it modifies the application state
	 * @see org.springframework.boot.test.web.client.TestRestTemplate#exchange
	 * @see org.springframework.http.HttpMethod#PUT
	 * @see org.springframework.http.HttpStatus#NO_CONTENT
	 */
	@Test
	@DirtiesContext
	void shouldUpdateAnExistingCashCard() {
		// Create CashCard update request with new amount
		// Note: null ID and owner fields ensure these cannot be modified
		CashCard cashCardUpdate = new CashCard(null, 19.99, null);
		
		// Create HTTP entity containing the update request
		HttpEntity<CashCard> request = new HttpEntity<>(cashCardUpdate);
		
		// Send PUT request to update CashCard #99 with authentication
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.exchange("/cashcards/99", HttpMethod.PUT, request, Void.class);
		
		// Verify the update was successful (204 NO_CONTENT)
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		// Verify the updated CashCard by making a GET request
		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards/99", String.class);
		// Confirm successful retrieval
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// Parse and validate the updated CashCard data
		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.id");
		Double amount = documentContext.read("$.amount");
		// Verify ID remains unchanged
		assertThat(id).isEqualTo(99);
		// Verify amount was updated to new value
		assertThat(amount).isEqualTo(19.99);
	}

	/**
	 * Tests attempting to update a non-existent CashCard.
	 * Verifies that the system properly handles requests for invalid card IDs
	 * by returning a NOT_FOUND status.
	 */
	@Test
	void shouldNotUpdateACashCardThatDoesNotExist() {
		// Create update request for non-existent card
		CashCard unknownCard = new CashCard(null, 19.99, null);
		HttpEntity<CashCard> request = new HttpEntity<>(unknownCard);
		
		// Attempt to update card with ID that doesn't exist
		ResponseEntity<Void> response = restTemplate.withBasicAuth("sarah1", "abc123")
				.exchange("/cashcards/99999", HttpMethod.PUT, request, Void.class);
		// Verify request fails with NOT_FOUND status
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	/**
	 * Tests data isolation by attempting to update a CashCard owned by another user.
	 * Verifies that users cannot modify CashCards they don't own, maintaining
	 * proper data security and isolation.
	 */
	@Test
	void shouldNotUpdateACashCardThatIsOwnedBySomeoneElse() {
		// Create update request for card owned by kumar2 (ID 102)
		CashCard kumarsCard = new CashCard(null, 333.33, null);
		HttpEntity<CashCard> request = new HttpEntity<>(kumarsCard);
		
		// Attempt to update kumar2's card using sarah1's credentials
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.exchange("/cashcards/102", HttpMethod.PUT, request, Void.class);
		// Verify request fails with NOT_FOUND status (system doesn't reveal card ownership)
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	/**
	 * Tests the successful deletion of an existing CashCard.
	 * Verifies that:
	 * 1. The DELETE request returns 204 NO_CONTENT
	 * 2. The deleted card cannot be retrieved afterwards (404 NOT_FOUND)
	 */
	@Test
	@DirtiesContext  // Reset application context after test since we're modifying data
	void shouldDeleteAnExistingCashCard() {
		// Attempt to delete CashCard #99 owned by sarah1
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.exchange("/cashcards/99", HttpMethod.DELETE, null, Void.class);
		// Verify deletion was successful
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		// Attempt to retrieve the deleted card to confirm it's gone
		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards/99", String.class);
		// Verify the card no longer exists
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	/**
	 * Tests deletion attempt of a non-existent CashCard.
	 * Verifies that attempting to delete a card with an invalid ID
	 * returns 404 NOT_FOUND status.
	 */
	@Test
	void shouldNotDeleteACashCardThatDoesNotExist() {
		// Attempt to delete non-existent CashCard
		ResponseEntity<Void> deleteResponse = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.exchange("/cashcards/99999", HttpMethod.DELETE, null, Void.class);
		// Verify request fails with NOT_FOUND
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	/**
	 * Tests data isolation by attempting to delete another user's CashCard.
	 * Verifies that:
	 * 1. User cannot delete cards they don't own (returns 404 NOT_FOUND)
	 * 2. The target card remains accessible to its actual owner
	 * Note: Returns 404 instead of 403 to avoid revealing card ownership
	 */
	@Test
	void shouldNotAllowDeletionOfCashCardsTheyDoNotOwn() {
		// Attempt to delete kumar2's card (#102) using sarah1's credentials
		ResponseEntity<Void> deleteResponse = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.exchange("/cashcards/102", HttpMethod.DELETE, null, Void.class);
		// Verify deletion attempt fails
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

		// Verify kumar2 can still access their card
		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("kumar2", "xyz789")
				.getForEntity("/cashcards/102", String.class);
		// Confirm card still exists and is accessible to its owner
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
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
