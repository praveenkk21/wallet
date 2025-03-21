package com.WalletProject.consumer;

import com.WalletProject.CommonConstants;
import com.WalletProject.model.Txn;
import com.WalletProject.model.TxnStatus;
import com.WalletProject.repository.TxnRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

public class TxnCompletedConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TxnRepository txnRepository;

    @KafkaListener(topics = {CommonConstants.TXN_COMPLETED_TOPIC}, groupId = "${txn.group.id}")
    public void userUpdated(String message) throws JsonProcessingException {
        JSONObject jsonObject = objectMapper.readValue(message, JSONObject.class);
        Integer txnId = (Integer) jsonObject.get(CommonConstants.txnId);
        //Set txn status
        Optional<Txn> optionalTxn = txnRepository.findById(txnId);
        if (optionalTxn.isPresent()) {
            Txn txn = optionalTxn.get();
            txn.setTxnStatus(TxnStatus.SUCCESS);
            txnRepository.save(txn);
        }
    }
}
