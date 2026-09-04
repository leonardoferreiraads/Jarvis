package com.leo.IA.Tools;

import dev.langchain4j.agent.tool.Tool;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Properties;

@Component
public class GmailTools {

    @Value("${spring.mail.username}")
    private String email;

    @Value("${spring.mail.password}")
    private String senha;

    @Autowired
    private JavaMailSender mailSender;

    @Tool("Enviar email")
    public String enviarEmails(
            String mensagem,
            String destinatario,
            String assunto
    ) {

        try {
            SimpleMailMessage email = new SimpleMailMessage();

            email.setTo(destinatario);
            email.setSubject(assunto);
            email.setText(mensagem);

            mailSender.send(email);

            return "Email enviado com sucesso para " + destinatario;

        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao enviar email: " + e.getMessage();
        }
    }
    @Tool("Ler os últimos emails recebidos")
    public String lerEmails() {
        try {
            Properties props = new Properties();

            props.put("mail.store.protocol", "imaps");

            Session session = Session.getDefaultInstance(props);

            Store store = session.getStore("imaps");

            store.connect(
                    "imap.gmail.com",
                    email,
                    senha
            );

            Folder inbox = store.getFolder("INBOX");

            inbox.open(Folder.READ_ONLY);

            Message[] mensagens = inbox.getMessages();

            StringBuilder resultado = new StringBuilder();

            int inicio = Math.max(0, mensagens.length - 5);

            for (int i = inicio; i < mensagens.length; i++) {

                Message msg = mensagens[i];

                resultado.append("De: ")
                        .append(Arrays.toString(msg.getFrom()))
                        .append("\n");

                resultado.append("Assunto: ")
                        .append(msg.getSubject())
                        .append("\n\n");
            }

            inbox.close(false);
            store.close();

            return resultado.toString();

        } catch (Exception e) {
            return "Erro ao ler emails: " + e.getMessage();
        }
    }
}

