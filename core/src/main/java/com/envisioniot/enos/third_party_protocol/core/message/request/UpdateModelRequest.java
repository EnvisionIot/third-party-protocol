package com.envisioniot.enos.third_party_protocol.core.message.request;

import com.envisioniot.enos.third_party_protocol.core.element.UpdateModelInfo;
import com.envisioniot.enos.third_party_protocol.core.message.MessageType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateModelRequest extends BaseRequest {
    private boolean partialUpdate;
    private List<UpdateModelInfo> models;

    public UpdateModelRequest() {
        setMessageType(MessageType.UpdateModel);
    }
}
