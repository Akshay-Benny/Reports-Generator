package com.example.reports.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import com.example.reports.model.ScheduledJob;

@Repository
public interface ScheduledJobRepository extends JpaRepository<ScheduledJob,Long> 
{
	ScheduledJob findByJobName(String jobName);
}
