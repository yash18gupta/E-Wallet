package com.example.service;

import com.example.Utils.Constants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
public class NotificationService {
    @Autowired
    SimpleMailMessage simpleMailMessage;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    JSONParser jsonParser;

    @Autowired
    RestTemplate restTemplate;

    @KafkaListener(topics = Constants.TXN_COMPLETED_TOPIC , groupId = "test123")
    public void sendNotification(String msg) throws ParseException {

        JSONObject event = (JSONObject) jsonParser.parse(msg);

        String sender = String.valueOf(event.get("sender"));
        String receiver = String.valueOf(event.get("receiver"));
        String externalTxnId = String.valueOf(event.get("externalTxnId"));
        Double amount = (Double) event.get("amount");
        String transactionStatus = String.valueOf(event.get("txnStatus"));

        String senderMail = getUserEmail(sender);

        String receiverMail = getUserEmail(receiver);

        if(!transactionStatus.equals("FAILED")){
            String receiverMsg = "Hi! Your account has been credited with amount " + amount + " for the transaction done by " + sender;
            simpleMailMessage.setTo(receiverMail);
            simpleMailMessage.setSubject(Constants.EMAIL_SUBJECT);
            simpleMailMessage.setFrom("ewallet.jdbl.59@gmail.com");
            simpleMailMessage.setText(receiverMsg);
            javaMailSender.send(simpleMailMessage);
        }

        String senderMsg = "Hi! Your transaction " + externalTxnId + " of amount " + amount + " has been " + transactionStatus;
        simpleMailMessage.setTo(senderMail);
        simpleMailMessage.setSubject(Constants.EMAIL_SUBJECT);
        simpleMailMessage.setFrom("ewallet.jdbl.59@gmail.com");
        simpleMailMessage.setText(senderMsg);
        javaMailSender.send(simpleMailMessage);
    }


    private String getUserEmail(String username){
        String url = "http://localhost:8000/user/username/" + username;

        // Creating authorization header for txn service
        String plainCreds = "notifService:notif123";
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        HttpEntity<String> request = new HttpEntity<>(headers);

        JSONObject userData = restTemplate.exchange(url, HttpMethod.GET, request, JSONObject.class).getBody();


        return (String)userData.get("email");

    }

}
