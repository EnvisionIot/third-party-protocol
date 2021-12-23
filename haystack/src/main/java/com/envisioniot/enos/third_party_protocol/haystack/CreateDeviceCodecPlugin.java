package com.envisioniot.enos.third_party_protocol.haystack;

import com.envisioniot.enos.third_party_protocol.core.CommonProperties;
import com.envisioniot.enos.third_party_protocol.core.ICodecPlugin;
import com.envisioniot.enos.third_party_protocol.core.message.request.CreateDeviceRequest;
import com.envisioniot.enos.third_party_protocol.core.message.response.CreateDeviceRspItemData;
import com.envisioniot.enos.third_party_protocol.core.message.response.Response;
import com.envisioniot.enos.third_party_protocol.core.message.response.Response.Item;
import com.envisioniot.enos.third_party_protocol.haystack.data.EquipInfo;
import com.envisioniot.enos.third_party_protocol.haystack.data.PointInfo;
import com.envisioniot.enos.third_party_protocol.haystack.data.SiteInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.envisioniot.enos.third_party_protocol.haystack.CommonFields.*;
import static com.envisioniot.enos.third_party_protocol.haystack.CommonUtils.*;

@Slf4j
public class CreateDeviceCodecPlugin implements ICodecPlugin<CreateDeviceRequest, CreateDeviceRspItemData> {
    private final static String CREATE_DEVICE_INPUT = "create_device_input";

    @Override
    public String getProtocol() {
        return CODEC_PROTOCOL;
    }

    @Override
    public CreateDeviceRequest decode(Map<String, Object> meta, String originalReq) {
        String ou = (String) meta.get(CommonProperties.ORG_ID);
        CreateDeviceInput input = GSON.fromJson(originalReq, CreateDeviceInput.class);

        val deviceInfos = CommonUtils.decodeEquips(ou, input.getEquips(), input.getSites());
        val modelInfos = CommonUtils.decodePoints(ou, input.getPoints());

        // We need this for encoding the response later
        meta.put(CREATE_DEVICE_INPUT, input);

        CreateDeviceRequest req = new CreateDeviceRequest();
        req.setDevices(deviceInfos);
        req.setModels(modelInfos);
        return req;
    }

    @Override
    public String encodeResponse(Map<String, Object> meta, String originalReq, Response<CreateDeviceRspItemData> response) {
        JsonObject result = new JsonObject();
        if (response.getMainCode() != Response.SUCCESS) {
            result.addProperty(CODE, response.getMainCode());
            result.addProperty(MSG, response.getMainMessage());
        } else {
            result.addProperty(CODE, 0);
            result.addProperty(MSG, "OK");
        }

        CreateDeviceInput input = (CreateDeviceInput) meta.remove(CREATE_DEVICE_INPUT);

        JsonArray array = new JsonArray();
        MutableInt totalSize = new MutableInt(), successSize = new MutableInt();
        if (CollectionUtils.isNotEmpty(response.getItems())) {
            Map<String, Item<CreateDeviceRspItemData>> items = new HashMap<>(response.getItems().size());
            response.getItems().forEach(item -> items.put(item.getData().getExternalDeviceId(), item));

            input.getEquips().forEach(equip -> {
                val rspItem = items.get(getExternalDeviceId(equip.getSiteRef(), equip.getId()));

                JsonObject item = new JsonObject();

                JsonObject itemData = new JsonObject();
                itemData.addProperty(ID, equip.getId());
                itemData.addProperty(SITE_REF, equip.getSiteRef());
                itemData.addProperty(MODEL_TYPE, equip.getModelType());

                item.add(DATA, itemData);
                if (rspItem != null) {
                    item.addProperty(CODE, rspItem.getCode());
                    item.addProperty(MSG, rspItem.getMessage());

                    CreateDeviceRspItemData createResult = rspItem.getData();
                    itemData.addProperty(ASSET_ID, createResult.getAssetId());
                    itemData.addProperty(PRODUCT_KEY, createResult.getProductKey());
                    itemData.addProperty(DEVICE_KEY, createResult.getDeviceKey());
                    itemData.addProperty(DEVICE_SECRET, createResult.getDeviceSecret());

                    if (rspItem.getCode() == Response.SUCCESS) {
                        successSize.increment();
                    }
                } else {
                    item.addProperty(CODE, Response.INTERNAL_ERROR);
                    item.addProperty(MSG, "[BUG] result not returned by service");
                }

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
    private static class CreateDeviceInput {
        private List<EquipInfo> equips;
        private List<PointInfo> points;
        private List<SiteInfo> sites;
    }
}
