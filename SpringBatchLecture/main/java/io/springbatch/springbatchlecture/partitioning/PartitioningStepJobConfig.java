package io.springbatch.springbatchlecture.retry.partitioning;

import io.springbatch.springbatchlecture.dbitemreader.Customer;
import io.springbatch.springbatchlecture.dbwriter.Customer2;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.SimplePartitioner;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@RequiredArgsConstructor
public class PartitioningStepJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final CustomPartitioner customPartitioner;


    @Bean
    public Job partitionBatchJob() {
        return jobBuilderFactory.get("partitionBatchJob")
                .incrementer(new RunIdIncrementer())
                .start(masterStep())
                .build();
    }


    @Bean
    public Step masterStep() {
        return stepBuilderFactory.get("partitioningStep")
                .partitioner("slaveStep", customPartitioner)
                .step(slaveStep())
                .gridSize(4)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public Step slaveStep() {
        return stepBuilderFactory.get("slaveStepMaster")
                .<Customer, Customer2>chunk(1000)
                .reader(pagingItemReader(null,null))
                .writer(batchWriter())
                .processor(batchProcessor())
                .build();
    }


    @Bean
    @StepScope
    public JdbcPagingItemReader<Customer> pagingItemReader(
            @Value("#{stepExecutionContext['start']}") Integer start,
            @Value("#{stepExecutionContext['end']}") Integer end
            ){

        System.out.println("start = " + start + " end = " + end);
        System.out.println("Partition Step Process");
        System.out.println();


        HashMap<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("customer_id", Order.ASCENDING);


        return new JdbcPagingItemReaderBuilder<Customer>()
                .name("pagingBuilder")
                .dataSource(dataSource)
                .fetchSize(1000)
                .beanRowMapper(Customer.class)
                .selectClause("customer_id, first_name, last_name, birth_date")
                .fromClause("from customer")
                .whereClause("where customer_id >= " + start + " and customer_id <= " + end)
                .sortKeys(sortKeys)
                .build();
    }


    @Bean
    @StepScope
    public ItemProcessor<? super Customer,? extends Customer2> batchProcessor() {
        return (ItemProcessor<Customer, Customer2>) item -> Customer2.builder()
                .id(item.getCustomer_id())
                .birthDate(item.getBirthDate())
                .lastName(item.getLastName())
                .firstName(item.getFirstName()).build();
    }

    @Bean
    @StepScope
    public ItemWriter<? super Customer2> batchWriter() {
        return new JdbcBatchItemWriterBuilder<Customer2>()
                .sql("INSERT INTO Customer2(customer2_id, birth_date, first_name, last_name) values (:id, :birthDate, :firstName, :lastName)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }


}
