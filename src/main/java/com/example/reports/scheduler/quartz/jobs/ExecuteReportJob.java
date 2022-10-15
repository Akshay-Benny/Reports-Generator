package com.example.reports.scheduler.quartz.jobs;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.reports.service.ExecuteReportService;

@Component
public class ExecuteReportJob implements Job
{	
	
	private Logger logger = LoggerFactory.getLogger(ExecuteReportJob.class);
	
	
	@Autowired
	ExecuteReportService executeReportService;
	
	
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException 
	{
    	logger.info("### PAYMENT REPORT FOR EXTERNAL USERS, EXECUTING AT : '{}'", Instant.now());
    	
		JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();
		
		Map<String,Object> metaData = new HashMap<String, Object>();
		
		if(map != null)
		{
			for(Entry<String,Object> entry : metaData.entrySet())
			{
				map.put(entry.getKey(),entry.getValue());
			}
		}
		
		try 
		{
			executeReportService.execute(map);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}	
	
}
