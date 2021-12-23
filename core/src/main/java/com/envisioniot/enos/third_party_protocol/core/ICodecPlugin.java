package com.envisioniot.enos.third_party_protocol.core;

import com.envisioniot.enos.third_party_protocol.core.message.request.BaseRequest;
import com.envisioniot.enos.third_party_protocol.core.message.response.BaseRspItemData;
import com.envisioniot.enos.third_party_protocol.core.message.response.Response;
import com.google.gson.JsonObject;

import java.util.Map;

public interface ICodecPlugin <R extends BaseRequest, T extends BaseRspItemData> {
    /**
     * This is the business protocol that identifies the original data format from
     * the client side. The protocol name here MUST be able to reflect the practical
     * format of the data.
     *
     * @return business protocol
     */
    String getProtocol();

    R decode(Map<String, Object> meta, String originalReq);

    String encodeResponse(Map<String, Object> meta, String originalReq, Response<T> response);
}
