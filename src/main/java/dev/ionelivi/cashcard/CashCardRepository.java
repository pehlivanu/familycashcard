package dev.ionelivi.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository interface for {@link CashCard} entities.
 * <p>
 * This interface extends the {@link CrudRepository} interface provided by Spring Data JPA,
 * which automatically generates the implementation for common CRUD (Create, Read, Update, Delete)
 * operations on the {@link CashCard} entity.
 * <p>
 * By extending {@link CrudRepository}, this interface inherits the following methods:
 * <ul>
 *     <li>{@link CrudRepository#save(Object)}</li>
 *     <li>{@link CrudRepository#saveAll(Iterable)}</li>
 *     <li>{@link CrudRepository#findById(Object)}</li>
 *     <li>{@link CrudRepository#existsById(Object)}</li>
 *     <li>{@link CrudRepository#findAll()}</li>
 *     <li>{@link CrudRepository#findAllById(Iterable)}</li>
 *     <li>{@link CrudRepository#count()}</li>
 *     <li>{@link CrudRepository#deleteById(Object)}</li>
 *     <li>{@link CrudRepository#delete(Object)}</li>
 *     <li>{@link CrudRepository#deleteAllById(Iterable)}</li>
 *     <li>{@link CrudRepository#deleteAll(Iterable)}</li>
 *     <li>{@link CrudRepository#deleteAll()}</li>
 * </ul>
 * <p>
 * Spring Data JPA will generate the implementation of this interface at runtime, based on the
 * entity class and the methods defined in this interface.
 */
interface CashCardRepository extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long> {
    
    /**
     * Retrieves a specific CashCard by both its ID and owner.
     * <p>
     * This method is necessary for implementing security and data isolation in the application.
     * It provides the following benefits:
     * <ul>
     *   <li>Ensures users can only access their own CashCards by requiring owner verification</li>
     *   <li>Replaces the default findById method with a secure, owner-based alternative</li>
     *   <li>Prevents unauthorized access to CashCards owned by other users</li>
     *   <li>Simplifies authorization checks in the service layer</li>
     * </ul>
     *
     * @param id the unique identifier of the CashCard
     * @param owner the username of the CashCard owner
     * @return the matching CashCard if found, or null if no CashCard exists with the given ID and owner
     */
    CashCard findByIdAndOwner(Long id, String owner);

    /**
     * Retrieves a page of CashCards belonging to a specific owner.
     * <p>
     * This method is necessary for implementing the list/search functionality with proper security
     * and performance considerations. It:
     * <ul>
     *   <li>Ensures users can only view their own cards by filtering by owner</li>
     *   <li>Supports pagination to prevent memory issues with large datasets</li>
     *   <li>Enables sorting capabilities for better user experience</li>
     *   <li>Replaces the default findAll method with a secure, owner-based alternative</li>
     * </ul>
     *
     * @param owner the username of the CashCard owner
     * @param pageRequest contains pagination parameters (page number, size) and sorting criteria
     * @return a {@link Page} containing the CashCards owned by the specified user
     */
    Page<CashCard> findByOwner(String owner, PageRequest pageRequest);
}
