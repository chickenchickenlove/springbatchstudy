package io.springbatch.springbatchlecture.dbitemreader.forsql;

import io.springbatch.springbatchlecture.dbitemreader.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
@Transactional
public class JpaRepository {

    private final EntityManager em;



    @Transactional
    public void testDataInject() {

        for (int i = 0; i < 1000; i++) {
            Customer customer = Customer.builder()
                    .firstName("customer" + i)
                    .lastName(i + "customer")
                    .birthDate(LocalDate.now())
                    .build();

            em.persist(customer);
            }

        for (int i = 0; i < 1000; i++) {
            Customer customer = Customer.builder()
                    .firstName("ali" + i)
                    .lastName(i + "ali")
                    .birthDate(LocalDate.now())
                    .build();
            em.persist(customer);
        }
        for (int i = 0; i < 1000; i++) {
            Customer customer = Customer.builder()
                    .firstName("bli" + i)
                    .lastName(i + "bli")
                    .birthDate(LocalDate.now())
                    .build();
            em.persist(customer);
        }
    }



}









