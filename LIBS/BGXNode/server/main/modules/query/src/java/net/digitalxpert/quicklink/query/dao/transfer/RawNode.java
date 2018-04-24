package net.bgx.bgxnetwork.query.dao.transfer;

import net.bgx.bgxnetwork.transfer.query.ObjectType;

/**
 * User: A.Borisenko
 * Date: 02.11.2006
 * Time: 9:40:18
 * To change this template use File | Settings | File Templates.
 */
public class RawNode {
    private RawNode _relatedNode;
    private int _srcID;
    private Long _objID;
    private String _inn;
    private String _kpp;
    private String _name;
    private String _sum;
    private String _descr;
    private String _adr;
    private ObjectType _objectType;

    public RawNode getRelatedNode() {
        return _relatedNode;
    }

    public void setRelatedNode(RawNode _relatedNode) {
        this._relatedNode = _relatedNode;
    }

    public int getSrcID() {
        return _srcID;
    }

    public void setSrcID(int _srcID) {
        this._srcID = _srcID;
    }

    public Long getObjID() {
        return _objID;
    }

    public void setObjID(Long _objID) {
        this._objID = _objID;
    }

    public String getInn() {
        return _inn;
    }

    public void setInn(String _inn) {
        this._inn = _inn;
    }

    public String getKpp() {
        return _kpp;
    }

    public void setKpp(String _kpp) {
        this._kpp = _kpp;
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public String getSum() {
        return _sum;
    }

    public void setSum(String _sum) {
        this._sum = _sum;
    }

    public String getDescr() {
        return _descr;
    }

    public void setDescr(String _descr) {
        this._descr = _descr;
    }

    public ObjectType getObjectType() {
        return _objectType;
    }

    public void setObjectType(ObjectType _objectType) {
        this._objectType = _objectType;
    }

    public String getAdress() {
        return _adr;
    }

    public void setAdress(String adr) {
        this._adr = adr;
    }
}
