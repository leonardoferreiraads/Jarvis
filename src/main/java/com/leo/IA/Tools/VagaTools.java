package com.leo.IA.Tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VagaTools {

    @Autowired
    private JarvisTools jarvisTools;

    @Autowired
    private GmailTools gmailTools;

    @Tool("Busca vagas de estágio ou junior em IA e desenvolvimento e envia por email")
    public String buscarEEnviarVagas() {
        String resultado = jarvisTools.buscarInfoTech(
                "vagas estágio junior IA inteligência artificial desenvolvedor São Paulo 2026"
        );

        // Formata cada vaga separadamente
        StringBuilder vagas = new StringBuilder();
        String[] linhas = resultado.split("\n\n");

        for (String vaga : linhas) {
            String[] partes = vaga.split("\n");
            if (partes.length >= 3) {
                String titulo = partes[0].replace("• ", "").trim();
                String link = partes[1].trim();
                String descricao = partes[2].trim();

                // Pega só as primeiras 150 letras do descritivo
                if (descricao.length() > 150) {
                    descricao = descricao.substring(0, 150) + "...";
                }

                vagas.append("¹").append(titulo).append("\n");
                vagas.append("²").append(link).append("\n");
                vagas.append("³").append(descricao).append("\n\n");
            }
        }

        String emailBody =
                "Fala, Léo! Separei as vagas do dia pra você:\n\n" +
                        vagas.toString() +
                        "---\n" +
                        "Vai lá e candida em pelo menos uma hoje. Você tem o perfil, falta só clicar.\n\n" +
                        "— Jarvis";

        gmailTools.enviarEmails(
                emailBody,
                "leonardodsf77@gmail.com",
                "Vagas do dia — " + java.time.LocalDate.now()
        );

        return "Email com vagas enviado com sucesso!";
    }
}