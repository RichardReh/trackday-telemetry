
package de.master.telemetry.vehicle;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String manufacturer;

    @Column(nullable = false)
    private String model;

    private Integer year;

    protected Vehicle() {
        // Required by JPA
    }

    public Vehicle(String name, String manufacturer,
                   String model, Integer year) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.model = model;
        this.year = year;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public Integer getYear() {
        return year;
    }
}
