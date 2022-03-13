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
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@Configuration
@RequiredArgsConstructor
public class JpaPagingItemReaderConfig {

    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;
    private final EntityManagerFactory emf;

    private final static int CHUNK_SIZE = 10;

    @Bean
    public Job JdbcJob() {
        return jobBuilderFactory
                .get("JpaPagingItemReaderJob")
                .start(step100())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step step100() {
        return stepBuilderFactory
                .get("JpaPagingItemReaderStep")
                .<Customer, Customer>chunk(CHUNK_SIZE)
                .reader(customerItemReader())
                .processor(new ItemProcessor<Customer, Customer>() {
                    @Override
                    public Customer process(Customer item) throws Exception {
                        System.out.println("item =" + item);

                        return Customer.builder()
                                .id(item.getId())
                                .lastName(item.getLastName().toUpperCase(Locale.ROOT))
                                .firstName(item.getFirstName().toUpperCase(Locale.ROOT))
                                .birthDate(item.getBirthDate())
                                .build();
                    }
                })
                .writer(new ItemWriter<Customer>() {
                    @Override
                    public void write(List<? extends Customer> items) throws Exception {
                        System.out.println("=========================");
                        System.out.println("Jpa Paging ITEM READER WORK");
                        items.forEach(customer -> System.out.println("customer = " + customer));
                        System.out.println("=========================");
                    }
                })
                .build();
    }

    @Bean
    public ItemReader<Customer> customerItemReader() {

        HashMap<String, Object> paramValues = new HashMap<>();
        paramValues.put("idValue", 2091L);

        return new JpaPagingItemReaderBuilder<Customer>()
                .name("JpaPagingItemReaderJob")
                .entityManagerFactory(emf)
                .pageSize(2)
                .queryString("select c from Customer c where c.id = :idValue")
                .parameterValues(paramValues)
                .build();
    }



}
