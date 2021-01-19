package com.emc.ps.appmod.service;

//import com.emc.ps.appmod.exception.DataValidatorException;

/**
 * 
 * @author ayon.bhattacharya
 *
 */
public interface HawqMetadataReader {
	/**
	 * This method will read hive meta data from data base and caches the data.
	 * In case of any exception this method will throw DataValidatorException
	 */
	public void processHiveMetadata() ;

}
