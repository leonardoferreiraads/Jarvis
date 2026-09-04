package com.leo.IA;

import com.leo.IA.Tools.VagaTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class AgendadorVagas {

    @Autowired
    private VagaTools vagaTools;

    @Scheduled(cron = "0 0 8 * * MON-FRI")   // 8h, dias úteis
    public void enviarVagasManha() {
        vagaTools.buscarEEnviarVagas();
    }

    @Scheduled(cron = "0 0 12 * * MON-FRI")  // 12h, dias úteis
    public void enviarVagasAlmoco() {
        vagaTools.buscarEEnviarVagas();
    }

    @Scheduled(cron = "0 0 18 * * MON-FRI")  // 18h, dias úteis
    public void enviarVagasTarde() {
        vagaTools.buscarEEnviarVagas();
    }
}