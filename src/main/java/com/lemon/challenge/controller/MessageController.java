package com.lemon.challenge.controller;


import com.lemon.challenge.controller.response.MessageResponse;
import com.lemon.challenge.model.Message;
import com.lemon.challenge.service.FoaasService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController

public class MessageController {

    @Autowired
    private FoaasService foaasService;

    @GetMapping(value = "/message")
    public MessageResponse message() {
       
        Message message =foaasService.getIdeaMessage();
        return new MessageResponse(message);

    }

}
