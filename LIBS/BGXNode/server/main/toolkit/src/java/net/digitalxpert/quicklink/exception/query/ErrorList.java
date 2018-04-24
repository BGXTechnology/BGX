package net.bgx.bgxnetwork.exception.query;
public final class ErrorList{
    public static final String MODULE_CODE = "Query";
    // 00100 - EJB
    public static final String EJB_EXCEPTION = "00101";
    public static final String EJB_ENTITY_NOT_FOUND = "00102";
    public static final String EJB_PERSIST_EXCEPTION = "00103";
    public static final String EJB_CANNOT_INSERT_DATA = "00104";
    public static final String EJB_CANNOT_UPDATE_DATA = "00105";
    public static final String EJB_CANNOT_REMOVE_DATA = "00106";
    // 00200 - Data access(SQL)
    public static final String DATA_CLOSE_OPERATION_EXCEPTION = "00201";
    public static final String DATA_CONNECTION_ERROR = "00202";
    public static final String DATA_NETWORK_ORACLE_EXCEPTION = "00203";
    public static final String DATA_DATA_SOURCE_EXCEPTION = "00204";
    public static final String DATA_CANNOT_EXECUTE_UPDATE = "00205";
    public static final String DATA_CANNOT_FIND_DATA = "00206";
    public static final String DATA_CANNOT_GET_RESULT_LIST = "00207";
    public static final String DATA_ILLEGAL_ARGUMENT_EXCEPTION = "00208";
    public static final String DATA_INVALID_QUERY_STATUS = "00209";
    // 00300 - Busines method
    public static final String BUSINES_EXCEPTION = "00301";
    public static final String BUSINES_NO_SUPPORT_OBJ_TYPE = "00302";
    public static final String BUSINES_PARAMETERS_NOT_SET = "00303";
    public static final String BUSINES_CONVERSION_TYPE_EXCEPTION = "00304";
    public static final String BUSINES_RESULT_NODES_EXCEPTION = "00305";
    public static final String BUSINES_CANNOT_INSTANTIATE_EXECUTOR = "00306";
    public static final String BUSINES_CANNOT_CREATE_NODE = "00307";
    public static final String BUSINES_QUERY_ALREADY_CREATED = "00308";
    public static final String BUSINES_UNKNOW_QUERY_TYPE = "00309";
    public static final String BUSINES_ROLE_PERMISSION_EXCEPTION = "00310";
    public static final String BUSINES_CANNOT_CREATE_QUERY = "00311";
    public static final String BUSINES_CANNOT_ACCESS_SERVICE = "00312";
    public static final String BUSINES_GRAPH_BUILD_EXCEPTION = "00313";
    public static final String BUSINES_NETWORK_DATA_EXCEPTION = "00314";
    public static final String BUSINES_TYPE_MISMATCH = "00315";
    public static final String BUSINES_INSTATIATE_DIALOG_EXCEPTION = "00316";
    public static final String BUSINES_REQUEST_IS_NULL = "00317";
}
