package com.envisioniot.enos.third_party_protocol.core.message.request;

import com.envisioniot.enos.third_party_protocol.core.message.MessageType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

@Getter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "messageType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateDeviceRequest.class, name = "CreateDevice"),
        @JsonSubTypes.Type(value = LoginDeviceRequest.class, name = "LoginDevice"),
        @JsonSubTypes.Type(value = PostMeasurePointRequest.class, name = "PostMeasurePoint"),
        @JsonSubTypes.Type(value = UpdateModelRequest.class, name = "UpdateModel"),
})
public abstract class BaseRequest {
    private MessageType messageType;

    /**
     * Mark this only accessible to internal package
     * @param messageType
     */
    void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
