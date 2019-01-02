package com.zbw.controller;

import com.zbw.constants.QueenConstants;
import com.zbw.utils.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RabbitController {

    @Autowired
    private MessageProducer producer;

    @RequestMapping("/sendTest")
    public String getRedis(){
        producer.send(QueenConstants.EXCHANGE_ZBW_QUEEN,QueenConstants.QUEEN_ZBW_DEMO_KEY,"first");
        return "success";
    }

}


