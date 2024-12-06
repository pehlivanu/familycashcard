package dev.ionelivi.cashcard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration class for the CashCard application.
 * <p>
 * This class configures Spring Security settings including:
 * <ul>
 * <li>HTTP security configuration</li>
 * <li>Password encoding</li>
 * <li>User details and roles for testing</li>
 * </ul>
 * 
 * @Configuration annotation is necessary to:
 *                <ul>
 *                <li>Mark this class as a source of bean definitions</li>
 *                <li>Enable component scanning for this configuration</li>
 *                <li>Allow Spring to process the configuration during application startup</li>
 *                </ul>
 */
@Configuration
class SecurityConfig {

    /**
     * Configures the security filter chain for HTTP requests.
     * <p>
     * This method defines:
     * <ul>
     * <li>URL patterns that require authentication</li>
     * <li>Role-based access control for endpoints</li>
     * <li>Basic HTTP authentication</li>
     * <li>CSRF protection settings</li>
     * </ul>
     * <p>
     * The @Bean annotation is necessary to:
     * <ul>
     * <li>Register the SecurityFilterChain in Spring's application context</li>
     * <li>Make the security configuration available to the Spring Security framework</li>
     * <li>Ensure this configuration is used during request processing</li>
     * </ul>
     *
     * @param http {@link HttpSecurity} the object to configure security settings
     * @return {@link SecurityFilterChain} the configured security filter chain
     * @throws Exception if configuration fails
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Configure security for all /cashcards endpoints
        http.authorizeHttpRequests(request -> request.requestMatchers("/cashcards/**") 
                .hasRole(UserRoles.CARD_OWNER.role())) // Only users with CARD-OWNER role can access
                .httpBasic(Customizer.withDefaults()) // Enable HTTP Basic authentication
                .csrf(csrf -> csrf.disable()); // Disable CSRF protection for simplicity in this
                                               // demo
        return http.build(); // Build and return the security configuration
    }

    /**
     * Configures the password encoder for the application.
     * <p>
     * Uses BCrypt hashing algorithm for secure password storage.
     * <p>
     * The @Bean annotation is necessary to:
     * <ul>
     * <li>Make the PasswordEncoder available for dependency injection</li>
     * <li>Ensure a single, shared instance is used throughout the application</li>
     * <li>Allow Spring Security to use this encoder for password verification</li>
     * </ul>
     *
     * @return {@link PasswordEncoder} configured to use BCrypt
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCrypt for password hashing
    }

    /**
     * Creates test users for development and testing purposes.
     * <p>
     * Configures two users:
     * <ul>
     * <li>sarah1: A user with CARD-OWNER role who can access cards</li>
     * <li>hank-owns-no-cards: A user with NON-OWNER role who cannot access cards</li>
     * </ul>
     * <p>
     * The @Bean annotation is necessary to:
     * <ul>
     * <li>Register the UserDetailsService in Spring's security context</li>
     * <li>Enable authentication using the configured test users</li>
     * <li>Make the user details available for Spring Security's authentication process</li>
     * <li>Ensure proper initialization order with other security components</li>
     * </ul>
     *
     * @param passwordEncoder {@link PasswordEncoder} used to hash user passwords
     * @return {@link UserDetailsService} containing the test users
     */
    @Bean
    UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
        User.UserBuilder users = User.builder(); // Get a user builder to create user details

        // Create a user 'sarah1' with CARD-OWNER role
        UserDetails sarah = users.username("sarah1") // Set username
                .password(passwordEncoder // Hash the password
                .encode("abc123")) // Original password
                .roles(UserRoles.CARD_OWNER.role()) // Assign CARD-OWNER role
                .build(); // Create the UserDetails object

        // Create a user 'hank-owns-no-cards' with NON-OWNER role
        UserDetails hankOwnsNoCards = users.username("hank-owns-no-cards") // Set username
                .password(passwordEncoder // Hash the password
                .encode("qrs456")) // Original password
                .roles(UserRoles.NON_OWNER.role()) // Assign NON-OWNER role
                .build(); // Create the UserDetails object

        UserDetails kumar = users.username("kumar2") // Set username
                .password(passwordEncoder.encode("xyz789")) // Hash the password
                .roles(UserRoles.CARD_OWNER.role()) // Assign CARD-OWNER role
                .build(); // Create the UserDetails object

        // Return an InMemoryUserDetailsManager containing both users
        return new InMemoryUserDetailsManager(sarah, hankOwnsNoCards, kumar);
    }
}
