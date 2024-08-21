package com.example.demo.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private  JavaMailSender javaMailSender;

    public EmailService (JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    public void sendVerificationEmail(String to, String subject, String body){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        this.javaMailSender.send(simpleMailMessage);
    }
}
