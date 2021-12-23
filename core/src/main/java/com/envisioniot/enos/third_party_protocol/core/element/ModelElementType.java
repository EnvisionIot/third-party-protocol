package com.envisioniot.enos.third_party_protocol.core.element;

public class ModelElementType {
    /**
     * Sub-type is required, such as "ARRAY:INT". For ARRAY type,
     * only INT, FLOAT, DOUBLE and TEXT sub-types are supported.
     */
    public final static String ARRAY = "ARRAY";

    public final static String BOOL = "BOOL";

    public final static String DATE = "DATE";

    /**
     * Note that for ENUM type, we only accept STRING for its items.
     * And if you have enumerable values, you can specify the type
     * like "ENUM:red,black,white,green". So the enumerable values
     * should be separated by comma.
     */
    public final static String ENUM = "ENUM";

    public final static String INT = "INT";

    public final static String FLOAT = "FLOAT";

    public final static String DOUBLE = "DOUBLE";

    /**
     * For STRUCT type, you should use a map to define its fields.
     * And we don't accept STRUCT field for a STRUCT type.
     */
    public final static String STRUCT = "STRUCT";

    public final static String TEXT = "TEXT";

    public final static String TIMESTAMP = "TIMESTAMP";

    public final static String FILE = "FILE";
}
