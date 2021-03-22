package com.dematic.bookStore.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
public class Author {

    public Author() {
    }

    public Author(String name, String lastName) {
        this.name = name;
        this.lastName = lastName;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String lastName;


    @ManyToMany(mappedBy = "authors")
    @JsonBackReference
    private Set<Book> books = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return id.equals(author.id) && name.equals(author.name) && lastName.equals(author.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastName);
    }
}
