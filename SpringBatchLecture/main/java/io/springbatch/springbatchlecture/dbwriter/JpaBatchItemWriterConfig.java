package io.springbatch.springbatchlecture.dbwriter;

import io.springbatch.springbatchlecture.dbitemreader.Customer;
import io.springbatch.springbatchlecture.dbwriter.service.CustomServiceList;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

//@Configuration
@RequiredArgsConstructor
public class JpaBatchItemWriterConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory emf;



    @Bean
    @Qualifier("job1")
    public Job jpaBatchItemWriterJob() {
        return jobBuilderFactory
                .get("jpaBatchItemWriterJob2")
                .start(jpaBatchItemWriterStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }


    @Bean
    public Step jpaBatchItemWriterStep() {
        return stepBuilderFactory
                .get("jpaBatchItemWriterStep")
                .<Customer, Customer3>chunk(30000)
                .reader(customItemReaderJpa())
                .processor(new ItemProcessor<Customer, Customer3>() {
                    @Override
                    public Customer3 process(Customer item) throws Exception {
//                        System.out.println("item = " + item);

                        Customer3 customer3 = Customer3.builder()
//                                .id(item.getId())
                                .birthDate(item.getBirthDate())
                                .lastName(item.getFirstName())
                                .firstName(item.getLastName())
                                .build();
//                        System.out.println("translate = " + customer3);
                        return customer3;

                    }
                })
                .writer(customItemWriterJpaBatch())
                .build();
    }

    @Bean
    public ItemWriter<Customer3> customItemWriterJpaBatch() {
        return new JpaItemWriterBuilder<Customer3>()
                .entityManagerFactory(emf)
                .usePersist(true)
                .build();
    }


    @Bean
    public ItemReader<Customer> customItemReaderJpa() {
        return new JpaCursorItemReaderBuilder<Customer>()
                .name("name1")
                .queryString("select c from Customer c")
                .maxItemCount(30000)
                .currentItemCount(0)
                .entityManagerFactory(emf)
                .build();
    }

}

