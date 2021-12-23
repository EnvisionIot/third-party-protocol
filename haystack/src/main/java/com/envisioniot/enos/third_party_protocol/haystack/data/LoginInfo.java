package com.envisioniot.enos.third_party_protocol.haystack.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginInfo {
    private String equipRef;
    private String siteRef;
    private String deviceSecret;
}
