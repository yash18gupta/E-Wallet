package com.example.controller;

import com.example.dto.TxnCreateRequest;
import com.example.dto.TxnListResponse;
import com.example.model.Txn;
import com.example.model.User;
import com.example.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transact")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/karo")
    public String transact(@RequestBody @Valid TxnCreateRequest request) throws JsonProcessingException, ParseException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User sender = (User) authentication.getPrincipal();
        return transactionService.intiateTxn(request, sender.getUsername());
    }

    @GetMapping("/list")
    public List<TxnListResponse> getAllTxn(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return transactionService.getAllTxn(user.getUsername());
    }

    @Value("${razorpay.keyId}")
    private String keyId;

    @Value("${razorpay.keySecret}")
    private String keySecret;

    @PostMapping("/addMoney")
    public String createOrder(@RequestParam("amount") int amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        try {
            RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100); // amount in paisa
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_receipt");
            Order order = razorpayClient.orders.create(orderRequest);

            this.transactionService.addMoney(user.getUsername(),(int)order.get("amount")/100);

            return order.toString();
        } catch (RazorpayException e) {
            return e.getMessage();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
