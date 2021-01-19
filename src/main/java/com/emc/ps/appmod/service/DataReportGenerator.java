package com.emc.ps.appmod.service;

import com.emc.ps.appmod.domain.DatabaseMetadataSummary;
import com.emc.ps.appmod.domain.DatabaseMetadataSummaryReport;

/**
 * 
 * @author ayon.bhattacharya
 *
 */
public interface DataReportGenerator {

	public void generateDataReport(DatabaseMetadataSummaryReport report);
	public boolean isDataComparisionRequired(DatabaseMetadataSummary mdSummary);
}
