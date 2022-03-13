package io.springbatch.springbatchlecture.customitemprocessor;

import io.springbatch.springbatchlecture.dbitemreader.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.support.builder.ClassifierCompositeItemProcessorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ClassifierCompositeItemProcessorConfig {

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
                .processor(custom1ItemProcessor())
                .writer(new ItemWriter<Customer>() {
                    @Override
                    public void write(List<? extends Customer> items) throws Exception {
                        for (Customer item : items) {
                            System.out.println("item = " + item);
                        }
                    }
                })
                .build();
    }

    public ItemProcessor<? super Customer, ? extends Customer> custom1ItemProcessor() {

        // ClassifierCompositeItemProcessor Builder 클래스 생성
        ClassifierCompositeItemProcessorBuilder<Customer, Customer> builder = new ClassifierCompositeItemProcessorBuilder<>();

        // Builder 클래스에 전달한 Classifier 클래스 생성
        ProcessorClassifier<? super Customer, ItemProcessor<?, ? extends Customer>> classifier2 = new ProcessorClassifier<>();

        //Classifier 내부에 가지고 있는 ParamMap(구분자 생성 및 초기화)
        HashMap<Integer, ItemProcessor<Customer, Customer>> paramMap = new HashMap<>();
        paramMap.put(0, new CustomItemProcessor1());
        paramMap.put(1, new CustomItemProcessor2());
        paramMap.put(2, new CustomItemProcessor3());

        //Classifier 셋팅
        classifier2.setParamMap(paramMap);

        // Builder를 통해서 Processor 생성
        return builder.classifier(classifier2).build();
    }

    @Bean
    public ItemReader<? extends Customer> customJpaItemReader() {
        return new JpaCursorItemReaderBuilder<Customer>()
                .name("abc")
                .queryString("select c from Customer c")
                .currentItemCount(0)
                .maxItemCount(1000)
                .entityManagerFactory(emf)
                .build();
    }


}
