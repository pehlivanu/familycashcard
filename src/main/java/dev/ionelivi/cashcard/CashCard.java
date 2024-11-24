package dev.ionelivi.cashcard;

import org.springframework.data.annotation.Id;


/**
 * Represents a cash card with an identifier and monetary amount. This immutable record provides a
 * simple data structure for cash card information.
 * 
 * <p>
 * As a record (introduced in Java 16), this class automatically provides:
 * <ul>
 * <li>A canonical constructor accepting all fields</li>
 * <li>Private, final fields for all components</li>
 * <li>Public accessor methods for all components</li>
 * <li>Implementation of equals(), hashCode(), and toString()</li>
 * <li>Immutability by default</li>
 * </ul>
 * </p>
 * 
 * @param id The unique identifier for the cash card. Marked with @Id to designate it as the primary
 *        key for database persistence. This ensures each cash card has a unique identifier, similar
 *        to how every credit card has a unique number. The @Id annotation is required for JPA
 *        entity mapping and typically corresponds to the primary key column in the database table.
 * @param amount The monetary value stored on the cash card
 */
record CashCard(Long id, Double amount) {
}
