/* $Id: //depot/MNS/bgxnetwork/server/main/toolkit/src/java/net/bgx/bgxnetwork/toolkit/ErrorList.java#1 $
 * $DateTime: 2006/03/07 13:52:51 $
 * $Change: 6772 $
 * $Author: grouzintsev $
 */
package net.bgx.bgxnetwork.toolkit;

/**
 * Список исключений модуля.
 */
public final class ErrorList {
    public static final String MODULE_CODE = "TOOLKIT";
    public static final String LOCATOR_CREATION_FAILED = "50001";
    public static final String LOOKUP_FAILED = "50002";
    public static final String SERVICE_CREATION_FAILED = "50003";
    public static final String SERIALIZED_HANDLE_IS_NULL = "50004";
    public static final String SERVICE_RESTORATION_FAILED = "50005";
    public static final String HANDLE_SERIALIZATION_FAILED = "50006";
    public static final String JNDI_NAME_IS_NOT_PROVIDED = "50007";
    public static final String CACHED_OBJECT_IS_NULL = "50008";
    public static final String GOT_NULL_BY_LOOKUP = "50009";

    public static final String BOUNDS_LOAD_CONFIG_FAILED = "51001";
    public static final String BOUNDS_NOT_SATISFIED = "51002";

    public static final String DICTIONARY_IS_NOT_READY = "52001";

    public static final String CONNECTION_POOL_ERROR = "53001";
}
