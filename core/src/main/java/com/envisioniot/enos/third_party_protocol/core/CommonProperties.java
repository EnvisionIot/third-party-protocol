package com.envisioniot.enos.third_party_protocol.core;

/**
 * These are the system-wide properties (namely not user defined properties in their
 * requests). The following properties would be auto populated by the backend service.
 * User can make use of them in their codec logic.
 *
 * @author jian.zhang4
 */
public class CommonProperties {
    /**
     * Organization unit (mandatory)
     */
    public final static String ORG_ID = "orgId";

    /**
     * Net component type we're currently using (mandatory)
     */
    public final static String NET_COMP_TYPE = "netCompType";

    /**
     * HTTP URI for current request (only usable for HTTP_SERVER net component)
     */
    public final static String HTTP_URI = "http-uri";

    /**
     * Protocol gateway id that's attached in the request (mandatory)
     */
    public final static String PROTOCOL_GATEWAY = "protocolGatewayId";

    /**
     * Protocol name (such as haystack) that identifies the format of device data (mandatory)
     */
    public final static String PROTOCOL_NAME = "protocolName";

    public final static String MESSAGE_TYPE = "message-type";

    /**
     * MQTT topic for current publish request (only usable for MQTT_SERVER net component)
     */
    public static final String MQTT_TOPIC = "mqtt-topic";

    public static final String MQTT_CLIENT_ID = "mqtt-client-id";

    public static final String MQTT_USERNAME = "mqtt-username";

    public static final String MQTT_PASSWORD = "mqtt-password";

    public static final String MQTT_KEEP_ALIVE = "mqtt-keep-alive";

    public static final String ACCESS_TOKEN = "access-token";

    public static final String CONNECT_TYPE = "connect-type";

    public static final String CONNECT_TYPE_INTEGRATION = "integration";

    public static final String CONNECT_TYPE_LOGIN = "login";

    public static final String TIMESTAMP = "timestamp";

}
