package io.springbatch.springbatchlecture.retry.multthreadchunk;

import io.springbatch.springbatchlecture.dbitemreader.Customer;
import io.springbatch.springbatchlecture.dbwriter.Customer2;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.item.ChunkOrientedTasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.batch.repeat.support.TaskExecutorRepeatTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.batch.api.listener.JobListener;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class MultiThreadChunkConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory emf;
    private final DataSource dataSource;


    @Bean
    public Job multiThreadedJob() {
        return jobBuilderFactory.get("multiThreadJob")
                .incrementer(new RunIdIncrementer())
                .start(multiThreadedStep())
                .build();
    }

    @Bean
    public Step multiThreadedStep() {
        return stepBuilderFactory.get("multiThreadStep")
                .<Customer, Customer2>chunk(1000)
                .reader(multiThreadItemReader())
                .processor((ItemProcessor<Customer, Customer2>) item -> Customer2.builder()
                        .id(item.getCustomer_id())
                        .firstName(item.getFirstName())
                        .lastName(item.getLastName())
                        .birthDate(item.getBirthDate())
                        .build())
                .writer(multiThreadItemWriter())
                .taskExecutor(myTaskExecutor())
                .build();
    }

    @Bean
    public ItemWriter<Customer2> multiThreadItemWriter() {
        return new JdbcBatchItemWriterBuilder<Customer2>()
                .sql("insert into Customer2(customer2_id, birth_date, first_name, last_name) values(:id, :birthDate, :firstName, :lastName)")
                .dataSource(dataSource)
                .beanMapped()
                .build();

    }
    @Bean
    public JpaPagingItemReader<Customer> multiThreadItemReader() {
        return new JpaPagingItemReaderBuilder<Customer>()
                .name("builder Name")
                .currentItemCount(0)
                .pageSize(1000)
                .maxItemCount(1000)
                .entityManagerFactory(emf)
                .queryString("select c from Customer c")
                .build();
    }

    @Bean
    public TaskExecutor myTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("Non-Thread-Free");
        executor.setMaxPoolSize(8);
        executor.setCorePoolSize(4);
        return executor;
    }


}

