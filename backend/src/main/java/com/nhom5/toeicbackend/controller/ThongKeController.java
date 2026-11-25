package com.nhom5.toeicbackend.controller;

import com.nhom5.toeicbackend.dao.ThongKeDAO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class ThongKeController {

    private final ThongKeDAO dashboardStatisticsDAO;

    public ThongKeController(ThongKeDAO dashboardStatisticsDAO) {
        this.dashboardStatisticsDAO = dashboardStatisticsDAO;
    }

    @GetMapping("/statistics")
    public Map<String, Long> getStatistics() {
        Map<String, Long> statistics = new LinkedHashMap<>();
        statistics.put("totalUsers", dashboardStatisticsDAO.countUsers());
        statistics.put("totalQuestions", dashboardStatisticsDAO.countQuestions());
        statistics.put("totalExams", dashboardStatisticsDAO.countExams());
        return statistics;
    }
}
