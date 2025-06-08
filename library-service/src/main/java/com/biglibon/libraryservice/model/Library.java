package com.biglibon.libraryservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "libraries")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Library {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "librarySeq")
    @SequenceGenerator(name = "librarySeq", sequenceName = "library_seq")
    private Long id;

    private String name;
    private String city;
    private String phone;

    @ElementCollection
    private List<String> bookIds;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;


    public Library(String name, String city, String phone) {
        this.name = name;
        this.city = city;
        this.phone = phone;
    }

    public Library(String name, String city, String phone, List<String> bookIds) {
        this.name = name;
        this.city = city;
        this.phone = phone;
        this.bookIds = bookIds;
    }
}