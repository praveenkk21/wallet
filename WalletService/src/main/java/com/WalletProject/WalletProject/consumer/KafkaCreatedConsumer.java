package com.WalletProject.WalletProject.consumer;

import com.WalletProject.CommonConstants;
import com.WalletProject.WalletProject.dto.WalletDto;
import com.WalletProject.WalletProject.model.Wallet;
import com.WalletProject.WalletProject.repository.WalletRepo;
import com.WalletProject.WalletProject.service.WalletServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.protocol.types.Field;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaCreatedConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletServiceImpl walletService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private WalletRepo walletRepo;

    @KafkaListener(topics = {CommonConstants.USER_ALTERNAIVE_TOPIC},groupId = "${wallet.group.id}")
    public void userUpdated(String message) throws JsonProcessingException {
        JSONObject jsonObject = objectMapper.readValue(message, JSONObject.class);
        Integer userId = (Integer) jsonObject.get(CommonConstants.USER_ID);
        String isNewUser = (String) jsonObject.get(CommonConstants.IS_NEW_USER);

        if (isNewUser.equals("true")) {
            WalletDto walletDto = WalletDto.builder()
                    .userId(userId)
                    .build();
            //walletService.addWallet(walletDto);
            Wallet wallet=walletService.addWallet(walletDto);
            if(wallet!=null){
                //ASAP notification wallet has been created for new user
                kafkaTemplate.send(CommonConstants.WALLET_CREATION_TOPIC,objectMapper.writeValueAsString(jsonObject));
            }
        }
    }

    @KafkaListener(topics = {CommonConstants.TXN_INITIATED_TOPIC},groupId = "${wallet.group.txn}")
    public void doTxn(String message) throws JsonProcessingException {
        JSONObject jsonObject = objectMapper.readValue(message, JSONObject.class);
        Integer senderId = (Integer) jsonObject.get(CommonConstants.senderUserId);
        Integer receiverId = (Integer) jsonObject.get(CommonConstants.receiverUserId);
        Double amount = (Double) jsonObject.get(CommonConstants.amount);
        Integer txnId = (Integer) jsonObject.get(CommonConstants.txnId);
        walletRepo.updateWallet(senderId,-amount);
        walletRepo.updateWallet(receiverId,+amount);

        JSONObject jsonObjectCompleted = new JSONObject();
        jsonObjectCompleted.put(CommonConstants.txnId,txnId);
        kafkaTemplate.send(CommonConstants.TXN_COMPLETED_TOPIC,objectMapper.writeValueAsString(jsonObjectCompleted));
    }
}
