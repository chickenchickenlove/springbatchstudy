package io.springbatch.springbatchlecture;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing // 스프링 Batch 작동하기 위한 Bean 등록
public class SpringbatchlectureApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbatchlectureApplication.class, args);
	}

}
