package com.example.vniitesttask.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
@Table(name = "detail")
public class DetailEntity {

    /*
        Fields validated by dto
     */

    @Column(name = "master_doc_number")
    Long masterDocNumber;

    @Id
    @Column(name = "detail_name")
    String detailName;

    @Column(name = "cost_value")
    Long costValue;

    // Didn't use many-to-one relationship to prevent hibernate's attempts to select full table
}
