package io.springbatch.springbatchlecture.customitemprocessor;

import io.springbatch.springbatchlecture.dbitemreader.Customer;
import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor1 implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer item) throws Exception {
        System.out.println("Classifier customItemProcessor1 called!");
        return item;
    }
}
