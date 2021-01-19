package com.emc.ps.appmod;


import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.emc.ps.appmod.service.Teradata2HawqValidator;

/**
*
* @author Ayon Bhattacharya
*
*/
@SpringBootApplication
public class Teradata2HawqValidatorApplication {

	private static final Logger LOGGER  = LoggerFactory.getLogger(Teradata2HawqValidatorApplication.class);
	
	@Autowired
	private Teradata2HawqValidator teradata2HawqValidator;
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Teradata2HawqValidatorApplication.class);
	    app.setBannerMode(Banner.Mode.OFF);
	    app.run(args);

	}

	@PostConstruct
	public void initValidation(){
		try{
			//---- Start Teradata to Hawq Data Validation
			teradata2HawqValidator.startTeradata2HawqValidation();
			//--- TO DO
			LOGGER.info("============================================================================");
			LOGGER.info("======DATA VALIDATION COMPLETED SUCCESSFULLY, CLOSING APPLICATION===========");
			LOGGER.info("============================================================================");
		}catch(Exception e){
			LOGGER.error("Error while performing data validation and closing application context, root cause is {}", e.getMessage());
			LOGGER.error(e.getMessage(), e);
			LOGGER.info("============================================================");
			LOGGER.info("======DATA VALIDATION FAILED, CLOSING APPLICATION===========");
			LOGGER.info("============================================================");
			//throw new DataValidatorException(e.getMessage(), e);
			// TO DO ----------
		}finally {
			// Once the data validation is successful close the application context
			System.exit(Integer.valueOf(0));
		}
	}


}


