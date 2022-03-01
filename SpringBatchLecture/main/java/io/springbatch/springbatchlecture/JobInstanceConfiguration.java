package io.springbatch.springbatchlecture;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class JobInstanceConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
                .start(step1())
                .next(step2())
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

                        // StepContribution에서 Job Parameter 확인 가능
                        // 실제 Job Parameter의 값. 수정도 가능함.
                        JobParameters jobParameters = contribution.getStepExecution().getJobExecution().getJobParameters();
                        jobParameters.getString("name");
                        jobParameters.getLong("seq");
                        jobParameters.getDate("date");
                        jobParameters.getDouble("age");


                        // ChunkContext에서 Job Parameter 확인 가능.
                        // 단순히 값만 읽어옴. 디버그 모드로 읽어보면 Parameter가 아닌 거 확인 가능.
                        Map<String, Object> jobParameters1 = chunkContext.getStepContext().getJobParameters();



                        System.out.println("STEP1 has executed");

                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }


    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
//                        throw new RuntimeException("step2 has failed"); // 예외를 발생시켜 Exeuction 상태가 Failed 되서 동일 Job Instance가 여러번 실행될 수 있는 것을 보여줌.
                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }



}
