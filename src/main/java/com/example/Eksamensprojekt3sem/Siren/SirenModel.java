package com.example.Eksamensprojekt3sem.Siren;

import com.example.Eksamensprojekt3sem.Enum.Status;
import com.example.Eksamensprojekt3sem.FireEvent.FireEventModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Set;

@Entity
@Table(name = "sirens")
public class SirenModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "siren_id")
    private int sirenId;

    @NotNull
    @Min(value = -90, message = "Latitude has to be -90 at minimum")
    @Max(value = 90, message = "Latitude has to be 90 at maximum")
    @Column(name = "latitude")
    private double latitude;

    @NotNull
    @Min(value = -180, message = "Longitude has to be -180 at minimum")
    @Max(value = 180, message = "Longitude has to be 180 at maximum")
    @Column(name = "longitude")
    private double longitude;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "disabled")
    private boolean disabled;

    @ManyToMany(mappedBy = "sirens", cascade = CascadeType.ALL)
    @JsonIgnore // <-- spring Jackson-serialisering af fireEvents
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
