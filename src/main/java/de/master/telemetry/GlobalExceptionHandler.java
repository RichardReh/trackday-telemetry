package de.master.telemetry;

import de.master.telemetry.vehicle.VehicleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VehicleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleVehicleNotFound(VehicleNotFoundException ex) {
        return ex.getMessage();
    }
}