package dev.ionelivi.cashcard;

import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for handling CashCard-related HTTP requests. Provides endpoints for managing
 * CashCard resources in the application.
 *
 * @RestController - Combines @Controller and @ResponseBody, indicating this class handles REST
 *                 requests and the return values should be written directly to the HTTP response
 *                 body (automatically converted to JSON/XML)
 * 
 * @RequestMapping - Maps all endpoints in this controller to start with "/cashcards" This creates a
 *                 base URL path for all methods in this controller
 */
@RestController
@RequestMapping("/cashcards")
class CashCardController {

    /** Repository for handling CashCard persistence operations */
    private final CashCardRepository cashCardRepository;

    /**
     * Constructs a new CashCardController with the specified repository.
     * 
     * <p>
     * This constructor is automatically used by Spring's dependency injection system to inject the
     * appropriate CashCardRepository instance when creating the controller.
     * </p>
     *
     * @param cashCardRepository the repository to handle CashCard data operations
     */
    private CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    /**
     * Retrieves a specific CashCard by its ID from the repository.
     * 
     * <p>
     * This endpoint handles GET requests to fetch individual CashCard records.
     * It uses the CashCardRepository to query the database and returns an
     * appropriate HTTP response based on whether the card was found.
     * </p>
     *
     * @param requestedId the ID of the requested CashCard
     * @return ResponseEntity containing:
     *         <ul>
     *         <li>200 OK with the CashCard if found</li>
     *         <li>404 Not Found if no CashCard exists with the given ID</li>
     *         </ul>
     * 
     * @see CashCardRepository#findById(Long)
     */
    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);
        if (cashCardOptional.isPresent()) {
            return ResponseEntity.ok(cashCardOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
