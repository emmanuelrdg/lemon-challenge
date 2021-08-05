package com.lemon.challenge.service;

import com.lemon.challenge.external.FoaasMessageResponse;
import com.lemon.challenge.external.FoassRestClient;
import com.lemon.challenge.model.Message;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FoaasServiceImp implements FoaasService{

    @Autowired
    FoassRestClient foassRestClient;

    public Message getIdeaMessage() {
        FoaasMessageResponse foassMessageResponse = foassRestClient.getIdeaMessage();
        ModelMapper modelMapper = new ModelMapper();
        Message message = modelMapper.map(foassMessageResponse, Message.class);

        return message;

    }
}
