package com.Academy.model;

import com.Academy.common.UserType;
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

    @Column(name = "reset_token")
    private String resetToken;
    private String profilePictureUrl;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Children> children = new ArrayList<>();

    public void addChild(Children child) {
        child.setUser(this);
        this.children.add(child);
    }

    public Object getName() {
        return username;
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
