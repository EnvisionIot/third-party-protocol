package com.envisioniot.enos.third_party_protocol.core.message.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateModelRspItemData extends BaseRspItemData {
    private String externalDeviceId;
    private String externalModelId;
}
