package de.master.telemetry.vehicle;

import de.master.telemetry.vehicle.dto.CreateVehicleRequest;
import de.master.telemetry.vehicle.dto.VehicleResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse createVehicle(
            @Valid @RequestBody CreateVehicleRequest request) {

        return vehicleService.createVehicle(request);
    }

    @GetMapping
    public List<VehicleResponse> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @GetMapping("/{id}")
    public VehicleResponse getVehicleById(@PathVariable UUID id) {
        return vehicleService.getVehicleById(id);
    }
}