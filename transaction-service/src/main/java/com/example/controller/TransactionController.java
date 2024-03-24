package com.example.controller;

import com.example.dto.TxnCreateRequest;
import com.example.dto.TxnListResponse;
import com.example.model.Txn;
import com.example.model.User;
import com.example.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
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

}
