package com.envisioniot.enos.third_party_protocol.haystack;

import com.envisioniot.enos.third_party_protocol.core.CommonProperties;
import com.envisioniot.enos.third_party_protocol.core.ICodecPlugin;
import com.envisioniot.enos.third_party_protocol.core.message.request.UpdateModelRequest;
import com.envisioniot.enos.third_party_protocol.core.message.response.Response;
import com.envisioniot.enos.third_party_protocol.core.message.response.Response.Item;
import com.envisioniot.enos.third_party_protocol.core.message.response.UpdateModelRspItemData;
import com.envisioniot.enos.third_party_protocol.haystack.data.PointInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.*;

import static com.envisioniot.enos.third_party_protocol.haystack.CommonFields.*;
import static com.envisioniot.enos.third_party_protocol.haystack.CommonUtils.*;

public class UpdateModelCodecPlugin implements ICodecPlugin<UpdateModelRequest, UpdateModelRspItemData> {
    private final static String UPDATE_MODEL_INPUT = "update_model_input";

    @Override
    public String getProtocol() {
        return CODEC_PROTOCOL;
    }

    @Override
    public UpdateModelRequest decode(Map<String, Object> meta, String originalReq) {
        String ou = (String) meta.get(CommonProperties.ORG_ID);
        UpdateModelInput input = GSON.fromJson(originalReq, UpdateModelInput.class);

        val modelInfos = CommonUtils.decodePoints(ou, input.getPoints());

        // We need this for encoding the response later
        meta.put(UPDATE_MODEL_INPUT, input);

        UpdateModelRequest req = new UpdateModelRequest();
        req.setPartialUpdate(input.isPartialUpdate);
        req.setModels(modelInfos);

        return req;
    }

    @Override
    public String encodeResponse(Map<String, Object> meta, String originalReq, Response<UpdateModelRspItemData> response) {
        JsonObject result = new JsonObject();
        if (response.getMainCode() != Response.SUCCESS) {
            result.addProperty(CODE, response.getMainCode());
            result.addProperty(MSG, response.getMainMessage());
        } else {
            result.addProperty(CODE, 0);
            result.addProperty(MSG, "OK");
        }

        val updateModelInput = (UpdateModelInput) meta.remove(UPDATE_MODEL_INPUT);

        JsonArray array = new JsonArray();
        MutableInt totalSize = new MutableInt(), successSize = new MutableInt();
        Set<String> seenDevices = new HashSet<>();
        if (CollectionUtils.isNotEmpty(response.getItems())) {
            Map<String, Item<UpdateModelRspItemData>> items = new HashMap<>(response.getItems().size());
            response.getItems().forEach(item -> items.put(item.getData().getExternalDeviceId(), item));

            updateModelInput.getPoints().forEach(pointInfo -> {
                String deviceId = getExternalDeviceId(pointInfo.getSiteRef(), pointInfo.getEquipRef());
                if (!seenDevices.add(deviceId)) {
                    // We only account for the device if it is seen firstly
                    return;
                }

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
                itemData.addProperty(EQUIP_REF, pointInfo.getEquipRef());
                itemData.addProperty(SITE_REF, pointInfo.getSiteRef());
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
    private static class UpdateModelInput {
        private boolean isPartialUpdate;
        private List<PointInfo> points;
    }
}
