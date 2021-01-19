package com.emc.ps.appmod.service.impl;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.emc.ps.appmod.data.store.AppDataStore;
import com.emc.ps.appmod.domain.Tables;
import com.emc.ps.appmod.exception.DataValidatorException;
import com.emc.ps.appmod.service.DataBaseMetadataConfigFileReader;
import com.emc.ps.appmod.service.XmlToJavaConverter;

/**
 * 
 * @author ayon.bhattacharya
 *
 */
@Service
public class DatabaseMetadataConfigFileReaderImpl implements DataBaseMetadataConfigFileReader{

	private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseMetadataConfigFileReader.class);
	
	@Value("${database.metadata.config.file.location}")
	private Resource databaseMetadataConfigFileLocation;
	
	@Autowired
	private XmlToJavaConverter<Tables> xmlToJavaConverter;
	
	
	
	@Override
	public void processDatabaseMetadataConfigFile() {
		// read metadata config file
		File file = null;
		try{
			file = databaseMetadataConfigFileLocation.getFile();
		}catch(IOException e){
			LOGGER.error("Error while reading database metadata xml file, root cause is {}", e.getMessage());
			LOGGER.error(e.getMessage(), e);
			throw new DataValidatorException(e.getMessage(), e);
		}
		LOGGER.info("Reading DatabaseMetadataConfigFile {}", file.toString());
		Tables tables = xmlToJavaConverter.convertFromXMLToObject(file.getAbsolutePath());
		LOGGER.info("Completed reading database Metadata Config File as {}", tables.toString());
		
		AppDataStore.updateConfigMetadataCache(tables.getTables());
	}
}
