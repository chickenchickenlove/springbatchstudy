package io.springbatch.springbatchlecture.dbwriter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer2 {

    @Id
    @GeneratedValue
    @Column(name = "customer2_id")
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;


}
