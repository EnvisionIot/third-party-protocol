package com.envisioniot.enos.third_party_protocol.core.message.response;

import lombok.*;

import java.util.Collections;
import java.util.List;

@Getter @Setter
@SuppressWarnings("unchecked")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Response<T extends BaseRspItemData> {
    public final static int SUCCESS = 200;

    /**
     * This code indicates that the server has handled the request. However,
     * there are failures for at least one item in the request (multiple
     * request items can be defined in the request). And the error details
     * can be checked in the corresponding items in response.
     */
    public final static int HAS_ERROR = 206;

    public final static int INVALID_ARG = 400;
    public final static int RESOURCE_NOT_FOUND = 404;
    public final static int RESOURCE_ALREADY_EXIST = 409;

    public final static int INTERNAL_ERROR = 500;

    private int mainCode;
    private String mainMessage;

    /**
     * The response items would not be empty only when {@link Response#mainCode}
     * is {@link Response#SUCCESS} or {@link Response#HAS_ERROR}. For other values,
     * it would be empty as the request is not accepted.
     */
    private List<Item<T>> items;

    public static <T extends BaseRspItemData> Response<T> success() {
       return wrap(Collections.emptyList());
    }

    public static <T extends BaseRspItemData> Response<T> wrap(List<Item<T>> items) {
        if (items == null) {
            items = Collections.emptyList();
        }

        Response response = new Response();
        long failed = items.stream().filter(item -> item.getCode() != SUCCESS).count();
        response.setMainCode(failed != 0 ? HAS_ERROR : SUCCESS);
        if (failed != 0) {
            response.setMainMessage(failed + " items failed");
        } else {
            response.setMainMessage("success");
        }
        response.setItems(items);
        return response;
    }

    public static <T extends BaseRspItemData> Response<T> wrap(int code, String errorMsg) {
        Response response = new Response();
        response.setMainCode(code);
        response.setMainMessage("request failed with error: " + errorMsg);
        response.setItems(Collections.emptyList());
        return response;
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class Item<T> {
        private int code;
        private String message;
        private T data;
    }
}
