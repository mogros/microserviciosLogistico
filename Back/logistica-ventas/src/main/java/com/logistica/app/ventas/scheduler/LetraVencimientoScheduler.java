package com.logistica.app.ventas.scheduler;

import com.logistica.app.ventas.entity.LetraVenta;
import com.logistica.app.ventas.repository.LetraVentaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class LetraVencimientoScheduler {

    private static final Logger log = LoggerFactory.getLogger(LetraVencimientoScheduler.class);
    @Autowired private LetraVentaRepository letraRepo;

    /** Corre todos los dias a medianoche */
    @Scheduled(cron = "0 1 0 * * *")
    @Transactional
    public void marcarLetrasVencidas() {
        List<LetraVenta> vencidas = letraRepo
            .findByFechaVencimientoBeforeAndEstado(LocalDate.now(), LetraVenta.EstadoLetra.PENDIENTE);
        if (!vencidas.isEmpty()) {
            vencidas.forEach(l -> { l.setEstado(LetraVenta.EstadoLetra.VENCIDO); letraRepo.save(l); });
            log.warn("{} letras marcadas como VENCIDAS", vencidas.size());
        }
    }

    /** Tambien verifica al arrancar el microservicio */
    @Scheduled(initialDelay = 10000, fixedDelay = Long.MAX_VALUE)
    @Transactional
    public void verificarAlArrancar() {
        log.info("Verificando letras vencidas al arrancar...");
        marcarLetrasVencidas();
    }
}
