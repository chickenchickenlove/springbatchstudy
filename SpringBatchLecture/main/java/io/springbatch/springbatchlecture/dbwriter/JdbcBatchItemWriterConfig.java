package io.springbatch.springbatchlecture.dbwriter;

import io.springbatch.springbatchlecture.dbitemreader.Customer;
import io.springbatch.springbatchlecture.dbitemreader.service.CustomService;
import io.springbatch.springbatchlecture.dbwriter.service.CustomServiceList;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.JobStepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.management.MXBean;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Locale;

@Configuration
@RequiredArgsConstructor
public class JdbcBatchItemWriterConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final EntityManagerFactory emf;


    @Bean
    @Qualifier("job2")
    public Job jdbcBatchItemWriterJob() {
        return jobBuilderFactory
                .get("jdbcBatchItemWriterJob2")
                .start(jdbcBatchItemWriterStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }


    @Bean
    public Step jdbcBatchItemWriterStep() {
        return stepBuilderFactory
                .get("jdbcBatchItemWriterStep")
                .<Customer, Customer2>chunk(30000)
                .reader(customItemReaderJdbc())
                .processor(new ItemProcessor<Customer, Customer2>() {

                    private Long key = 0L;

                    @Override
                    public Customer2 process(Customer item) throws Exception {
//                        System.out.println("item = " + item);

                        Customer2 customer2 = Customer2.builder()
                                .id(item.getId())
                                .birthDate(item.getBirthDate())
                                .lastName(item.getFirstName())
                                .firstName(item.getLastName())
                                .build();
//                        System.out.println("translate = " + customer2);
                        return customer2;

                    }
                })

                .writer(customItemWriterJdbcBatch())
                .build();
    }

    @Bean
    public ItemWriter<Customer2> customItemWriterJdbcBatch() {

        return new JdbcBatchItemWriterBuilder<Customer2>()
                .dataSource(dataSource)
                .sql("insert into customer2(customer2_id, first_Name, last_Name, birth_Date) values (:id, :firstName, :lastName, :birthDate)")
                .beanMapped()
                .build();
    }


    @Bean
    public ItemReader<Customer> customItemReaderJdbc() {
        return new JpaCursorItemReaderBuilder<Customer>()
                .name("name1")
                .queryString("select c from Customer c")
                .maxItemCount(30000)
                .currentItemCount(0)
                .entityManagerFactory(emf)
                .build();
    }
}
