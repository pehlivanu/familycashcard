package dev.ionelivi.cashcard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Cash Card Spring Boot application.
 * 
 * <p>
 * This class bootstraps and launches the Spring Boot application. The {@code @SpringBootApplication} 
 * annotation enables the following features:
 * <ul>
 *   <li>{@code @Configuration}: Tags this class as a source of bean definitions</li>
 *   <li>{@code @EnableAutoConfiguration}: Enables Spring Boot's auto-configuration mechanism</li>
 *   <li>{@code @ComponentScan}: Enables component scanning in the application package</li>
 * </ul>
 * </p>
 */
@SpringBootApplication
public class CashcardApplication {

	/**
	 * The main method which serves as the entry point for the application.
	 * 
	 * <p>
	 * This method delegates to Spring Boot's {@link SpringApplication#run} method to bootstrap 
	 * the application. It sets up the default configuration, starts the embedded server, 
	 * and performs class path scanning.
	 * </p>
	 * 
	 * @param args command line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(CashcardApplication.class, args);
	}

}
