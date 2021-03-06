package eu.openminted.store.controller;

import org.springframework.boot.SpringApplication;
import eu.openminted.store.config.ApplicationConfigurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Starts the application. 
 * @author galanisd
 *
 */
public class Starter {
	
	private final static Logger log = LoggerFactory.getLogger(Starter.class);	
			
	public static void main(String[] args) {		
		log.info("Starting OMTD STORE SERVICE.");
				
		// Configure app.
		ApplicationConfigurator appConfigtr = new ApplicationConfigurator();
		appConfigtr.configure();
		
		// Run app within a Spring Context.
		SpringApplication springApplication = new SpringApplication(ApplicationBoot.class);		
		springApplication.run();
	}

}

