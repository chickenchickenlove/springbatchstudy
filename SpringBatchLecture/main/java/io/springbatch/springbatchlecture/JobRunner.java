package io.springbatch.springbatchlecture;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


// 스프링부트가 제공함
// 스프링부트 초기화 완료되면 가장 먼저 호출하는 타입의 클래스
// 이 Runner의 run을 호출해줌.
//@Component
@RequiredArgsConstructor
public class JobRunner implements ApplicationRunner {

    // 스프링부트가 생성될 때, 이미 빈으로 등록되어있음.
    private final JobLauncher jobLauncher;
    // Batch Job의 의존성 주입
    private final Job job;

    // 여기서 잡을 실행해보자.
    @Override
    public void run(ApplicationArguments args) throws Exception {

        // JobParamter 만들기.
        // key / value 형태로 값을 가짐
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("name", "user2")
                .toJobParameters();

        // 스프링부트는 스프링을 초기화하면서, 내부적으로 잡 런처를 가지고 잡을 실행시킴.
        // Job 런처로 잡을 실행해본다.
        jobLauncher.run(job, jobParameters);


    }
}
