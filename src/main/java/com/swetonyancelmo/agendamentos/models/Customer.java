package com.swetonyancelmo.agendamentos.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity()
@Table(name = "tb_customers")
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;
}
