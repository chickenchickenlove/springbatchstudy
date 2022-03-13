package io.springbatch.springbatchlecture.dbwriter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
@Transactional
public class FindRepository {

    private final EntityManager em;


    public int findCustomerSize() {
        return em.createQuery("select c from Customer c")
                .getResultList().size();
    }

    public int findCustomer2Size() {
        return em.createQuery("select c from Customer2 c")
                .getResultList().size();
    }

    public int findCustomer3Size() {
        return em.createQuery("select c from Customer3 c")
                .getResultList().size();
    }





}
