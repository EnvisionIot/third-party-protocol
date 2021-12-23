package com.envisioniot.enos.third_party_protocol.core.message.request;

import com.envisioniot.enos.third_party_protocol.core.element.LoginDeviceInfo;
import com.envisioniot.enos.third_party_protocol.core.message.MessageType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class LoginDeviceRequest extends BaseRequest {
    public final static int DEFAULT_KEEP_ONLINE_SECONDS = 600;

    private List<LoginDeviceInfo> devices;
    private String signMethod;
    private long timestamp;

    /**
     * How long (seconds) we need to keep the device online if no messages come
     * from a device after login ? If not provided, the default value would be
     * {@link LoginDeviceRequest#DEFAULT_KEEP_ONLINE_SECONDS} seconds.
     */
    private int keepOnline;

    public LoginDeviceRequest() {
        setMessageType(MessageType.LoginDevice);
    }

}
