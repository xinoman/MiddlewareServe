package com.its.core.local.hezhou.tmri.tfc.webservice;

public class TransProxy implements com.its.core.local.hezhou.tmri.tfc.webservice.Trans {
  private String _endpoint = null;
  private com.its.core.local.hezhou.tmri.tfc.webservice.Trans trans = null;
  
  public TransProxy() {
    _initTransProxy();
  }
  
  public TransProxy(String endpoint) {
    _endpoint = endpoint;
    _initTransProxy();
  }
  
  private void _initTransProxy() {
    try {
      trans = (new com.its.core.local.hezhou.tmri.tfc.webservice.TransServiceLocator()).getTmri();
      if (trans != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)trans)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)trans)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (trans != null)
      ((javax.xml.rpc.Stub)trans)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.its.core.local.hezhou.tmri.tfc.webservice.Trans getTrans() {
    if (trans == null)
      _initTransProxy();
    return trans;
  }
  
  public long initTrans(java.lang.String kkbh, java.lang.String fxlx, long cdh, java.lang.String info) throws java.rmi.RemoteException{
    if (trans == null)
      _initTransProxy();
    return trans.initTrans(kkbh, fxlx, cdh, info);
  }
  
  public long writeVehicleInfo(java.lang.String kkbh, java.lang.String fxlx, long cdh, java.lang.String hphm, java.lang.String hpzl, java.lang.String gcsj, long clsd, long clxs, java.lang.String wfdm, long cwkc, java.lang.String hpys, java.lang.String cllx, java.lang.String fzhpzl, java.lang.String fzhphm, java.lang.String fzhpys, java.lang.String clpp, java.lang.String clwx, java.lang.String csys, java.lang.String tplj, java.lang.String tp1, java.lang.String tp2, java.lang.String tp3, java.lang.String tztp) throws java.rmi.RemoteException{
    if (trans == null)
      _initTransProxy();
    return trans.writeVehicleInfo(kkbh, fxlx, cdh, hphm, hpzl, gcsj, clsd, clxs, wfdm, cwkc, hpys, cllx, fzhpzl, fzhphm, fzhpys, clpp, clwx, csys, tplj, tp1, tp2, tp3, tztp);
  }
  
  public long queryLimitSpeed(java.lang.String kkbh, java.lang.String fxlx, long cdh, java.lang.String cllx) throws java.rmi.RemoteException{
    if (trans == null)
      _initTransProxy();
    return trans.queryLimitSpeed(kkbh, fxlx, cdh, cllx);
  }
  
  public java.lang.String querySyncTime() throws java.rmi.RemoteException{
    if (trans == null)
      _initTransProxy();
    return trans.querySyncTime();
  }
  
  public java.lang.String getLastMessage() throws java.rmi.RemoteException{
    if (trans == null)
      _initTransProxy();
    return trans.getLastMessage();
  }
  
  
}