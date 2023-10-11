package com.test.emailservice.domain.users;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "user")
public class User {

    private static final int DAILY_QUOTA = 1000;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    private String role;
    private int emailsSent;
    private LocalDate lastSentDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getEmailsSent() {
        return emailsSent;
    }

    public void setEmailsSent(int emailsSent) {
        this.emailsSent = emailsSent;
    }

    public LocalDate getLastSentDate() {
        return lastSentDate;
    }

    public void setLastSentDate(LocalDate lastSentDate) {
        this.lastSentDate = lastSentDate;
    }

    public int getDailyQuota() {
        return DAILY_QUOTA;
    }

}
