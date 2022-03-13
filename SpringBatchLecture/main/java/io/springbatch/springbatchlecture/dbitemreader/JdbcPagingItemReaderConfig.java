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
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

//@Configuration
@RequiredArgsConstructor
public class JdbcPagingItemReaderConfig {

    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;
    private final DataSource dataSource;
    private final static int CHUNK_SIZE = 10;

    @Bean
    public Job JdbcJob() {
        return jobBuilderFactory
                .get("JcbcPagingItemReaderJob")
                .start(step100())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step step100() {
        return stepBuilderFactory
                .get("JcbcPagingItemReaderStep")
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
                        System.out.println("JDBC Paging ITEM READER WORK");
                        items.forEach(customer -> System.out.println("customer = " + customer));
                        System.out.println("=========================");
                    }
                })
                .build();
    }

    @Bean
    public ItemReader<Customer> customerItemReader() {

        HashMap<String, Order> sortKey = new HashMap<>();
        sortKey.put("first_name", Order.ASCENDING);

        return new JdbcPagingItemReaderBuilder<Customer>()
                .name("JdbcPagingItemReaderJob")
                .dataSource(dataSource)
                .pageSize(2)
                .beanRowMapper(Customer.class)
//                .fetchSize(CHUNK_SIZE)
                .currentItemCount(0)
                .maxItemCount(100)
                .selectClause("first_name, last_name, birth_date, customer_id")
                .fromClause("from Customer")
                .sortKeys(sortKey)
                .build();
    }



}
