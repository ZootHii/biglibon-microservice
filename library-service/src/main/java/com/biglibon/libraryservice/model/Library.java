package com.biglibon.libraryservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private long id;

    @ElementCollection
    private List<Long> bookIds;
}