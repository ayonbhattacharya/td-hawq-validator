package com.emc.ps.appmod.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 
 * @author ayon.bhattacharya
 *
 */

	@XmlRootElement(name = "table", namespace="tables")
	@XmlType(propOrder = { "name","schema","rowCountToValidate","columnTypeCheckRequired","additionalColumns","targetName"})
	@Service
	public class TableInfo {

		@Value("${default.row.count.to.validate}")
		private Integer defaultRowCountToValidate;

		/**
		 *
		 */
		public TableInfo() {
		}

		private String name;
		private String schema;
		private Integer rowCountToValidate;
		private String targetName;
		private String columnTypeCheckRequired;
		private String additionalColumns;

		@XmlElement(name = "name")
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@XmlElement(name = "schema")
		public String getSchema() {
			return schema;
		}

		public void setSchema(String schema) {
			this.schema = schema;
		}

		@XmlElement(name = "rowCountToValidate")
		public Integer getRowCountToValidate() {
			return rowCountToValidate;
		}

		public void setRowCountToValidate(Integer rowCountToValidate) {
			// If user configured value is not null and greater than zero then set
			// the value otherwise use default value
			if ((null != rowCountToValidate) && (rowCountToValidate.intValue() > 0)) {
				this.rowCountToValidate = rowCountToValidate;
			}else {
				this.rowCountToValidate = defaultRowCountToValidate;
			}
		}
		
		@XmlElement(name = "targetName")
		public String getTargetName() {
			return targetName;
		}

		public void setTargetName(String targetName) {
			this.targetName = targetName;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
		
		  @XmlElement(name = "columnTypeCheckRequired")
		  public String getColumnTypeCheckRequired() {
		    return columnTypeCheckRequired;
		  }
		
		public void setColumnTypeCheckRequired(String columnTypeCheckRequired) {
			this.columnTypeCheckRequired = columnTypeCheckRequired;
		}

		
		 @XmlElement(name = "additionalColumns")
		  public String getAdditionalColumns() {
		    return additionalColumns;
		  }
		
		public void setAdditionalColumns(String additionalColumns) {
			this.additionalColumns = additionalColumns;
		}

}

