package com.example.service;

import com.example.Utils.Constants;
import com.example.dto.TxnCreateRequest;
import com.example.dto.TxnListResponse;
import com.example.model.Txn;
import com.example.model.TxnStatus;
import com.example.model.TxnType;
import com.example.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    JSONParser jsonParser;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;


    public String intiateTxn(TxnCreateRequest request,String sender) throws ParseException, JsonProcessingException {

        Txn txn = Txn.builder()
                .externalTxnId(UUID.randomUUID().toString())
                .sender(sender)
                .receiver(request.getReceiver())
                .purpose(request.getPurpose())
                .amount(request.getAmount())
                .txnStatus(TxnStatus.PENDING)
                .build();

        transactionRepository.save(txn);

        kafkaTemplate.send(Constants.TXN_INITIATED_TOPIC, objectMapper.writeValueAsString(txn));

        return txn.getExternalTxnId();
    }
    @KafkaListener(topics = {Constants.WALLET_UPDATE_TOPIC},groupId = "test123")
    public void updateTxn(String msg) throws ParseException, JsonProcessingException {

        JSONObject event = (JSONObject) jsonParser.parse(msg);


        String sender = String.valueOf(event.get("sender"));
        String receiver = String.valueOf(event.get("receiver"));
        String walletUpdateStatus = String.valueOf(event.get("walletUpdateStatus"));
        String externalTxnId = String.valueOf(event.get("externalTxnId"));
        Double amount = (Double) event.get("amount");

        TxnStatus transactionStatus = walletUpdateStatus.equals("FAILED") ? TxnStatus.FAILED : TxnStatus.SUCCESS;
        this.transactionRepository.updateTxnStatus(externalTxnId, transactionStatus);

        Txn transaction =  this.transactionRepository.findByExternalTxnId(externalTxnId);

        kafkaTemplate.send(Constants.TXN_COMPLETED_TOPIC, objectMapper.writeValueAsString(transaction));

    }

    public List<TxnListResponse> getAllTxn(String username) {

        List<Txn> txnList = transactionRepository.findBySenderOrReceiverOrderByCreatedTimeDesc(username);

        List<TxnListResponse> response = txnList.stream().map((txn)->TxnListResponse.builder()
                        .externalTxnId(txn.getExternalTxnId())
                        .receiver(txn.getReceiver())
                        .amount(txn.getAmount())
                        .purpose(txn.getPurpose())
                        .txnStatus(txn.getTxnStatus())
                        .txnType(txn.getReceiver().equals(username) ? TxnType.CREDITED:TxnType.DEBITED)
                        .txnTime(txn.getCreatedTime().toString())
                .build())
                .collect(Collectors.toList());

        return response;
    }
}
