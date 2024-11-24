package dev.ionelivi.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for handling CashCard-related HTTP requests.
 * Provides endpoints for managing CashCard resources in the application.
 *
 * @RestController - Combines @Controller and @ResponseBody, indicating this class handles
 *                  REST requests and the return values should be written directly to the
 *                  HTTP response body (automatically converted to JSON/XML)
 * 
 * @RequestMapping - Maps all endpoints in this controller to start with "/cashcards"
 *                  This creates a base URL path for all methods in this controller
 */
@RestController
@RequestMapping("/cashcards")
class CashCardController {

    /**
     * Retrieves a specific CashCard by its ID.
     * Currently implements a simple mock response for demonstration purposes,
     * returning a hardcoded CashCard for ID 99 and 404 for all other IDs.
     *
     * @GetMapping - Maps HTTP GET requests to this method for path "/{requestedId}"
     *               The full path will be "/cashcards/{requestedId}"
     * 
     * @PathVariable - Binds the URL path variable {requestedId} to the method parameter
     *
     * @param requestedId the ID of the requested CashCard
     * @return ResponseEntity<CashCard> with either:
     *         - 200 OK and the CashCard if found
     *         - 404 Not Found if the CashCard doesn't exist
     */
    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        if (requestedId.equals(99L)) {
            CashCard cashCard = new CashCard(99L, 123.45);
            return ResponseEntity.ok(cashCard);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
