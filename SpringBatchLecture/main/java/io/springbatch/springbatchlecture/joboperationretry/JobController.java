package io.springbatch.springbatchlecture.joboperationretry;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class JobController {

    private final JobOperator jobOperator;
    private final JobExplorer jobExplorer;

    @RequestMapping("/batch/new")
    public String jobStart(@RequestParam(name = "id") String id) throws JobInstanceAlreadyExistsException, NoSuchJobException, JobParametersInvalidException {
        Set<String> jobNames = jobOperator.getJobNames();
        for (String jobName : jobNames) {
            jobOperator.start(jobName, id + "abc");
        }
        System.out.println("Job Start!");
        return "batch Job start!";
    }

    @RequestMapping("/batch/stop")
    public String jobStop() throws JobInstanceAlreadyExistsException, NoSuchJobException, JobParametersInvalidException, NoSuchJobExecutionException, JobExecutionNotRunningException {

        System.out.println("Job Stop!");

        Set<String> jobNames = jobOperator.getJobNames();

        Set<Long> jobOperationRetryJob = jobOperator.getRunningExecutions("jobOperationRetryJob");
        for (Long aLong : jobOperationRetryJob) {
            jobOperator.stop(aLong);
        }

        return "batch Job stop!";
    }

    @RequestMapping("/batch/re")
    public String jobRetry(@RequestParam(name = "id") String id) throws JobInstanceAlreadyExistsException, NoSuchJobException, JobParametersInvalidException, JobInstanceAlreadyCompleteException, NoSuchJobExecutionException, JobRestartException {
        System.out.println("Job Retry!");
        Set<String> jobNames = jobOperator.getJobNames();

        JobInstance lastJobInstance = jobExplorer.getLastJobInstance("jobOperationRetryJob");
        JobExecution lastJobExecution = jobExplorer.getLastJobExecution(lastJobInstance);

        jobOperator.restart(lastJobExecution.getId());
        return "batch Job Retry!";
    }


    JobRegistry


}
