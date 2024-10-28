package com.Academy.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String gender;
    private String email;
    private String password;
    private String mobile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Children> children = new ArrayList<>();

    public void addChild(Children child) {
        child.setUser(this);  // Set the user reference in each child
        this.children.add(child);  // Add child to the list
    }

    @Entity
    @Table(name = "children")
    @Getter
    @Setter
    public static class Children {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
        private String age;
        private String gender;

        @ManyToOne
        @JoinColumn(name = "user_id")
        @JsonBackReference
        private User user;
    }
}
