package io.springbatch.springbatchlecture.customitemprocessor;

import io.springbatch.springbatchlecture.dbitemreader.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManagerFactory;

//@Configuration
@RequiredArgsConstructor
public class CompositeItemProcessorConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory emf;


    @Bean
    public Job compositeItemProcessorJob() {
        return jobBuilderFactory
                .get("compositeItemProcessorJob")
                .start(compositeItemProcessorStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step compositeItemProcessorStep() {
        return stepBuilderFactory
                .get("compositeItemProcessorStep")
                .<Customer, Customer>chunk(10)
                .reader(customJpaItemReader())
                .processor(customItemProcessor())
                .writer(items -> items.forEach(System.out::println))
                .build();

    }

    @Bean
    public ItemProcessor<Customer, Customer> customItemProcessor() {
        CompositeItemProcessorBuilder<Customer, Customer> builder = new CompositeItemProcessorBuilder<>();
        return builder.
                delegates(subItemProcessor2(), subItemProcessor1())
                .build();
    }

    @Bean
    public ItemProcessor<Customer, Customer> subItemProcessor1(){
        return item -> {
            System.out.println("subItemProcessor1 Called!");
            System.out.println("item = " + item);
            return item;
        };
    }

    @Bean
    public ItemProcessor<Customer, Customer> subItemProcessor2(){
        return item -> {
            System.out.println("subItemProcessor2 Called!");
            System.out.println("item = " + item);
            return item;
        };
    }

    @Bean
    public ItemReader<Customer> customJpaItemReader() {
        return new JpaCursorItemReaderBuilder<Customer>()
                .name("builder1")
                .queryString("select c from Customer c")
                .entityManagerFactory(emf)
                .maxItemCount(10)
                .currentItemCount(0)
                .build();
    }


}
