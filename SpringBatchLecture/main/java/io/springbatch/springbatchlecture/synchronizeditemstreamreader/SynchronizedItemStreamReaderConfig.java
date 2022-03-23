package io.springbatch.springbatchlecture.retry.synchronizeditemstreamreader;

import io.springbatch.springbatchlecture.dbitemreader.Customer;
import io.springbatch.springbatchlecture.dbwriter.Customer2;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@RequiredArgsConstructor
@Configuration
public class SynchronizedItemStreamReaderConfig<JdbcBatchItemReader> {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory emf;
    private final DataSource dataSource;


    @Bean
    public Job SynchronizedItemStreamReaderJob() {
        return jobBuilderFactory.get("SynchronizedItemStreamReaderJob")
                .incrementer(new RunIdIncrementer())
                .start(synchronizedItemStreamReaderStep())
                .build();
    }

    @Bean
    public Step synchronizedItemStreamReaderStep() {
        return stepBuilderFactory.get("SynchronizedItemStreamReaderStep")
                .<Customer, Customer2>chunk(10)
                .reader(myItemStreamReader())
//                .reader(myNonSynchronizedItemReader())
                .processor(new ItemProcessor<Customer, Customer2>() {
                    @Override
                    public Customer2 process(Customer item) throws Exception {
                        System.out.println(" >> Now Thread : " + Thread.currentThread().getName() + " Item : " + item.getCustomer_id());

                        return Customer2.builder()
                                .id(item.getCustomer_id())
                                .firstName(item.getFirstName())
                                .lastName(item.getLastName())
                                .birthDate(item.getBirthDate())
                                .build();
                    }
                })
                .writer(myItemStreamWriter())
                .taskExecutor(myTaskExecutor())
                .build();
    }

    @Bean
    public TaskExecutor myTaskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setThreadNamePrefix("not-safety-thread");

        return executor;
    }

    @StepScope
    @Bean
    public JdbcBatchItemWriter<Customer2> myItemStreamWriter() {
        return new JdbcBatchItemWriterBuilder<Customer2>()
                .sql("insert into Customer2(customer2_id, birth_date, first_name, last_name) values(:id, :birthDate, :firstName, :lastName)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    @StepScope
    @Bean
    public SynchronizedItemStreamReader<Customer> myItemStreamReader() {
        SynchronizedItemStreamReaderBuilder<Customer> returnBuilder = new SynchronizedItemStreamReaderBuilder<>();
        SynchronizedItemStreamReader<Customer> build = returnBuilder.delegate(myNonSynchronizedItemReader()).build();
        return build;
    }

    @StepScope
    @Bean
    public JpaCursorItemReader<Customer> myNonSynchronizedItemReader() {
        return new JpaCursorItemReaderBuilder<Customer>()
                .queryString("select c from Customer c")
                .name("myNonSynchronizedItemReader")
                .maxItemCount(100)
                .currentItemCount(0)
                .entityManagerFactory(emf)
                .build();
    }


}
