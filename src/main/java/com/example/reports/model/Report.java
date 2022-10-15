package com.example.reports.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.example.reports.service.dto.ReportDataModel;
import com.vladmihalcea.hibernate.type.json.JsonStringType;

@Entity
@TypeDefs({
    @TypeDef(name = "jsonb", typeClass = JsonStringType.class)
})
@Table(name = "report")
public class Report 
{
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "qc_report_id_seq")
    @SequenceGenerator(name = "qc_report_id_seq",sequenceName="qc_report_id_seq",allocationSize=1)
    private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description")
	private String description;
	
	@Column(name = "frequency")
	private String frequency;
	
	@Type(type = "jsonb")
    @Column(name = "recipients", columnDefinition = "jsonb")
	private List<Map<String,String>> recipients;
	
	@Column(name = "email_subject", nullable = false, unique = true)
	private String emailSubject;
	
	@Column(name = "email_template", nullable = false, unique = true)
	private String emailTemplate;
	
	@Type(type = "jsonb")
    @Column(name = "meta_data", columnDefinition = "jsonb")
	private ReportDataModel dataModel;
	
	
	public Report() 
	{
		this.dataModel = new ReportDataModel();
		this.recipients = new ArrayList<Map<String,String>>();
	}
	
	
	public ReportDataModel getDataModel() {
		return dataModel;
	}

	public void setDataModel(ReportDataModel dataModel) {
		this.dataModel = dataModel;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(String emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Map<String,String>> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<Map<String,String>> recipients) {
		this.recipients = recipients;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

}
