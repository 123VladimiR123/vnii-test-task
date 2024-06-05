package com.example.vniitesttask.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
@Table(name = "master")
public class MasterEntity {

    /*
        Fields validated by DTO,
        creationDate is provided by mapper
     */

    @Id
    @Column(name = "doc_number")
    Long id;

    @Column(name = "creation_date")
    LocalDateTime creationDate;

    @Column(name = "cost_value_sum")
    Long cost;

    @Column(name = "description")
    String description;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_doc_number")
    List<DetailEntity> detailEntityList;
}
