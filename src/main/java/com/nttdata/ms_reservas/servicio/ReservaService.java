package com.nttdata.ms_reservas.servicio;

import com.nttdata.ms_reservas.cliente.VehiculoClient;
import com.nttdata.ms_reservas.model.Reserva;
import com.nttdata.ms_reservas.model.ReservaRequest;
import com.nttdata.ms_reservas.model.Vehiculo;
import org.springframework.stereotype.Service;

import java.util.List;

/** Reglas de negocio de las reservas. */
@Service
public class ReservaService {

    private static final int DIAS_MIN = 1;
    private static final int DIAS_MAX = 30;
    private static final int DIAS_DESCUENTO = 7;
    private static final double DESCUENTO = 0.10;
    private static final String CONFIRMADA = "CONFIRMADA";

    private final VehiculoClient vehiculoClient;
    private final ReservaRepository repository;

    public ReservaService(VehiculoClient vehiculoClient, ReservaRepository repository) {
        this.vehiculoClient = vehiculoClient;
        this.repository = repository;
    }

    public Reserva crear(ReservaRequest req) {
        validar(req);                                                        // RN-01
        Vehiculo vehiculo = vehiculoClient.buscar(req.vehiculoId());         // RN-06 / RN-07
        if (!vehiculo.disponible()) {
            throw new VehiculoNoDisponibleException("VEHICULO NO DISPONIBLE"); // RN-04
        }
        if (repository.tieneReservaActiva(vehiculo.id())) {
            throw new VehiculoNoDisponibleException("YA RESERVADO");           // RN-05
        }
        double total = calcularTotal(vehiculo.tarifaDia(), req.dias());
        return repository.guardar(new Reserva(null, vehiculo.id(), vehiculo.placa(),
                req.cliente().trim(), req.dias(), total, CONFIRMADA));
    }

    public List<Reserva> listar() {
        return repository.listar();
    }

    // RN-02 y RN-03
    private double calcularTotal(double tarifaDia, int dias) {
        double total = tarifaDia * dias;
        if (dias >= DIAS_DESCUENTO) {
            total = total * (1 - DESCUENTO);
        }
        return Math.round(total * 100.0) / 100.0;
    }

    // RN-01
    private void validar(ReservaRequest req) {
        if (req == null || req.vehiculoId() == null) {
            throw new IllegalArgumentException("El vehículo es obligatorio");
        }
        if (req.cliente() == null || req.cliente().isBlank()) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        if (req.dias() < DIAS_MIN || req.dias() > DIAS_MAX) {
            throw new IllegalArgumentException("Los días deben estar entre " + DIAS_MIN + " y " + DIAS_MAX);
        }
    }
}
