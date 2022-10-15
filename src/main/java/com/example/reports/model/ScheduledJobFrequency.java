package com.example.reports.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "scheduled_job_frequency")
public class ScheduledJobFrequency
{
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scheduled_job_frequency_id_seq")
    @SequenceGenerator(name = "scheduled_job_frequency_id_seq",sequenceName="scheduled_job_frequency_id_seq",allocationSize=1)
    private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "cron_expression")
	private String cronExpression;

	
	
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

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
}
