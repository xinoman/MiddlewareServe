package com.its.core.local.dianbai.ws;

public class TransProxy implements com.its.core.local.dianbai.ws.Trans {
  private String _endpoint = null;
  private com.its.core.local.dianbai.ws.Trans trans = null;
  
  public TransProxy() {
    _initTransProxy();
  }
  
  public TransProxy(String endpoint) {
    _endpoint = endpoint;
    _initTransProxy();
  }
  
  private void _initTransProxy() {
    try {
      trans = (new com.its.core.local.dianbai.ws.TransServiceLocator()).getTrans();
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
  
  public com.its.core.local.dianbai.ws.Trans getTrans() {
    if (trans == null)
      _initTransProxy();
    return trans;
  }
  
  public int initTrans(java.lang.String deviceId, java.lang.String deviceKey) throws java.rmi.RemoteException{
    if (trans == null)
      _initTransProxy();
    return trans.initTrans(deviceId, deviceKey);
  }
  
  public boolean closeTrans() throws java.rmi.RemoteException{
    if (trans == null)
      _initTransProxy();
    return trans.closeTrans();
  }
  
  public boolean writeVehicleInfo(java.lang.String deviceId, java.lang.String directionId, java.lang.String driveWay, java.lang.String licence, java.lang.String licenceType, java.lang.String passDateTime, long speed, long limitSpeed, java.lang.String violationType, long carLength, java.lang.String licenceColor, java.lang.String carType, java.lang.String picPath1, java.lang.String picPath2, java.lang.String picPath3, java.lang.String picPath4, java.lang.String violate, java.lang.String sendType) throws java.rmi.RemoteException{
    if (trans == null)
      _initTransProxy();
    return trans.writeVehicleInfo(deviceId, directionId, driveWay, licence, licenceType, passDateTime, speed, limitSpeed, violationType, carLength, licenceColor, carType, picPath1, picPath2, picPath3, picPath4, violate, sendType);
  }
  
  public boolean writeVehicleInfo2(java.lang.String deviceId, java.lang.String directionId, java.lang.String driveWay, java.lang.String licence, java.lang.String licenceType, java.lang.String passDateTime, long speed, long limitSpeed, java.lang.String violationType, long carLength, java.lang.String licenceColor, java.lang.String carType, java.lang.String backszLicense, java.lang.String backLicenseColor, java.lang.String backHpyz, java.lang.String clpp, java.lang.String clwx, java.lang.String csys, java.lang.String picPath1, java.lang.String picPath2, java.lang.String picPath3, java.lang.String picPath4, java.lang.String violate, java.lang.String sendType) throws java.rmi.RemoteException{
    if (trans == null)
      _initTransProxy();
    return trans.writeVehicleInfo2(deviceId, directionId, driveWay, licence, licenceType, passDateTime, speed, limitSpeed, violationType, carLength, licenceColor, carType, backszLicense, backLicenseColor, backHpyz, clpp, clwx, csys, picPath1, picPath2, picPath3, picPath4, violate, sendType);
  }
  
  public boolean writeDeviceStatus(java.lang.String deviceId, java.lang.String status, java.lang.String gzsj, java.lang.String gzzt1, java.lang.String gzzt2, java.lang.String gzzt3, java.lang.String gzzt4) throws java.rmi.RemoteException{
    if (trans == null)
      _initTransProxy();
    return trans.writeDeviceStatus(deviceId, status, gzsj, gzzt1, gzzt2, gzzt3, gzzt4);
  }
  
  public long queryLimitSpeed(java.lang.String deviceId, java.lang.String carType) throws java.rmi.RemoteException{
    if (trans == null)
      _initTransProxy();
    return trans.queryLimitSpeed(deviceId, carType);
  }
  
  public long queryViolateSpeed(java.lang.String deviceId, java.lang.String carType) throws java.rmi.RemoteException{
    if (trans == null)
      _initTransProxy();
    return trans.queryViolateSpeed(deviceId, carType);
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
  
  public java.lang.String flushPassFailMessage() throws java.rmi.RemoteException{
    if (trans == null)
      _initTransProxy();
    return trans.flushPassFailMessage();
  }
  
  
}