package com.example.reports.service.dto;

import java.util.HashMap;
import java.util.List;

public class ReportDataModel 
{
	String query;
	
	HashMap<String,Object> params;
	
	List<ReportDataModelField> fields;

	
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void setFields(List<ReportDataModelField> fields) {
		this.fields = fields;
	}

	public HashMap<String, Object> getParams() {
		return params;
	}

	public void setParams(HashMap<String, Object> params) {
		this.params = params;
	}

	public List<ReportDataModelField> getFields() {
		return fields;
	}
}
