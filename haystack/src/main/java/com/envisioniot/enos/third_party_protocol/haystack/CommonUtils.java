package com.envisioniot.enos.third_party_protocol.haystack;

import com.envisioniot.enos.third_party_protocol.core.element.CreateDeviceInfo;
import com.envisioniot.enos.third_party_protocol.core.element.ModelElementType;
import com.envisioniot.enos.third_party_protocol.core.element.UpdateModelInfo;
import com.envisioniot.enos.third_party_protocol.haystack.data.EquipInfo;
import com.envisioniot.enos.third_party_protocol.haystack.data.PointInfo;
import com.envisioniot.enos.third_party_protocol.haystack.data.SiteInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class CommonUtils {
    public static final String CODEC_PROTOCOL = "haystack";

    private static final String FORMATTER = "%s-%s";
    private static final String DEFAULT_TIMEZONE = "Asia/Shanghai";

    private static final String HAYSTACK_BIZ_ID = "HAYSTACK_BIZ_ID";
    private static final String HAYSTACK_SITE_REF = "HAYSTACK_SITE_REF";

    public static final Gson GSON = new Gson();

    public static List<CreateDeviceInfo> decodeEquips(String orgId, Collection<EquipInfo> equips, Collection<SiteInfo> sites) {
        if (CollectionUtils.isEmpty(equips)) {
            return Collections.emptyList();
        }

        Map<String, String> timezoneMap = sites != null
                ? sites.stream().collect(Collectors.toMap(SiteInfo::getId, SiteInfo::getTz))
                : Collections.emptyMap();

        return equips.stream()
                .map(equip -> convert(orgId, equip, timezoneMap.getOrDefault(equip.getSiteRef(), DEFAULT_TIMEZONE)))
                .collect(Collectors.toList());
    }

    private static CreateDeviceInfo convert(String orgId, EquipInfo equip, String timezone) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(equip.getId()), "equip id not defined in equip %s", equip);
        Preconditions.checkArgument(StringUtils.isNotEmpty(equip.getModelType()), "model type not defined in equip %s", equip);

        CreateDeviceInfo deviceInfo = new CreateDeviceInfo();
        deviceInfo.setTimezone(timezone);
        deviceInfo.setExternalDeviceId(getExternalDeviceId(equip.getSiteRef(), equip.getId()));
        deviceInfo.setExternalModelId(getExternalModelId(orgId, equip.getModelType()));
        deviceInfo.setTags(getTags(equip));
        return deviceInfo;
    }

    public static List<UpdateModelInfo> decodePoints(String orgId, Collection<PointInfo> points) {
        if (CollectionUtils.isEmpty(points)) {
            return Collections.emptyList();
        }

        Map<String, UpdateModelInfo> modelInfos = new HashMap<>(12);
        points.forEach(point -> {
            Preconditions.checkArgument(StringUtils.isNotEmpty(point.getEquipRef()), "equipRef not defined in point %s", point);
            Preconditions.checkArgument(StringUtils.isNotEmpty(point.getNavName()), "navName not defined in point %s", point);
            Preconditions.checkArgument(StringUtils.isNotEmpty(point.getKind()), "kind not defined in point %s", point);

            String externalDeviceId = getExternalDeviceId(point.getSiteRef(), point.getEquipRef());

            UpdateModelInfo modelDesc = modelInfos.computeIfAbsent(externalDeviceId,
                    id -> {
                        UpdateModelInfo modelInfo = new UpdateModelInfo();
                        modelInfo.setExternalDeviceId(externalDeviceId);
                        modelInfo.setMeasurepoints(new HashMap<>(32));
                        return modelInfo;
                    }
            );

            String mpType = translateHaystackType(point.getKind());
            val existingMpType = modelDesc.getMeasurepoints().put(point.getNavName(), mpType);
            if (existingMpType != null) {
                Preconditions.checkArgument(Objects.equals(mpType, existingMpType),
                        "found multiple different types defined for sensor: %s of equip: %s",
                        point.getNavName(), point.getEquipRef());
            }
        });

        return new ArrayList<>(modelInfos.values());
    }

    private static Map<String, String> getTags(EquipInfo equip) {
        Map<String, String> tags = Maps.newHashMap();
        if (StringUtils.isNotEmpty(equip.getId())) {
            tags.put(HAYSTACK_BIZ_ID, equip.getId());
        }
        if (StringUtils.isNotEmpty(equip.getSiteRef())) {
            tags.put(HAYSTACK_SITE_REF, equip.getSiteRef());
        }

        return tags;
    }

    public static String translateHaystackType(String type) {
        if ("Str".equals(type)) {
            return ModelElementType.TEXT;
        } else if ("Number".equals(type)) {
            return ModelElementType.FLOAT;
        } else if ("Bool".equals(type)) {
            return ModelElementType.BOOL;
        }
        throw new IllegalArgumentException(String.format("type: [%s] is not support", type));
    }

    public static String getExternalDeviceId(String site, String equipId) {
        if (StringUtils.isNotEmpty(site)) {
            return String.format(FORMATTER, site, equipId);
        }
        return equipId;
    }

    public static String getExternalModelId(String orgId, String modelType) {
        return String.format(FORMATTER, orgId, modelType);
    }

}
