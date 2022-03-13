package io.springbatch.springbatchlecture.dbitemreader.forsql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class JpaConfig {

    private final JpaRepository jpaRepository;

    @RequestMapping("/testdata")
    @ResponseBody
    public String testDataInject() {
        jpaRepository.testDataInject();
        return "ok";
    }




}
