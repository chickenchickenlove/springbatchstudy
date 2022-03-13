package io.springbatch.springbatchlecture.customitemprocessor;

import io.springbatch.springbatchlecture.dbitemreader.Customer;
import lombok.Setter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.classify.Classifier;

import java.util.Map;

// C : Classifiable
// T : 반환될 값
@Setter
public class ProcessorClassifier<C,T> implements Classifier<C,T> {

    // 구분자 객체
    private Map<Integer, ItemProcessor<Customer, Customer>> paramMap;

    // Processor Return 로직.
    // ClassifierItemProcessor는 process 메서드를 할 때, classify 메서드에서 ItemProcessor를 전달받아야함.
    @Override
    public T classify(C classifiable) {
        Customer key = (Customer) classifiable;
        int i = key.getId().intValue() % 3;
        return (T)paramMap.get(i);
    }
}
