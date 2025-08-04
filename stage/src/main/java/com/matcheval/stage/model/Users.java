package com.matcheval.stage.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Date dnaiss;
    @Enumerated(EnumType.STRING)
    private Civility civility;
    private String phonenumber;
    private Date createdAt;
    private LocalDateTime lastLogin;

    private boolean locked=false;
    private boolean enabled=true;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Roles role;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    @JsonBackReference
    private Users manager;

    // ➕ Ajout : liste des recruteurs pour ce manager
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL)
    @JsonManagedReference

    private List<Users> recruteurs;

}
