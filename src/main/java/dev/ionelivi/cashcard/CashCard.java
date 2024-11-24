package dev.ionelivi.cashcard;

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
 * @param id The unique identifier for the cash card
 * @param amount The monetary value stored on the cash card
 */
record CashCard(Long id, Double amount) {
}
