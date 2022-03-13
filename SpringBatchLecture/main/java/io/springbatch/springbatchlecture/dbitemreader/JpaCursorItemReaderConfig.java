package io.springbatch.springbatchlecture.dbitemreader;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@Configuration
@RequiredArgsConstructor
public class JpaCursorItemReaderConfig {

    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;
    private final DataSource dataSource;
    private final EntityManagerFactory emf;
    private final static int CHUNK_SIZE = 100;



    @Bean
    public Job jpaCursorItemReaderJob() {
        return jobBuilderFactory
                .get("jpaCursorItemReaderJob")
                .start(stepJpa())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step stepJpa() {
        return stepBuilderFactory
                .get("jpaItemReaderStep")
                .<Customer, Customer>chunk(CHUNK_SIZE)
                .reader(customerJpaItemReader())
                .processor(new ItemProcessor<Customer, Customer>() {
                    @Override
                    public Customer process(Customer item) throws Exception {
                        return Customer.builder()
                                .firstName(item.getFirstName().toUpperCase(Locale.ROOT))
                                .lastName(item.getLastName().toUpperCase(Locale.ROOT))
                                .birthDate(item.getBirthDate())
                                .build();
                    }
                })
                .writer(new ItemWriter<Customer>() {
                    @Override
                    public void write(List<? extends Customer> items) throws Exception {
                        System.out.println("=========================");
                        System.out.println("JPA ITEM READER WORK");
                        items.forEach(customer -> System.out.println("customer.getFirstName() = " + customer.getFirstName()));
                        System.out.println("=========================");
                    }
                })
                .build();

    }

    @Bean
    public ItemReader<Customer> customerJpaItemReader() {

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("firstname", "%customer0");


        return new JpaCursorItemReaderBuilder<Customer>()
                .name("jpaCursorItemReader")
                .entityManagerFactory(emf)
                .queryString("select c from Customer c")
//                .queryString("select c from Customer c where firstName = :firstname")
//                .queryString("select c from Customer c where firstName like :firstname")
//                .parameterValues(paramMap)
                .maxItemCount(10)
                .currentItemCount(0)
                .build();
        }
    }
