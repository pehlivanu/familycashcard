package dev.ionelivi.cashcard;

/**
 * Enum representing the different user roles in the CashCard application.
 * <p>
 * This enum is used to define the roles that can be assigned to users
 * for access control within the application.
 * </p>
 */
public enum UserRoles {
    /**
     * Role for users who own cash cards.
     */
    CARD_OWNER,

    /**
     * Role for users who do not own cash cards.
     */
    NON_OWNER;

    /**
     * Returns the name of the role as a string.
     *
     * @return the name of the enum constant
     */
    public String role() {
        return name();  // Return the name of the enum constant
    }
} 