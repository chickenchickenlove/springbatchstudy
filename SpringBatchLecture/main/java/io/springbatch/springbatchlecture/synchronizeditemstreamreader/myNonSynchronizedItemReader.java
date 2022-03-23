package io.springbatch.springbatchlecture.retry.synchronizeditemstreamreader;

import org.springframework.batch.item.*;

public class myNonSynchronizedItemReader implements org.springframework.batch.item.ItemStreamReader<String> {

    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return null;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {

    }
}
