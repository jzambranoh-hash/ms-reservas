package com.nttdata.ms_reservas.web;

import com.nttdata.ms_reservas.cliente.CatalogoNoDisponibleException;
import com.nttdata.ms_reservas.cliente.VehiculoNoEncontradoException;
import com.nttdata.ms_reservas.model.Reserva;
import com.nttdata.ms_reservas.model.ReservaRequest;
import com.nttdata.ms_reservas.servicio.ReservaService;
import com.nttdata.ms_reservas.servicio.VehiculoNoDisponibleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** API REST de reservas. */
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Reserva> crear(@RequestBody ReservaRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(req));
        } catch (IllegalArgumentException e) {
            return error(req, HttpStatus.BAD_REQUEST, "DATOS INVALIDOS");
        } catch (VehiculoNoDisponibleException e) {
            return error(req, HttpStatus.CONFLICT, e.getMessage());
        } catch (VehiculoNoEncontradoException e) {
            return error(req, HttpStatus.NOT_FOUND, "VEHICULO NO EXISTE");
        } catch (CatalogoNoDisponibleException e) {
            return error(req, HttpStatus.SERVICE_UNAVAILABLE, "CATALOGO NO DISPONIBLE");
        }
    }

    @GetMapping
    public List<Reserva> listar() {
        return service.listar();
    }

    /** Construye la respuesta de error. (Resuelto) */
    private ResponseEntity<Reserva> error(ReservaRequest req, HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado)
                .body(new Reserva(null, req.vehiculoId(), null, req.cliente(), req.dias(), 0, mensaje));
    }
}
