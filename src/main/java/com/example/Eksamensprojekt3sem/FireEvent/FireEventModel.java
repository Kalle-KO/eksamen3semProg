package com.example.Eksamensprojekt3sem.FireEvent;

import com.example.Eksamensprojekt3sem.Siren.SirenModel;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "fire_event")
public class FireEventModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fire_event_id")
    private int FireEventId;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "closed")
    private boolean closed;

    @ManyToMany
    @JoinTable(
            name = "fire_event_siren",
            joinColumns = @JoinColumn(name = "fire_event_id"),
            inverseJoinColumns = @JoinColumn(name = "siren_id")
    )
    private Set<SirenModel> sirens =  new HashSet<>();

    public FireEventModel() {}

    public FireEventModel(int fireEventId, double latitude, double longitude, LocalDateTime timestamp, boolean closed, Set<SirenModel> sirens) {
        FireEventId = fireEventId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.closed = closed;
        this.sirens = sirens;
    }

    public int getFireEventId() {
        return FireEventId;
    }

    public void setFireEventId(int fireEventId) {
        FireEventId = fireEventId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Set<SirenModel> getSirens() {
        return sirens;
    }

    public void setSirens(Set<SirenModel> sirens) {
        this.sirens = sirens;
    }
}
