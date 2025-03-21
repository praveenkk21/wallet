package com.WalletProject.service;

import com.WalletProject.CommonConstants;
import com.WalletProject.dto.TxnRequestDto;
import com.WalletProject.exceptions.TxnExcepion;
import com.WalletProject.model.Txn;
import com.WalletProject.model.TxnStatus;
import com.WalletProject.model.Wallet;
import com.WalletProject.repository.TxnRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


@Service
public class TxnServiceImpl {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TxnRepository txnRepository;

    @Transactional
    public Txn createTxn(TxnRequestDto txnRequestDto) throws JsonProcessingException {
    //Check if sender has right balance
        Integer senderId= txnRequestDto.getSenderId();
        Integer receiverId= txnRequestDto.getReceiverId();
        Double amount=txnRequestDto.getAmount();

        //sync call
//        Wallet senderDetail=restTemplate.getForEntity("http://localhost:8083?userId="+senderId, Wallet.class).getBody();
//        Wallet receiverDetail=restTemplate.getForEntity("http://localhost:8083?userId="+receiverID, Wallet.class).getBody();

        Wallet senderDetail, receiverDetail;
        try {
            senderDetail = restTemplate.getForEntity("http://WalletService/wallets?userId={userId}", Wallet.class, senderId).getBody();
            receiverDetail = restTemplate.getForEntity("http://WalletService/wallets?userId={userId}", Wallet.class, receiverId).getBody();
        } catch (Exception e) {
            throw new TxnExcepion("Wallet service unavailable: " + e.getMessage());
        }

        if(senderDetail ==null){
            throw new TxnExcepion(senderId+" is incorrect ID of the sender");
        }
        if(receiverDetail ==null){
            throw new TxnExcepion(receiverId+" is incorrect ID of the receiver");
        }

        if(senderDetail.getBalance()<amount){
            throw new TxnExcepion(senderId+" is not having enough balance");
        }

        JSONObject jsonObject=new JSONObject();
        jsonObject.put(CommonConstants.senderUserId,senderDetail.getUserId());
        jsonObject.put(CommonConstants.receiverUserId,receiverDetail.getUserId());
        jsonObject.put(CommonConstants.amount,amount);

        Txn txn= Txn.builder()
                .amount(amount)
                .txnStatus(TxnStatus.INITIATED)
                .senderId(senderDetail.getUserId())
                .receiverId(receiverDetail.getUserId())
                .build();

        txn = txnRepository.save(txn);
        jsonObject.put(CommonConstants.txnId,txn.getId());

        kafkaTemplate.send(CommonConstants.TXN_INITIATED_TOPIC,objectMapper.writeValueAsString(jsonObject));
        return txn;
    }
}
