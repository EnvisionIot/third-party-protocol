package com.envisioniot.enos.third_party_protocol.haystack;

import com.envisioniot.enos.third_party_protocol.core.ICodecPlugin;
import com.envisioniot.enos.third_party_protocol.core.SignMethod;
import com.envisioniot.enos.third_party_protocol.core.SignUtil;
import com.envisioniot.enos.third_party_protocol.core.element.LoginDeviceInfo;
import com.envisioniot.enos.third_party_protocol.core.message.request.LoginDeviceRequest;
import com.envisioniot.enos.third_party_protocol.core.message.response.LoginDeviceRspItemData;
import com.envisioniot.enos.third_party_protocol.core.message.response.Response;
import com.envisioniot.enos.third_party_protocol.haystack.data.LoginInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.envisioniot.enos.third_party_protocol.haystack.CommonFields.*;
import static com.envisioniot.enos.third_party_protocol.haystack.CommonUtils.*;

public class LoginDevicePlugin implements ICodecPlugin<LoginDeviceRequest, LoginDeviceRspItemData> {
    private final static String LOGIN_DEVICE_INPUT = "login_device_input";

    private final static SignMethod SIGN_METHOD = SignMethod.SHA256;

    @Override
    public String getProtocol() {
        return CODEC_PROTOCOL;
    }

    @Override
    public LoginDeviceRequest decode(Map<String, Object> meta, String originalReq) {
        val input = GSON.fromJson(originalReq, LoginDeviceInput.class);
        if (CollectionUtils.isEmpty(input.getLoginInfos())) {
            return null;
        }

        val now = System.currentTimeMillis();
        val devices = new ArrayList<LoginDeviceInfo>();
        input.getLoginInfos().forEach(loginInfo -> {
            String deviceId = CommonUtils.getExternalDeviceId(loginInfo.getSiteRef(), loginInfo.getEquipRef());
            if (StringUtils.isEmpty(loginInfo.getDeviceSecret())) {
                throw new RuntimeException("device secret MUST be provided, invalid device: " + GSON.toJson(loginInfo));
            }

            LoginDeviceInfo deviceInfo = new LoginDeviceInfo();
            deviceInfo.setExternalDeviceId(deviceId);

            deviceInfo.setSign(SignUtil.sign(SIGN_METHOD, loginInfo.getDeviceSecret(), deviceInfo, now));

            devices.add(deviceInfo);
        });

        LoginDeviceRequest req = new LoginDeviceRequest();
        req.setKeepOnline(600);
        req.setTimestamp(now);
        req.setSignMethod(SIGN_METHOD.getName());
        req.setDevices(devices);

        meta.put(LOGIN_DEVICE_INPUT, input);

        return req;
    }

    @Override
    public String encodeResponse(Map<String, Object> meta, String originalReq, Response<LoginDeviceRspItemData> response) {
        val input = (LoginDeviceInput) meta.remove(LOGIN_DEVICE_INPUT);

        JsonObject result = new JsonObject();
        if (response.getMainCode() != Response.SUCCESS) {
            result.addProperty(CODE, response.getMainCode());
            result.addProperty(MSG, response.getMainMessage());
        } else {
            result.addProperty(CODE, 0);
            result.addProperty(MSG, "OK");
        }

        JsonArray array = new JsonArray();
        MutableInt totalSize = new MutableInt(), successSize = new MutableInt();
        if (CollectionUtils.isNotEmpty(response.getItems())) {
            Map<String, Response.Item<LoginDeviceRspItemData>> items = new HashMap<>(response.getItems().size());
            response.getItems().forEach(item -> items.put(item.getData().getExternalDeviceId(), item));

            input.getLoginInfos().forEach(loginInfo -> {
                String deviceId = getExternalDeviceId(loginInfo.getSiteRef(), loginInfo.getEquipRef());
                val rspItem = items.get(deviceId);

                JsonObject item = new JsonObject();
                if (rspItem != null) {
                    item.addProperty(CODE, rspItem.getCode());
                    item.addProperty(MSG, rspItem.getMessage());

                    if (rspItem.getCode() == Response.SUCCESS) {
                        successSize.increment();
                    }
                } else {
                    item.addProperty(CODE, Response.INTERNAL_ERROR);
                    item.addProperty(MSG, "[BUG] result not returned by service");
                }

                JsonObject itemData = new JsonObject();
                itemData.addProperty(EQUIP_REF, loginInfo.getEquipRef());
                itemData.addProperty(SITE_REF, loginInfo.getSiteRef());
                item.add(DATA, itemData);

                array.add(item);
                totalSize.increment();
            });
        }

        result.add(DATA, array);
        result.addProperty(TOTAL_SIZE, totalSize.intValue());
        result.addProperty(SUCCESS_SIZE, successSize.intValue());

        return GSON.toJson(result);
    }

    @Getter
    @Setter
    private static class LoginDeviceInput {
        private List<LoginInfo> loginInfos;
    }
}
