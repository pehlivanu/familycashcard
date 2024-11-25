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
    CashCard findByIdAndOwner(Long id, String owner);
    Page<CashCard> findByOwner(String owner, PageRequest pageRequest);
}
