package com.envisioniot.enos.third_party_protocol.haystack;

import com.envisioniot.enos.third_party_protocol.core.ICodecPlugin;
import com.envisioniot.enos.third_party_protocol.core.element.ModelElementType;
import com.envisioniot.enos.third_party_protocol.core.element.PostedMeasurePoints;
import com.envisioniot.enos.third_party_protocol.core.message.request.PostMeasurePointRequest;
import com.envisioniot.enos.third_party_protocol.core.message.response.PostMeasurePointRspItemData;
import com.envisioniot.enos.third_party_protocol.core.message.response.Response;
import com.envisioniot.enos.third_party_protocol.haystack.data.MeasurePoint;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.envisioniot.enos.third_party_protocol.haystack.CommonFields.CODE;
import static com.envisioniot.enos.third_party_protocol.haystack.CommonFields.MSG;
import static com.envisioniot.enos.third_party_protocol.haystack.CommonUtils.*;

@Slf4j
public class PostMeasurePointPlugin implements ICodecPlugin<PostMeasurePointRequest, PostMeasurePointRspItemData> {
    private final static Pattern NUMBER = Pattern.compile("(\\d+([.]\\d+)?)");

    @Override
    public String getProtocol() {
        return CODEC_PROTOCOL;
    }

    @Override
    public PostMeasurePointRequest decode(Map<String, Object> meta, String originalReq) {
        val input = GSON.fromJson(originalReq, PostMeasurePointInput.class);
        if (CollectionUtils.isEmpty(input.points)) {
            return null;
        }

        final long now = System.currentTimeMillis();

        Map<String, Map<Long, Map<String, Object>>> collector = new HashMap<>(16);
        MutableInt count = new MutableInt();

        input.points.forEach(point -> {
            String externalDevId = getExternalDeviceId(point.getSiteRef(), point.getEquipRef());
            long time = point.getTime() > 0 ? point.getTime() : now;

            /**
             * If the currVal is not provided, here we just ignore it.
             */
            if (point.getCurVal() != null) {
                Object normalizedVal = normalizeVal(point.getKind(), point.getCurVal());
                collector
                        // Here we use TreeMap to make sure that measure points are order'ed
                        .computeIfAbsent(externalDevId, id -> new TreeMap<>())
                        .computeIfAbsent(time, t -> {
                            count.increment();
                            return new HashMap<>(8);
                        })
                        .put(point.getNavName(), normalizedVal);
            }
        });

        if (count.intValue() == 0) {
            return null;
        }

        List<PostedMeasurePoints> postedMps = new ArrayList<>(count.intValue());
        collector.forEach((externalDevId, measurepointsByTs) -> {
            measurepointsByTs.forEach((time, measurepoints) -> {
                val postedMp = new PostedMeasurePoints();
                postedMp.setExternalDeviceId(externalDevId);
                postedMp.setMeasurepoints(measurepoints);
                postedMp.setTime(time);
                postedMps.add(postedMp);
            });
        });

        PostMeasurePointRequest request = new PostMeasurePointRequest();
        request.setIgnoreInvalidMeasurePoint(input.isIgnoreInvalidMeasurePoint());
        request.setRealtime(true);
        request.setMeasurepoints(postedMps);

        return request;
    }

    private Object normalizeVal(String kind, Object value) {
        String type = CommonUtils.translateHaystackType(kind);
        if (ModelElementType.BOOL.equals(type)) {
            if (value instanceof Boolean) {
                return value;
            }
            return "T".equalsIgnoreCase(value.toString());
        } else if (ModelElementType.FLOAT.equals(type)) {
            if (value instanceof Number) {
                return value;
            }
            Matcher m = NUMBER.matcher(value.toString());
            if (m.find()) {
                return Float.parseFloat(m.group());
            }

            throw new IllegalArgumentException(String.format("unable to normalize value [%s] of kind [%s]", value, kind));
        }
        return value;
    }

    @Override
    public String encodeResponse(Map<String, Object> meta, String originalReq, Response<PostMeasurePointRspItemData> rsp) {
        JsonObject result = new JsonObject();
        if (rsp.getMainCode() != Response.SUCCESS) {
            result.addProperty(CODE, rsp.getMainCode());
            if (CollectionUtils.isNotEmpty(rsp.getItems())) {
                StringBuilder msgBuilder = new StringBuilder();
                rsp.getItems().forEach(item -> {
                    if (item.getCode() != Response.SUCCESS && StringUtils.isNotEmpty(item.getMessage())) {
                        msgBuilder.append(item.getMessage()).append(";");
                    }
                });
                result.addProperty(MSG, msgBuilder.length() != 0 ? msgBuilder.toString() : rsp.getMainMessage());
            } else {
                result.addProperty(MSG, rsp.getMainMessage());
            }
        } else {
            result.addProperty(CODE, 0);
            result.addProperty(MSG, "OK");
        }
        return GSON.toJson(result);
    }

    @Getter
    @Setter
    private static class PostMeasurePointInput {
        private boolean ignoreInvalidMeasurePoint;
        private List<MeasurePoint> points;
    }
}
