package com.emc.ps.appmod.service;

import com.emc.ps.appmod.domain.DatabaseMetadataSummaryReport;

/**
 * 
 * @author ayon.bhattacharya
 *
 */

public interface DatabaseMetadataReportGenerator {

	public void generateMetadataReport(DatabaseMetadataSummaryReport report);
}
