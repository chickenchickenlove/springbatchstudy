package io.springbatch.springbatchlecture.retry.skip;

import io.springbatch.springbatchlecture.retry.faulttolerent.SkippableException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipException;
import org.springframework.batch.item.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SkipStepConfig {

    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;

    @Bean
    public Job skipJob() {
        return jobBuilderFactory.get("skipJob")
                .incrementer(new RunIdIncrementer())
                .start(skipStep1())
                .build();
    }

    @Bean
    public Step skipStep1() {
        return stepBuilderFactory.get("skipStep1")
                .<String, String>chunk(5)
                .reader(customReader())
                .processor(customProcessor())
                .writer(customWriter())
                .faultTolerant()
                .skip(SkipException1.class)
                .noSkip(noSkipException.class)
                .skipLimit(5)
                .build();
    }

    private ItemWriter<String> customWriter() {
        return new ItemWriter<String>() {
            @Override
            public void write(List<? extends String> items) throws Exception {
                for (String item : items) {
                    if (item.equals("item13") || item.equals("item14")) {
                        System.out.println(" >>  itemWriter Exception = " + item);
                        throw new SkipException1();
                    } else {
                        System.out.println("itemWriter Completed = " + item);
                    }
                }
            }
        };


    }

    private ItemProcessor<String, String> customProcessor() {
        return new ItemProcessor<String, String>() {
            @Override
            public String process(String item) throws Exception {
                if (item.equals("item3")) {
                    System.out.println(" >> itemProcessor Exception = " + item);
                    throw new SkipException1();
                } else {
                    System.out.println("itemProcessor Completed = " + item);
                    return item;
                }
            }
        };
    }

    private ItemReader<String> customReader() {
        return new ItemReader<String>() {
            int i = 0;
            @Override
            public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                i++;
                if ((i == 6) || (i == 7)) {
                    System.out.println(" >>  itemReader Exception = item" + i);
                    throw new SkipException1();
                } else {
                    System.out.println("itemReader = item" + i);
                    return i > 20 ? null : "item" + i;
                }
            }
        };
    }
}
