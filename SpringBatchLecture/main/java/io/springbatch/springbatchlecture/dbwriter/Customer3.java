package io.springbatch.springbatchlecture.dbwriter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer3 {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "customer3_id")
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;


}
