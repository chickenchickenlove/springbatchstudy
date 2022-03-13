package io.springbatch.springbatchlecture.dbwriter;

import io.springbatch.springbatchlecture.dbwriter.service.FindRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//@Controller
public class TempController {


    private final JobLauncher jobLauncher;
    private final FindRepository findRepository;
    private final Job job1;
    private final Job job2;


    public TempController(JobLauncher jobLauncher, Job jpaBatchItemWriterJob, Job jdbcBatchItemWriterJob, FindRepository findRepository) {
        this.jobLauncher = jobLauncher;
        this.job1 = jpaBatchItemWriterJob;
        this.job2 = jdbcBatchItemWriterJob;
        this.findRepository = findRepository;
    }

    @RequestMapping("/test1")
    @ResponseBody
    public String test1() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {


        JobParameters abc = new JobParametersBuilder().addString("abc", "1zx999df3dgcbcb")
                .toJobParameters();

        long s = System.currentTimeMillis();
        jobLauncher.run(job1, abc);
        long e = System.currentTimeMillis();

        System.out.println("jpaBatchItemWriterJob Takes Time = " + (e-s));
        return "ok";
    }


    @RequestMapping("/test2")
    @ResponseBody
    public String test2() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        JobParameters abc = new JobParametersBuilder().addString("abc", "1zx999df3")
                .toJobParameters();

        long s = System.currentTimeMillis();
        jobLauncher.run(job2, abc);
        long e = System.currentTimeMillis();
        System.out.println("jdbcBatchItemWriterJob Takes Time = " + (e-s));

        return "ok";
    }

    @RequestMapping("/find")
    @ResponseBody
    public String findData() {
        int totalNum1 = findRepository.findCustomerSize();
        int totalNum2 = findRepository.findCustomer2Size();
        int totalNum3 = findRepository.findCustomer3Size();
        System.out.println("Customer Total Num = " + totalNum1);
        System.out.println("Customer2 Total Num = " + totalNum2);
        System.out.println("Customer3 Total Num = " + totalNum3);
        return "ok";
    }


}
