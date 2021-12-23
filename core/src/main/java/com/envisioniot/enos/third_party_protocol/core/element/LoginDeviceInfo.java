package com.envisioniot.enos.third_party_protocol.core.element;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDeviceInfo extends GenericDeviceId {
    private String sign;
}
