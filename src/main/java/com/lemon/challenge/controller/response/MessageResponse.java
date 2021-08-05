package com.lemon.challenge.controller.response;

import com.lemon.challenge.model.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private String message;
    private String subtitle;

    public MessageResponse(Message message) {
        this.message = message.getMessage();
        this.subtitle = message.getSubtitle();
    }
}
