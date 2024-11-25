package dev.ionelivi.cashcard;

import org.springframework.data.annotation.Id;


/**
 * Represents a cash card with an identifier and monetary amount. This immutable record provides a
 * simple data structure for cash card information.
 * 
 * <p>
  * As a record (introduced in Java 16),{@link Record} feature which automatically provides:
 * <ul>
 * <li>{@link Record#toString()} - Generates string representation</li>
 * <li>{@link Record#equals(Object)} - Implements value-based equality</li>
 * <li>{@link Record#hashCode()} - Consistent with equals()</li>
 * <li>Private, final fields with public accessors ({@link Record#components()})</li>
 * </ul>
 * </p>
 * 
 * <p>
 * The record components are:
 * <ul>
 * <li>{@code id} - Unique identifier, maps to database primary key</li>
 * <li>{@code amount} - Monetary value as {@link Double}</li>
 * <li>{@code owner} - The owner of the cash card</li>
 * </ul>
 * </p>
 *
 * @see Record - Java's immutable data carrier type
 * @see Id - Spring Data's primary key marker
 * 
 * @param id The unique identifier for the cash card. Marked with {@link Id} to designate it as the primary
 *        key for database persistence. This ensures each cash card has a unique identifier, similar
 *        to how every credit card has a unique number. The {@link Id} annotation is required for JPA
 *        entity mapping and typically corresponds to the primary key column in the database table.
 * @param amount The monetary value stored on the cash card
 * @param owner The username or identifier of the person who owns this cash card
 */
record CashCard(@Id Long id, Double amount, String owner) {
}

