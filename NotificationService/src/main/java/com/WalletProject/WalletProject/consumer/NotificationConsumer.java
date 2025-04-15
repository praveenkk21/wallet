package com.WalletProject.WalletProject.consumer;
import com.WalletProject.CommonConstants;
import com.WalletProject.CommonConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    @Autowired
    private SimpleMailMessage simpleMailMessage;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JavaMailSender javaMailSender;

    @KafkaListener(topics = {CommonConstants.WALLET_CREATION_TOPIC},groupId = "${notification.group.id}")
    public void walletCreatedNotification(String message) throws JsonProcessingException {
        JSONObject jsonObject=objectMapper.readValue(message,JSONObject.class);

        String email= (String) jsonObject.get(CommonConstants.EMAIL);
        String name= (String) jsonObject.get(CommonConstants.NAME);

        simpleMailMessage.setText("Wallet has been created for "+name);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setFrom("admin@wallet.com");
        simpleMailMessage.setSubject("Wallet creation");
        javaMailSender.send(simpleMailMessage);
    }
}
