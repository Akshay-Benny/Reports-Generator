package com.example.reports.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import com.example.reports.model.Report;


@Repository
public interface ReportRepository extends JpaRepository<Report,Long> 
{

}
