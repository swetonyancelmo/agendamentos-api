package com.swetonyancelmo.agendamentos.services;

import com.swetonyancelmo.agendamentos.models.Appointment;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String fromAddress
    ) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    /**
     * Envia e-mail de confirmação de agendamento ao cliente.
     * Falhas no envio são logadas mas não propagadas — a confirmação do agendamento
     * não deve ser revertida por indisponibilidade do serviço de e-mail.
     */
    public void sendConfirmationEmail(Appointment appointment) {
        String to = appointment.getCustomer().getEmail();
        String subject = "Agendamento confirmado - " + appointment.getService().getServiceName();
        String body = buildEmailBody(appointment);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
            log.info("E-mail de confirmação enviado para {} (agendamento {})",
                    to, appointment.getId());
        } catch (MessagingException e) {
            log.warn("Falha ao enviar e-mail de confirmação para o agendamento {} (cliente: {}): {}",
                    appointment.getId(), to, e.getMessage());
        }
    }

    private String buildEmailBody(Appointment appointment) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                        .container { max-width: 600px; margin: 40px auto; background: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
                        .header { background-color: #4CAF50; padding: 24px 32px; text-align: center; }
                        .header h1 { color: #ffffff; margin: 0; font-size: 22px; }
                        .body { padding: 32px; }
                        .body p { color: #444444; font-size: 15px; line-height: 1.6; }
                        .details { background-color: #f9f9f9; border-left: 4px solid #4CAF50; padding: 16px 20px; border-radius: 4px; margin: 20px 0; }
                        .details p { margin: 6px 0; font-size: 15px; color: #333333; }
                        .footer { text-align: center; padding: 16px; font-size: 12px; color: #999999; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>✅ Agendamento Confirmado!</h1>
                        </div>
                        <div class="body">
                            <p>Olá, <strong>%s</strong>!</p>
                            <p>Seu agendamento foi <strong>confirmado</strong> com sucesso. Veja os detalhes abaixo:</p>
                            <div class="details">
                                <p>📋 <strong>Serviço:</strong> %s</p>
                                <p>🏢 <strong>Estabelecimento:</strong> %s</p>
                                <p>📅 <strong>Data:</strong> %s</p>
                                <p>⏰ <strong>Horário:</strong> %s às %s</p>
                            </div>
                            <p>Até logo!</p>
                        </div>
                        <div class="footer">
                            <p>Este é um e-mail automático. Por favor, não responda.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                appointment.getCustomer().getName(),
                appointment.getService().getServiceName(),
                appointment.getBusiness().getName(),
                appointment.getAppointmentDate().format(DATE_FMT),
                appointment.getStartTime().format(TIME_FMT),
                appointment.getEndTime().format(TIME_FMT)
        );
    }
}
