package com.nttdata.ms_reservas.servicio;

import com.nttdata.ms_reservas.model.Reserva;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/** Almacén en memoria de las reservas. */
@Repository
public class ReservaRepository {

    private final List<Reserva> reservas = new ArrayList<>();
    private final AtomicLong secuencia = new AtomicLong();

    public synchronized Reserva guardar(Reserva reserva) {
        Reserva conId = new Reserva(secuencia.incrementAndGet(), reserva.vehiculoId(), reserva.placa(),
                reserva.cliente(), reserva.dias(), reserva.total(), reserva.estado());
        reservas.add(conId);
        return conId;
    }

    // RN-05
    public synchronized boolean tieneReservaActiva(Long vehiculoId) {
        return reservas.stream().anyMatch(r -> r.vehiculoId().equals(vehiculoId));
    }

    public List<Reserva> listar() {
        return Collections.unmodifiableList(reservas);
    }
}
