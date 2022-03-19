package io.springbatch.springbatchlecture.retry.partitioning;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor

public class CustomPartitioner implements Partitioner {

    private final EntityManager em;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        Tuple singleResult = em.createQuery("select min(c.customer_id), max(c.customer_id) from Customer c", Tuple.class).getSingleResult();
        int minValue = ((Long) singleResult.get(0)).intValue();
        int maxValue = ((Long) singleResult.get(1)).intValue();
        int eachValue = ((maxValue - minValue) / gridSize) + 1 ;

        HashMap<String, ExecutionContext> partition = new HashMap<>();

        int start = minValue;
        int end = eachValue;

        for (int i = 0; i < gridSize; i++) {

            // 값 셋팅
            ExecutionContext executionContext = new ExecutionContext();
            executionContext.put("start", start);
            executionContext.put("end", end);

            // 파티셔너에 넣기
            partition.put(String.valueOf(i), executionContext);

            // 다음 값을 셋팅하기
            start = end + 1;
            end = end + eachValue;

        }
        return partition;
    }
}
