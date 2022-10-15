package com.example.reports.scheduler.quartz;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.PostConstruct;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.reports.model.ScheduledJob;
import com.example.reports.model.ScheduledJobFrequency;
import com.example.reports.repository.ScheduledJobRepository;

@Component
public class SchedulerInit 
{
	@Autowired
	ScheduledJobRepository scheduledJobRepository;
	
	@Autowired
	Scheduler scheduler;
	
	private static Logger logger = LoggerFactory.getLogger(Scheduler.class);
	
    @PostConstruct
	@SuppressWarnings("unchecked")
    public void initScheduler() throws SchedulerException, ParseException 
    { 
    	logger.debug("### SCHEDULING JOBS ON INIT..");
    	
    	List<ScheduledJob> jobs = scheduledJobRepository.findAll();
    	
    	if(scheduler.isStarted())
		{
    		for(ScheduledJob job : jobs)
        	{
        		ScheduledJobFrequency frequency = job.getFrequency();
        		
    			JobKey jobKey = new JobKey(job.getJobName(), job.getGroupName());
    			TriggerKey triggerKey = new TriggerKey(job.getJobName(), job.getGroupName());
    			
    			logger.debug("### JOB NAME : {} ",job.getJobName());
    			logger.debug("### JOB GROUP NAME : {} ",job.getGroupName());
    			logger.debug("### IS JOB EXIST : {} ",scheduler.checkExists(jobKey));
    			
    			if(!scheduler.checkExists(jobKey))
    			{
    				try 
    				{
    					Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(job.getJobClass());
    					
    					JobDataMap jobDataMap = new JobDataMap();
    					
    					Map <String,Object> metaData = job.getMetaData();
    					
    					if(metaData != null)
    					{
    						for(Entry<String,Object> entry : metaData.entrySet())
        					{
        						jobDataMap.put(entry.getKey(),entry.getValue());
        					}
    					}
    					
    					JobDetail jobDetail = JobBuilder.newJob(jobClass)
    												    .withIdentity(jobKey)
    												    .setJobData(jobDataMap)
    												    .build();
    					
    					Trigger trigger = TriggerBuilder.newTrigger()
    													.withIdentity(triggerKey)
    	     		   			   						.withSchedule(CronScheduleBuilder.cronSchedule(frequency.getCronExpression()))
    	     		   			   						.build();
    					
    					scheduler.scheduleJob(jobDetail, trigger);
    					
    					logger.debug("### SCHEDULED NEW JOB : {} ",job.getJobName());
    				} 
    				catch (ClassNotFoundException e) 
    				{
    					e.printStackTrace();
    				}
    			}
        	}
		}
    }
}
