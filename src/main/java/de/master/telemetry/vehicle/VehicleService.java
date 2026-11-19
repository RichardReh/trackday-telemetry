
package de.master.telemetry.vehicle;

import de.master.telemetry.vehicle.dto.CreateVehicleRequest;
import de.master.telemetry.vehicle.dto.VehicleResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public VehicleResponse createVehicle(CreateVehicleRequest request) {

        Vehicle vehicle = new Vehicle(
                request.name(),
                request.manufacturer(),
                request.model(),
                request.year()
        );

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return toResponse(savedVehicle);
    }

    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public VehicleResponse getVehicleById(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() ->
                        new VehicleNotFoundException(id));

        return toResponse(vehicle);
    }

    private VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getName(),
                vehicle.getManufacturer(),
                vehicle.getModel(),
                vehicle.getYear()
        );
    }
}
