package com.example.reports.service;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.mail.internet.MimeMessage;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Service
public class MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    @Autowired
    JavaMailSender javaMailSender;
    
    @Value("${mail.from}")
    private String fromAddress;
    
    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart, isHtml, to, subject, content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try
        {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(fromAddress);
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled()) {
                log.warn("Email could not be sent to user '{}'", to, e);
            } else {
                log.warn("Email could not be sent to user '{}': {}", to, e.getMessage());
            }
        }
    }    
    
    @Async
    public void sendEmail(String to, String subject, String content, List<URL> attachments) 
	{
        log.debug("## SEND EMAIL [multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",!attachments.isEmpty(), true, to, subject, content);
        
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        
        try
        {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, !attachments.isEmpty(), StandardCharsets.UTF_8.name());
            
            message.setTo(to);
            
            for(URL url : attachments)
            {	
            	ByteArrayResource resource =new ByteArrayResource(IOUtils.toByteArray(url.openStream()));
            	message.addAttachment(extractFileName(url.toString()),resource);
            }
            
            message.setFrom(fromAddress);
            message.setSubject(subject);
            message.setText(content,true);
            
            javaMailSender.send(mimeMessage);
            
            log.debug("## SEND EMAIL TO USER '{}'", to);
        }
        catch (Exception e)
        {
        	log.error("## EMAIL COULD NOT BE SENT TO USER '{}' : {}", to, e.getMessage());
        }
    }
    
    public String extractFileName(String filePath) 
	{
    	log.info("## ATTACHMENT FILE PATH :: " + filePath);
		String[] split2 = null;
		String fieName = null;

		if(filePath.contains("/")) 
		{
			split2 = filePath.split("/");
			fieName = split2[split2.length - 1];
		} 
		else if(filePath.indexOf("\\") != -1) 
		{
			split2 = filePath.split("\\\\");
			fieName = split2[split2.length - 1];
		} 
		else 
		{
			fieName = filePath;
		}
		return fieName;
	}
}
