package com.emc.ps.appmod.service.impl;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import com.emc.ps.appmod.data.store.AppDataStore;
import com.emc.ps.appmod.domain.Column;
import com.emc.ps.appmod.domain.RdbmsTableMetadata;
import com.emc.ps.appmod.domain.TableMetadata;
import com.emc.ps.appmod.exception.DataValidatorException;
import com.emc.ps.appmod.service.DatabaseMetadataComparator;
import com.emc.ps.appmod.service.HawqMetadataReader;

@Service
public class HawqMetadataReaderImpl implements HawqMetadataReader{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HawqMetadataReader.class);

	@Autowired
	@Qualifier("hawqDataSource")
	private DataSource hawqDataSource;
	
	@Value("${db.compare.metadata.values}")
	private String compareMetadataFlag;
/*	@Autowired
	private DatabaseMetadataComparator databaseMetadataComparator;*/
	
	@Override
	public void processHiveMetadata() {
			Connection con =null;
			try{
				con= DataSourceUtils.getConnection(hawqDataSource);
			}catch(CannotGetJdbcConnectionException e ){
				e.printStackTrace();
			}
			try{
				List<String> nonExistingHawqTables = new ArrayList();
				//get rdbms table information
				List<RdbmsTableMetadata> rdbmsTableInfoList = AppDataStore.getRdbmsTableInfoList();
				//hawq table metadata information
				List<TableMetadata> hawqMetadataList = new ArrayList<>();
				int totalTblsSize = rdbmsTableInfoList.size();
				int index =0;
				for (RdbmsTableMetadata rdbmsTableMetadata : rdbmsTableInfoList) {
					String schema = rdbmsTableMetadata.getSchema();
					String tableName = rdbmsTableMetadata.getName();
					String targetTableName = rdbmsTableMetadata.getTargetName();
					
					if (null == targetTableName || "".equals(targetTableName)) {
						targetTableName = tableName;
					}
					
					LOGGER.info("Hawq table name to compare with rdbms table, schema {}, rdbms table name {} and hawq table name {}", schema, tableName, targetTableName);

					LOGGER.info("============================================================================");
					LOGGER.info("======READING TARGET METADATA for TABLE {} : {} OF {} =============", targetTableName, ++index,
							totalTblsSize);
					LOGGER.info("============================================================================");
				
					Statement stmt = con.createStatement();
					// describe table
					//String sql = new StringBuilder().append("describe ").append(targetTableName).toString();
					//String schema_name ="validator_test";
					String countSql = new StringBuilder().append("select count(*) from INFORMATION_SCHEMA.COLUMNS where table_name = \'").append(targetTableName).append("\';").toString();
					String sql = new StringBuilder().append("select * from INFORMATION_SCHEMA.COLUMNS where table_name = \'").append(targetTableName).append("\';").toString();
					LOGGER.info("Running query {} ", sql);
					ResultSet res = null;
					ResultSet res1 = null;
					try {
						res1 = stmt.executeQuery(countSql);
						res1.next();
						int rowcount = res1.getInt("count");
						if(rowcount==0){
							nonExistingHawqTables.add(targetTableName);
							LOGGER.error("Table with name {} does not exists in target database", targetTableName);
							continue;
						}
						res = stmt.executeQuery(sql);
					} catch (SQLException e) {
						e.printStackTrace();
						LOGGER.error("Error while describing table {}, root cause is {}", targetTableName, e.getMessage());
						continue;
					}
					TableMetadata hawqTblMetadata = new TableMetadata();
					hawqTblMetadata.setName(targetTableName);
					List<Column> colList = new ArrayList<>();
					SimpleEntry<String, String> tableEntry = new SimpleEntry<String, String>(schema, tableName);
					RdbmsTableMetadata rdbmsTableInfo = AppDataStore.getRdbmsTableInfo(tableEntry);
					List<Column> rdbmsCols = rdbmsTableInfo.getCols();
				
					while (res.next()) {
						String hawqColName = res.getString("column_name");
						String hawqType = null;
						if(compareMetadataFlag.equalsIgnoreCase("false")){
							hawqType = "Default";
						}
						else{
						 hawqType=res.getString("data_type");
						}
						for (Column column : rdbmsCols) {
							
							if (column.getName().equalsIgnoreCase(hawqColName)) {
								String rdbmsType = column.getType();
								hawqType = hawqType.indexOf("(") > 0 ? hawqType.substring(0, hawqType.indexOf("("))
										: hawqType;
								String mappingHawqType = "";
								//String mappingHiveType = databaseMetadataComparator.getMappedDatatype(rdbmsType, rdbmsType);
								if (hawqType.equalsIgnoreCase(mappingHawqType)) {
									hawqType = rdbmsType;
									break;
								}
							}
						}
						colList.add(new Column(hawqColName, hawqType));
				}
				
					hawqTblMetadata.setCols(colList);
					hawqMetadataList.add(hawqTblMetadata);
				}
				LOGGER.info("Extracted HAWQ tables metadata and HAWQ tables metadata is {}.", hawqMetadataList);
				// update cache
				AppDataStore.updateHawqMetadataCache(hawqMetadataList);
				// update non existing hawq tables
				AppDataStore.updateNonExistingHawqTbls(nonExistingHawqTables);
			}catch(Exception e1){
				LOGGER.error("Error while reading HAWQ metadata for data source {}, root cause is {}", hawqDataSource,e1.getMessage());
				LOGGER.error(e1.getMessage(), e1);
				throw new DataValidatorException(e1.getMessage(), e1);
			}
	}

}
