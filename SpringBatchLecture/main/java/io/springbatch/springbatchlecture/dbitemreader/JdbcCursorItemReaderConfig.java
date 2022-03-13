package io.springbatch.springbatchlecture.dbitemreader;

import io.springbatch.springbatchlecture.dbitemreader.Customer;
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
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.List;
import java.util.Locale;

//@Configuration
@RequiredArgsConstructor
public class JdbcCursorItemReaderConfig {

    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;
    private final DataSource dataSource;
    private final static int CHUNK_SIZE = 10;

    @Bean
    public Job JdbcJob() {
        return jobBuilderFactory
                .get("JcbcJob")
                .start(step100())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step step100() {
        return stepBuilderFactory
                .get("JdbcCursorItemStep")
                .<Customer, Customer>chunk(CHUNK_SIZE)
                .reader(customerItemReader())
                .processor(new ItemProcessor<Customer, Customer>() {
                    @Override
                    public Customer process(Customer item) throws Exception {
                        System.out.println("here");
                        return Customer.builder()
                                .lastName(item.getLastName().toUpperCase(Locale.ROOT))
                                .firstName(item.getFirstName().toUpperCase(Locale.ROOT))
                                .build();
                    }
                })
                .writer(new ItemWriter<Customer>() {
                    @Override
                    public void write(List<? extends Customer> items) throws Exception {
                        items.forEach(customer -> System.out.println("customer Name = " + customer.getFirstName()));
                    }
                })
                .build();
    }

    @Bean
    public ItemReader<Customer> customerItemReader() {
        return new JdbcCursorItemReaderBuilder()
                .name("myCustomItemReaderBuilder")
                .fetchSize(50)
                .dataSource(dataSource)
                .beanRowMapper(Customer.class)
                .sql("select id, first_Name, last_Name from customer order by last_Name")
//                .sql("select id, firstName, lastName from customer order by lastName where firstName = :first")
                .maxItemCount(20)
                .currentItemCount(0)
                .maxRows(100)
                .build();
    }


}
