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
     * @param principal {@link java.security.Principal} the authenticated user's principal containing ownership information
     * @return {@link ResponseEntity} containing:
     *         <ul>
     *         <li>200 OK with the CashCard if found (via {@link ResponseEntity#ok})</li>
     *         <li>404 Not Found if no CashCard exists (via {@link ResponseEntity#notFound})</li>
     *         </ul>
     * 
     * @see ResponseEntity#ok(Object) - Creates response with 200 status and body
     * @see ResponseEntity#notFound() - Creates builder for 404 response
     */
    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {

        // Query the repository for the CashCard by ID and owner
        CashCard cashCard = findCashCard(requestedId, principal.getName());
        if (cashCard != null) {
            // If CashCard is found, return it with 200 OK status
            return ResponseEntity.ok(cashCard);
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
     * @param principal {@link java.security.Principal} the authenticated user's principal for setting card ownership
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
        // Create a new CashCard with the owner set to the authenticated user's principal
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

    /**
     * Retrieves all CashCards owned by the authenticated user with pagination and sorting support.
     *
     * @param pageable {@link org.springframework.data.domain.Pageable} contains pagination
     * @param principal the authenticated user's principal for filtering owned cards
     * @return {@link ResponseEntity} containing:
     *         <ul>
     *         <li>200 OK with the list of CashCards if found</li>
     *         <li>Empty list if no CashCards exist for the user</li>
     *         </ul>
     */
    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        // Query the repository for CashCards by owner, with pagination and sorting support
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(), PageRequest.of(
                //
                pageable.getPageNumber(),
                // Number of items per page
                pageable.getPageSize(),
                // Sort by amount in ascending order if not specified
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))));
        // Return 200 OK with the list of CashCards
        return ResponseEntity.ok(page.getContent());
    }

    /**
     * Updates an existing CashCard with new data while preserving ownership.
     * 
     * <p>
     * This endpoint handles PUT requests to modify CashCard records. It ensures:
     * <ul>
     * <li>Only the card owner can perform updates</li>
     * <li>The ID and owner cannot be modified</li>
     * <li>Only the amount field can be updated</li>
     * </ul>
     * </p>
     *
     * @param requestedId the ID of the CashCard to update
     * @param cashCardUpdate the new CashCard data from request body
     * @param principal the authenticated user's principal containing ownership information
     * @return {@link ResponseEntity} containing:
     *         <ul>
     *         <li>204 NO_CONTENT if update successful</li>
     *         <li>404 NOT_FOUND if card doesn't exist or user doesn't own it</li>
     *         </ul>
     */
    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putCashCard(@PathVariable Long requestedId,
            @RequestBody CashCard cashCardUpdate, Principal principal) {
        // First check if the card exists and belongs to the user
        CashCard existingCard = findCashCard(requestedId, principal.getName());
        if (existingCard == null) {
            // Return 404 if card not found or user doesn't own it
            return ResponseEntity.notFound().build();
        }

        // Create updated card preserving ID and owner, only updating the amount
        CashCard updatedCashCard = new CashCard(existingCard.id(), // Preserve original ID
                cashCardUpdate.amount(), // Update amount from request
                principal.getName() // Preserve original owner
        );

        // Save the updated card and return 204 No Content
        cashCardRepository.save(updatedCashCard);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a CashCard if it exists and belongs to the authenticated user.
     * 
     * <p>
     * This endpoint handles DELETE requests to remove CashCard records. It ensures:
     * <ul>
     * <li>The card exists before attempting deletion</li>
     * <li>Only the owner can delete their own cards</li>
     * <li>Returns 404 for non-existent cards or unauthorized access attempts</li>
     * </ul>
     * </p>
     *
     * @param id the ID of the CashCard to delete
     * @param principal the authenticated user's principal containing ownership information
     * @return {@link ResponseEntity} containing:
     *         <ul>
     *         <li>204 NO_CONTENT if deletion successful</li>
     *         <li>404 NOT_FOUND if card doesn't exist or user doesn't own it</li>
     *         </ul>
     */
    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long id, Principal principal) {
        // Check if card exists AND belongs to the authenticated user
        if (cashCardRepository.existsByIdAndOwner(id, principal.getName())) {
            // If authorized, delete the card
            cashCardRepository.deleteById(id);
            // Return 204 No Content to indicate successful deletion
            return ResponseEntity.noContent().build();
        }
        // Return 404 Not Found if card doesn't exist or user doesn't own it
        // (avoiding information disclosure about card ownership)
        return ResponseEntity.notFound().build();
    }

    /**
     * Helper method to find a CashCard by ID and owner. Centralizes the card lookup logic to avoid
     * repetition and ensure consistent authorization checks across endpoints.
     *
     * @param id the unique identifier of the requested CashCard
     * @param owner the username of the authenticated user
     * @return the matching CashCard if found and owned by the user, null otherwise
     * @see CashCardRepository#findByIdAndOwner
     */
    private CashCard findCashCard(Long id, String owner) {
        // Query the repository for the CashCard by ID and owner
        CashCard cashCard = cashCardRepository.findByIdAndOwner(id, owner);
        return cashCard;
    }

}
