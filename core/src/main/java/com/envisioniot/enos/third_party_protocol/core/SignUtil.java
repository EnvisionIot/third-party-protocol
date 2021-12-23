package com.envisioniot.enos.third_party_protocol.core;

import com.envisioniot.enos.third_party_protocol.core.element.GenericDeviceId;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SignUtil {

    public final static String EXTERNAL_DEVICE_ID = "externalDeviceId";

    public final static String ASSET_ID = "assetId";

    public final static String PRODUCT_KEY = "productKey";
    public final static String DEVICE_KEY = "deviceKey";

    public final static String TIMESTAMP = "timestamp";

    public static String sign(String secret, Map<String, String> params, SignMethod signMethod) {
        StringBuilder stringBuilder = new StringBuilder();
        if (params != null) {
            // Sorting parameter names by dictionary
            String[] keyArray = params.keySet().toArray(new String[0]);
            Arrays.sort(keyArray);
            for (String key : keyArray) {
                stringBuilder.append(key).append(params.get(key));
            }
        }
        stringBuilder.append(secret);
        return signMethod.sign(stringBuilder.toString());
    }


    public static String sign(SignMethod signMethod, String secret, GenericDeviceId deviceId, long timestamp) {
        return sign(secret, getSignParams(deviceId, timestamp), signMethod);
    }

    public static Map<String, String> getSignParams(GenericDeviceId deviceId, long timestamp) {
        Map<String, String> params = new HashMap<>(3);

        if (StringUtils.isNotEmpty(deviceId.getExternalDeviceId())) {
            params.put(EXTERNAL_DEVICE_ID, deviceId.getExternalDeviceId());
        } else if (StringUtils.isNotEmpty(deviceId.getAssetId())) {
            params.put(ASSET_ID, deviceId.getAssetId());
        } else if (StringUtils.isNotEmpty(deviceId.getProductKey()) && StringUtils.isNotEmpty(deviceId.getDeviceKey())) {
            params.put(PRODUCT_KEY, deviceId.getProductKey());
            params.put(DEVICE_KEY, deviceId.getDeviceKey());
        } else {
            throw new IllegalArgumentException("invalid device id: " + new Gson().toJson(deviceId));
        }

        params.put(TIMESTAMP, String.valueOf(timestamp));

        return params;
    }

}
