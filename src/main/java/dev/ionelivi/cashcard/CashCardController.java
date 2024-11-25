package dev.ionelivi.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.*;

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
     * This endpoint handles GET requests to fetch individual CashCard records. It uses the
     * CashCardRepository to query the database and returns an appropriate HTTP response based on
     * whether the card was found.
     * </p>
     *
     * @param requestedId the ID of the requested CashCard
     * @return {@link ResponseEntity} containing:
     *         <ul>
     *         <li>200 OK with the CashCard if found (via {@link ResponseEntity#ok})</li>
     *         <li>404 Not Found if no CashCard exists (via {@link ResponseEntity#notFound})</li>
     *         </ul>
     * 
     * @see Optional#isPresent() - Checks if value exists
     * @see Optional#get() - Retrieves the value if present
     * @see ResponseEntity#ok(Object) - Creates response with 200 status and body
     * @see ResponseEntity#notFound() - Creates builder for 404 response
     */
    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
        // Query the repository for the CashCard, returns Optional to handle null case
        Optional<CashCard> cashCardOptional = Optional
                .ofNullable(cashCardRepository.findByIdAndOwner(requestedId, principal.getName()));
        
        // If CashCard is found, return it with 200 OK status
        if (cashCardOptional.isPresent()) {
            return ResponseEntity.ok(cashCardOptional.get());
        } else {
            // If no CashCard found, return 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Creates a new CashCard resource.
     * 
     * <p>
     * This endpoint handles POST requests to create new CashCard records. It saves the provided
     * CashCard data to the repository and returns the location of the newly created resource
     * in the Location header.
     * </p>
     *
     * @param newCashCardRequest the CashCard data to create, provided in request body
     * @param ucb {@link UriComponentsBuilder} used to construct the resource URI:
     *        <ul>
     *        <li>{@link UriComponentsBuilder#path(String)} - Adds path segment</li>
     *        <li>{@link UriComponentsBuilder#buildAndExpand(Object...)} - Replaces path variables</li>
     *        </ul>
     * @return {@link ResponseEntity} with:
     *         <ul>
     *         <li>201 Created status (via {@link ResponseEntity#created(URI)})</li>
     *         <li>Location header containing {@link URI} of new resource</li>
     *         <li>No response body (Void)</li>
     *         </ul>
     * 
     * @see URI - Represents the location of the new resource
     * @see ResponseEntity#created(URI) - Creates response with 201 status and Location header
     */
    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest,
            UriComponentsBuilder ucb, Principal principal) {
        CashCard cashCardWithOwner =
                new CashCard(null, newCashCardRequest.amount(), principal.getName());
    
  
        // Save the new CashCard to the repository
        CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);
        
        // Build the URI for the new resource: /cashcards/{id}
        URI locationOfNewCashCard =
                ucb.path("cashcards/{id}").buildAndExpand(savedCashCard.id()).toUri();
        
        // Return 201 Created status with Location header
        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
            PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(), 
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
            ));
        return ResponseEntity.ok(page.getContent());
    }

}
