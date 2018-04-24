package net.bgx.bgxnetwork.dao;

import java.util.Map;

public class Query {
    private String _statement;
    private Map _parameters;

    Query() {
    }

    void setStatement(String aStatement) {
        _statement = aStatement;
    }

    void setParameters(Map aParameters) {
        _parameters = aParameters;
    }

    public String getStatement() {
        return _statement;
    }

    public int getParameterIndex(String aName) {
        return (Integer) _parameters.get(aName);
    }
}
