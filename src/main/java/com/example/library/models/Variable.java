package com.example.library.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "variables")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Variable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String value;

    public Variable(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
