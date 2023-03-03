package com.example.newmedicalservice.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.FileNotFoundException;



@Log4j2
@Service
public class DefaultEmailService implements EmailService {

    @Value("${mail.from}")
    private String from;

    @Value("${mail.subject}")
    private String subject;

    @Value("${mail.enabled}")
    private boolean enabled;

    @Value("${mail.preLink}")
    private String preLink;

    private JavaMailSender emailSender;


    @Autowired
    public void setMailSender(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendSimpleEmail(String toAddress, String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        emailSender.send(simpleMailMessage);
    }

    @Override
    public void sendEmailWithAttachment(String toAddress, String subject, String message, String attachment) throws MessagingException, FileNotFoundException {

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setTo(toAddress);
        messageHelper.setSubject(subject);
        messageHelper.setText(message);
        FileSystemResource file = new FileSystemResource(ResourceUtils.getFile(attachment));
        messageHelper.addAttachment("attachmentFilename", file);
        emailSender.send(mimeMessage);
    }


    public void sendEmail(String to, String clientID) throws MessagingException, FileNotFoundException {
        if (!enabled) {
            log.debug("Email notifications are disabled");
        } else {
            try {
                MimeMessage msg = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
                helper.setFrom(from);
                helper.setTo(to);
                helper.setSubject(subject);
                String link = preLink + clientID;
                String message = "click on the following link to fill in the documents ==> " + link;
                helper.setText(message, true);
                emailSender.send(msg);
            } catch (Throwable e) {
                e.printStackTrace();
                log.error("Error when sending an email: " + e);
            }
        }
    }





}
