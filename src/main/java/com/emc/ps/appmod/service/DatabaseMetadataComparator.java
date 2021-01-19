/**
 *
 */
package com.emc.ps.appmod.service;

import com.emc.ps.appmod.domain.DatabaseMetadataSummaryReport;

/**
 * @author ayon.bhattacharya
 *
 */
public interface DatabaseMetadataComparator {

	public DatabaseMetadataSummaryReport compareDatabaseMetadata();

	public String getMappedDatatype(String key, String defaultValue);
}
