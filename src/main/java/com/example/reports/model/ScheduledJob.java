package com.example.reports.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.vladmihalcea.hibernate.type.json.JsonStringType;

@Entity
@TypeDefs({
    @TypeDef(name = "jsonb", typeClass = JsonStringType.class)
})
@Table(name = "scheduled_job")
public class ScheduledJob 
{
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scheduled_job_id_seq")
    @SequenceGenerator(name = "scheduled_job_id_seq",sequenceName="scheduled_job_id_seq",allocationSize=1)
    private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description")
	private String description;
	
	@Column(name = "job_name", nullable = false, unique = true)
	private String jobName;
	
	@Column(name = "job_class", nullable = false, unique = true)
	private String jobClass;

	@ManyToOne
	private ScheduledJobFrequency frequency;
	
	
	@Type(type = "jsonb")
    @Column(name = "meta_data", columnDefinition = "jsonb")
	private Map<String,Object> metaData;
	
	@Column(name = "group_name", nullable = false, unique = true)
	private String groupName;
	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public ScheduledJob() 
	{
		this.metaData = new HashMap<String, Object>();
	}
	
	public Map<String, Object> getMetaData() {
		return metaData;
	}

	public void setMetaData(Map<String, Object> metaData) {
		this.metaData = metaData;
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

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobClass() {
		return jobClass;
	}

	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}

	public ScheduledJobFrequency getFrequency() {
		return frequency;
	}

	public void setFrequency(ScheduledJobFrequency frequency) {
		this.frequency = frequency;
	}
	
}
