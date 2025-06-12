package com.example.Eksamensprojekt3sem.Siren;

import com.example.Eksamensprojekt3sem.Enum.Status;
import com.example.Eksamensprojekt3sem.FireEvent.FireEventModel;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "sirens")
public class SirenModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "siren_id")
    private int sirenId;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "status")
    private Status status;

    @Column(name = "disabled")
    private boolean disabled;

    @ManyToMany(mappedBy = "sirens")
    private Set<FireEventModel> fireEvents;

    public SirenModel() {}

    public SirenModel(double latitude, double longitude, Status status, boolean disabled) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.disabled = disabled;
    }

    public int getSirenId() {
        return sirenId;
    }

    public void setSirenId(int sirenId) {
        this.sirenId = sirenId;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<FireEventModel> getFireEvents() {
        return fireEvents;
    }

    public void setFireEvents(Set<FireEventModel> fireEvents) {
        this.fireEvents = fireEvents;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
