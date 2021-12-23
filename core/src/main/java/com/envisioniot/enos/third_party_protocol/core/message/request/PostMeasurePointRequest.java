package com.envisioniot.enos.third_party_protocol.core.message.request;

import com.envisioniot.enos.third_party_protocol.core.element.PostedMeasurePoints;
import com.envisioniot.enos.third_party_protocol.core.message.MessageType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostMeasurePointRequest extends BaseRequest {
    private boolean ignoreInvalidMeasurePoint;
    private boolean realtime;
    private List<PostedMeasurePoints> measurepoints;

    public PostMeasurePointRequest() {
        setMessageType(MessageType.PostMeasurePoint);
    }
}
