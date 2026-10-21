package com.example.order_management.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_email",
                        columnNames = "email"
                )
        })
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false,length = 255)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    protected UserEntity(){}

    public UserEntity(String email, String passwordHash){
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public UUID getId() {
        return id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getEmail() {
        return email;
    }
}
