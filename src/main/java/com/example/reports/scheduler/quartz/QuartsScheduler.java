package com.example.reports.scheduler.quartz;
 
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;


@Configuration
public class QuartsScheduler
{
    private static Logger logger = LoggerFactory.getLogger(QuartsScheduler.class);
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private Environment environment;

    
    @PostConstruct
    public void init() 
    {
        logger.info("###  INITIALIZED QUARTZ SCHEDULER..");
    }
   
    
    @Bean
    public SpringBeanJobFactory springBeanJobFactory() 
    {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        logger.debug("###  CONFIGURING JOB FACTORY");

        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    
    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factory) throws SchedulerException 
    {
    	logger.info("###  INITIALIZING QUARTZ SCHEDULER..");
        Scheduler scheduler = factory.getScheduler();
        
        logger.info("###  STARTING QUARTZ SCHEDULER THREADS..");
        scheduler.start();
        return scheduler;
    }

    
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException 
    {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(springBeanJobFactory());
        factory.setQuartzProperties(quartzProperties());
        return factory;
    }

    
    public Properties quartzProperties() throws java.io.IOException 
    {
    	Arrays.stream(environment.getActiveProfiles()).forEach(env -> logger.info("###  ACTIVE PROFILES {}",env));
    	
    	logger.info("###  LOADING QUARTZ PROPERTIES FOR {}",environment.getActiveProfiles()[0]);
    	
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz-"+environment.getActiveProfiles()[0]+".properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }
  	
}