package com.example.reports.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import com.example.reports.model.Report;
import com.example.reports.repository.ReportRepository;
import com.example.reports.service.dto.ReportDataModel;
import com.example.reports.service.dto.ReportDataModelField;
import com.example.reports.service.util.DBUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class ExecuteReportService
{
	private static Logger logger = LoggerFactory.getLogger(ExecuteReportService.class);
	
	
	@Autowired
	private MailService mailService;
	
	
	@Autowired
	private ReportRepository reportRepository;
	
	
	@Autowired
	SpringTemplateEngine templateEngine;
	
	
	@Autowired
	DBUtil dbUtil;
	
	
	@Value("${qc.report.path}")
	String reportPath;
	
	
	public void execute(JobDataMap metaData) throws Exception
	{
		if(!metaData.containsKey("reportId"))
		{
			logger.info("### NO REPORT ID IS PRESENT IN THE META DATA");
			return;
		}
		
		Long reportId = new Long((Integer) metaData.get("reportId"));
		
		Report report = reportRepository.findById(reportId).get();
		
    	Instant now = Instant.now();
    	
    	logger.info("### CHECKING '{}' PAYMENT REPORT FOR EXTERNAL USERS AT : '{}'", report.getFrequency(), now);

    	List<Map<String,String>> recipients = report.getRecipients();
    	
    	ReportDataModel dataModel = report.getDataModel();
    	
    	HashMap<String, Object> nvPair = dataModel.getParams();
		
		LinkedList<LinkedHashMap<String,Object>> result = dbUtil.fetchData(dataModel.getQuery(),nvPair);
		
		Comparator<ReportDataModelField> mapComparator = new Comparator<ReportDataModelField>() 
		{
		    public int compare(ReportDataModelField m1,ReportDataModelField m2) 
		    {
		        return m1.getColumnPosition().compareTo(m2.getColumnPosition());
		    }
		};
		
		Collections.sort(dataModel.getFields(), mapComparator);
		
		File attachment = createPDF(report, dataModel.getFields(), result);
    	
    	if(!recipients.isEmpty())
    	{
	    	try 
	    	{
	    		List<URL> attachments = new ArrayList<URL>();
	    		
	    		attachments.add(attachment.toURI().toURL());
	    		
	    		for(Map<String,String> recipient : recipients)
	    		{
	    			logger.debug("SENDING EMAIL WITH '{}' FILE ATTACHED TO USER : '{}'", report.getName(), recipient.get("name"));
	    	    	
	    			Context context = new Context();
		    		
	    			context.setVariable("name",(String) recipient.get("name"));
		    		
		            String content = templateEngine.process(report.getEmailTemplate(), context);
		    		
					mailService.sendEmail(recipient.get("email"), report.getEmailSubject(), content, attachments);
	    		}
			} 
	    	catch(IOException e) 
	    	{
				e.printStackTrace();
				logger.error("ERROR OCCURED WHILE SENDING EMAIL.",e);
			}
    	}
	}
	
	
	private File createPDF (Report report, List<ReportDataModelField> fields, LinkedList<LinkedHashMap<String,Object>> result) throws IOException
    {
 	   	String path = reportPath + report.getFrequency().toLowerCase() + "_"+ report.getName().toLowerCase().replace(" ","_") +"_"+Instant.now().toEpochMilli() + ".pdf";
 	   
 	    logger.debug("File Path : "+ path);
 	   	
    	File file = new File(path);
    	
    	if(!file.exists())
    	{
    		file.createNewFile();
    	}
    	
    	Document doc = new Document();
    	  
  		try 
    	{  
    	   PdfWriter.getInstance(doc , new FileOutputStream(path));
    	    
    	   doc.addAuthor("Qseries");
    	   doc.addCreationDate();
    	   doc.addProducer();
    	   doc.addCreator("Qseries");
    	   doc.addTitle(report.getName());
    	   doc.setPageSize(PageSize.A4.rotate());
    	   
    	   doc.open();
    	   
	       PdfPTable table = new PdfPTable(fields.size());

	       table.setWidthPercentage(100);
	       table.setSpacingBefore(0f);
	       table.setSpacingAfter(0f);
	       table.setHeaderRows(1);

	       PdfPCell cell = new PdfPCell(new Phrase(report.getName()));
	       cell.setColspan(fields.size()+1);
	       cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	       cell.setPadding(5.0f);
	       cell.setBackgroundColor(new BaseColor(127,54,219));
	       table.addCell(cell);
	       

	       float[] relativeWidths = new float[fields.size()];
	       
	       for(int i=0; i<fields.size(); i++)
	       {
	    	   relativeWidths[i] = fields.get(i).getWidth();
	       }
	       
	       table.setWidths(relativeWidths);
	       
	       for(ReportDataModelField field : fields)
	       {
	    	   table.addCell(field.getHeader());
	       }
	       
	       for(LinkedHashMap<String,Object> dataRow : result)
	       {
	    	   for(ReportDataModelField field : fields)
	 	       {
	    		   if(dataRow.containsKey(field.getName()))
	    		   {
	    			   table.addCell(dataRow.get(field.getName()).toString());
	    		   }
	    		   else
	    		   {
	    			   table.addCell("");
	    		   }
	 	       }
	       }
	       doc.add(table);
	       doc.close();
         
   	  	} 
  		catch (FileNotFoundException | DocumentException e) 
  		{
           e.printStackTrace();
   	  	}
  		return file;
    }
	
}
