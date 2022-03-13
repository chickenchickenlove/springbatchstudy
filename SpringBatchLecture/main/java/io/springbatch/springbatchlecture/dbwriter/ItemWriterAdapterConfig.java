package io.springbatch.springbatchlecture.dbwriter;

import io.springbatch.springbatchlecture.dbitemreader.Customer;
import io.springbatch.springbatchlecture.dbwriter.service.CustomServiceList;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration
@RequiredArgsConstructor
public class ItemWriterAdapterConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory emf;


    @Bean
    public Job itemWriterAdapterJob() {
        return jobBuilderFactory
                .get("itemWriterAdapterJob")
                .start(itemWriterAdapterStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step itemWriterAdapterStep(){
        return stepBuilderFactory
                .get("itemWriterAdapterStep")
                .<Customer, Customer>chunk(10)
                .reader(customItemReader2())
                .writer(customItemAdapter())
                .build();


    }

    public ItemWriter<Customer> customItemAdapter() {

        ItemWriterAdapter<Customer> adapter = new ItemWriterAdapter<>();
        adapter.setTargetMethod("writeMember");
        adapter.setTargetObject(new CustomServiceList<Customer>());

        return adapter;
    }



    public ItemReader<Customer> customItemReader2() {
        return new JpaCursorItemReaderBuilder<Customer>()
                .name("customItemBuilder")
                .entityManagerFactory(emf)
                .queryString("select c from Customer c")
                .maxItemCount(1000)
                .currentItemCount(0)
                .build();
    }






}
