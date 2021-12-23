package com.envisioniot.enos.third_party_protocol.haystack;

import com.envisioniot.enos.third_party_protocol.core.CommonProperties;
import com.envisioniot.enos.third_party_protocol.core.ICodecPlugin;
import com.envisioniot.enos.third_party_protocol.core.message.request.BaseRequest;
import com.envisioniot.enos.third_party_protocol.core.message.response.BaseRspItemData;
import com.envisioniot.enos.third_party_protocol.core.message.response.Response;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Codec plugin proxy that routes request to specific implementation based on {@link CommonProperties#HTTP_URI}.
 *
 * @author jian.zhang4
 */
@SuppressWarnings("unchecked")
public class CodecPluginProxy implements ICodecPlugin<BaseRequest, BaseRspItemData> {

    private final Map<String, ICodecPlugin> routingRules;

    public CodecPluginProxy() {
        // Config routing rules here for request
        routingRules = ImmutableMap.<String, ICodecPlugin>builder()
                .put("/createDevice", new CreateDeviceCodecPlugin())
                .put("/updateModel", new UpdateModelCodecPlugin())
                .put("/postMeasurepoint", new PostMeasurePointPlugin())
                .put("/loginDevice", new LoginDevicePlugin())
                .build();
    }

    @Override
    public String getProtocol() {
        return "haystack";
    }

    @Override
    public BaseRequest decode(Map<String, Object> meta, String originalReq) {
        return getPluginImpl(meta).decode(meta, originalReq);
    }

    @Override
    public String encodeResponse(Map<String, Object> meta, String originalReq, Response<BaseRspItemData> response) {
        return getPluginImpl(meta).encodeResponse(meta, originalReq, response);
    }


    private ICodecPlugin getPluginImpl(Map<String, Object> meta) {
        String httpUri = (String) meta.get(CommonProperties.HTTP_URI);
        Preconditions.checkState(httpUri != null, "[BUG] found no config [Ts] in meta", CommonProperties.HTTP_URI);

        ICodecPlugin plugin = routingRules.get(httpUri);
        Preconditions.checkArgument(plugin != null, "found no codec implementation for uri: %s", httpUri);

        return plugin;
    }
}
