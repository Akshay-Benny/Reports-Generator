package com.example.reports.service.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Component
public class DBUtil 
{
	@Autowired
	SpringTemplateEngine templateEngine;
	
	@Value("${spring.datasource.username}")
	String dbusername;
	
	@Value("${spring.datasource.password}")
	String dbpassword;
	
	@Value("${spring.datasource.url}")
	String dburl;
	
	
	public LinkedList<LinkedHashMap<String,Object>> fetchData(String query, HashMap<String, Object> nvPair) throws Exception 
	{	
		//query = renderTemplate(query,nvPair);
		
		System.out.println("## QUERY GENERATED => "+query);
		
		LinkedList<LinkedHashMap<String,Object>> result = fetchFromDB(query);
		
		System.out.println("## RETREIVED "+result.size()+" RECORDS");
		
		return result;
	}
	
	public LinkedList<LinkedHashMap<String,Object>> fetchFromDB(String query) throws Exception
	{
		return executeQuery(query);
	}
	
	public LinkedList<LinkedHashMap<String,Object>> deleteFromDB(String query) throws Exception
	{
		return executeQuery(query);
	}
	
	public LinkedList<LinkedHashMap<String,Object>> executeQuery(String query) throws Exception
	{
		LinkedList<LinkedHashMap<String,Object>> result = new LinkedList<LinkedHashMap<String,Object>>();
		Connection conn = null;
		ResultSet rs = null;	
		try 
		{
			DriverManager.registerDriver(new org.postgresql.Driver());
			conn = DriverManager.getConnection(dburl, dbusername, dbpassword);
			conn.setAutoCommit(true);
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(query);	
			result = resultSetToArrayList(rs);
		} 
		catch (Exception e) 
		{		
			System.out.println("@@@ Exception while executing query :: " + query);
			System.out.println("@@@ Exception while executing query :: "+ e.getMessage());		
		}
		finally
		{
			if(rs!=null)
			{
				rs.close();
			}
			if(conn!=null)
			{
				conn.close();				
			}		
		}	
		return result;
	}
	
	public LinkedList<LinkedHashMap<String,Object>> resultSetToArrayList(ResultSet rs) throws SQLException
	{
		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();
		LinkedList<LinkedHashMap<String,Object>> list = new LinkedList<LinkedHashMap<String,Object>>();
		while(rs.next())
		{
			LinkedHashMap<String,Object> row = new LinkedHashMap<String,Object>();
		    for(int i=1; i<=columns; ++i)
		    {           
		    	row.put(md.getColumnName(i).toUpperCase(),rs.getObject(i));
		    }
		    list.add(row);
		}
		return list;
	}

	public String renderTemplate(String template, Map<String, Object> nvPairs) 
	{
		Context context = new Context();
		Set<Entry<String,Object>> kvPairs = nvPairs.entrySet();
		for(Entry<String,Object> kvPair : kvPairs)
		{
			context.setVariable(kvPair.getKey(), kvPair.getValue());
		}
		return templateEngine.process(template, context);
	}
}
