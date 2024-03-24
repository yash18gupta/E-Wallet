package com.example.service;

import com.example.model.Wallet;
import com.example.repository.WalletRepository;
import com.example.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    JSONParser jsonParser;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    private static Logger logger = LoggerFactory.getLogger(WalletService.class);

    @Value("${wallet.opening-balance}")
    private Double balance;

    @KafkaListener(topics = {Constants.USER_CREATED_TOPIC},groupId = "test123")
    public void create(String msg) throws ParseException {

        JSONObject event = (JSONObject) jsonParser.parse(msg);

        String username = String.valueOf(event.get("username"));

        if(username == null){
            logger.warn("create : unable to find username in event , data={}",event);
            return;
        }

        Wallet wallet = Wallet.builder()
                    .username(username)
                    .balance(balance)
                    .build();

            walletRepository.save(wallet);

    }

    @KafkaListener(topics = {Constants.TXN_INITIATED_TOPIC}, groupId = "test_123")
    public void updateWallet(String msg) throws ParseException, JsonProcessingException {

        JSONObject event = (JSONObject) jsonParser.parse(msg);

        String sender = String.valueOf(event.get("sender"));
        String receiver =  String.valueOf(event.get("receiver"));
        Double amount = (Double) event.get("amount");
        String externalTxnId = String.valueOf(event.get("externalTxnId"));

        Wallet senderWallet = this.walletRepository.findByUsername(sender);
        Wallet receiverWallet = this.walletRepository.findByUsername(receiver);

        JSONObject message = new JSONObject();
        message.put("receiver", receiver);
        message.put("sender", sender);
        message.put("amount", amount);
        message.put("externalTxnId", externalTxnId);


        if(senderWallet == null || receiverWallet == null || senderWallet.getBalance()<amount){
            message.put("walletUpdateStatus", "FAILED");
            kafkaTemplate.send(Constants.WALLET_UPDATE_TOPIC, objectMapper.writeValueAsString(message));
            return;
        }

        try {
            walletRepository.updateWallet(sender, -amount);
            walletRepository.updateWallet(receiver, amount);
            message.put("walletUpdateStatus", "SUCCESS");
            kafkaTemplate.send(Constants.WALLET_UPDATE_TOPIC, objectMapper.writeValueAsString(message));
        }catch (Exception e){
            message.put("walletUpdateStatus", "FAILED");
            kafkaTemplate.send(Constants.WALLET_UPDATE_TOPIC, objectMapper.writeValueAsString(message));
        }

    }
}
