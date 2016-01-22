package com.its.core.local.hezheng.ws;

public class AutoServicesPortTypeProxy implements com.its.core.local.hezheng.ws.AutoServicesPortType {
  private String _endpoint = null;
  private com.its.core.local.hezheng.ws.AutoServicesPortType autoServicesPortType = null;
  
  public AutoServicesPortTypeProxy() {
    _initAutoServicesPortTypeProxy();
  }
  
  public AutoServicesPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initAutoServicesPortTypeProxy();
  }
  
  private void _initAutoServicesPortTypeProxy() {
    try {
      autoServicesPortType = (new com.its.core.local.hezheng.ws.AutoServicesLocator()).getautoServicesHttpPort();
      if (autoServicesPortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)autoServicesPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)autoServicesPortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (autoServicesPortType != null)
      ((javax.xml.rpc.Stub)autoServicesPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.its.core.local.hezheng.ws.AutoServicesPortType getAutoServicesPortType() {
    if (autoServicesPortType == null)
      _initAutoServicesPortTypeProxy();
    return autoServicesPortType;
  }
  
  public java.lang.String queryArmyVehicle(java.lang.String xlh, java.lang.String pzbh) throws java.rmi.RemoteException{
    if (autoServicesPortType == null)
      _initAutoServicesPortTypeProxy();
    return autoServicesPortType.queryArmyVehicle(xlh, pzbh);
  }
  
  public java.lang.String queryPoliceman(java.lang.String xlh, java.lang.String jybh) throws java.rmi.RemoteException{
    if (autoServicesPortType == null)
      _initAutoServicesPortTypeProxy();
    return autoServicesPortType.queryPoliceman(xlh, jybh);
  }
  
  public java.lang.String writeSurveil(java.lang.String xlh, java.lang.String doc) throws java.rmi.RemoteException{
    if (autoServicesPortType == null)
      _initAutoServicesPortTypeProxy();
    return autoServicesPortType.writeSurveil(xlh, doc);
  }
  
  public java.lang.String writeForce(java.lang.String xlh, java.lang.String doc) throws java.rmi.RemoteException{
    if (autoServicesPortType == null)
      _initAutoServicesPortTypeProxy();
    return autoServicesPortType.writeForce(xlh, doc);
  }
  
  public java.lang.String querySurveil(java.lang.String xlh, java.lang.String pzbh, java.lang.String hpzl, java.lang.String hphm) throws java.rmi.RemoteException{
    if (autoServicesPortType == null)
      _initAutoServicesPortTypeProxy();
    return autoServicesPortType.querySurveil(xlh, pzbh, hpzl, hphm);
  }
  
  public java.lang.String queryVehicle(java.lang.String xlh, java.lang.String hpzl, java.lang.String hphm) throws java.rmi.RemoteException{
    if (autoServicesPortType == null)
      _initAutoServicesPortTypeProxy();
    return autoServicesPortType.queryVehicle(xlh, hpzl, hphm);
  }
  
  public java.lang.String queryRoad(java.lang.String xlh, java.lang.String xzqh, java.lang.String glbm, java.lang.String dldm) throws java.rmi.RemoteException{
    if (autoServicesPortType == null)
      _initAutoServicesPortTypeProxy();
    return autoServicesPortType.queryRoad(xlh, xzqh, glbm, dldm);
  }
  
  public java.lang.String queryViolation(java.lang.String xlh, java.lang.String jdsbh, java.lang.String jszh) throws java.rmi.RemoteException{
    if (autoServicesPortType == null)
      _initAutoServicesPortTypeProxy();
    return autoServicesPortType.queryViolation(xlh, jdsbh, jszh);
  }
  
  public java.lang.String writeViolation(java.lang.String xlh, java.lang.String doc) throws java.rmi.RemoteException{
    if (autoServicesPortType == null)
      _initAutoServicesPortTypeProxy();
    return autoServicesPortType.writeViolation(xlh, doc);
  }
  
  public java.lang.String queryVioCode(java.lang.String xlh, java.lang.String wfxwdm) throws java.rmi.RemoteException{
    if (autoServicesPortType == null)
      _initAutoServicesPortTypeProxy();
    return autoServicesPortType.queryVioCode(xlh, wfxwdm);
  }
  
  public java.lang.String queryWrit(java.lang.String xlh, java.lang.String city, java.lang.String glbm, java.lang.String wslb) throws java.rmi.RemoteException{
    if (autoServicesPortType == null)
      _initAutoServicesPortTypeProxy();
    return autoServicesPortType.queryWrit(xlh, city, glbm, wslb);
  }
  
  public java.lang.String writeArmyVehicle(java.lang.String xlh, java.lang.String doc) throws java.rmi.RemoteException{
    if (autoServicesPortType == null)
      _initAutoServicesPortTypeProxy();
    return autoServicesPortType.writeArmyVehicle(xlh, doc);
  }
  
  public java.lang.String querySurveilPhoto(java.lang.String xlh, java.lang.String xh) throws java.rmi.RemoteException{
    if (autoServicesPortType == null)
      _initAutoServicesPortTypeProxy();
    return autoServicesPortType.querySurveilPhoto(xlh, xh);
  }
  
  public java.lang.String queryForce(java.lang.String xlh, java.lang.String pzbh) throws java.rmi.RemoteException{
    if (autoServicesPortType == null)
      _initAutoServicesPortTypeProxy();
    return autoServicesPortType.queryForce(xlh, pzbh);
  }
  
  public java.lang.String writeSurveilAll(java.lang.String xlh, java.lang.String doc) throws java.rmi.RemoteException{
    if (autoServicesPortType == null)
      _initAutoServicesPortTypeProxy();
    return autoServicesPortType.writeSurveilAll(xlh, doc);
  }
  
  public java.lang.String managerSurveil(java.lang.String xlh, java.lang.String doc) throws java.rmi.RemoteException{
    if (autoServicesPortType == null)
      _initAutoServicesPortTypeProxy();
    return autoServicesPortType.managerSurveil(xlh, doc);
  }
  
  public java.lang.String queryDriver(java.lang.String xlh, java.lang.String jszh, java.lang.String dabh) throws java.rmi.RemoteException{
    if (autoServicesPortType == null)
      _initAutoServicesPortTypeProxy();
    return autoServicesPortType.queryDriver(xlh, jszh, dabh);
  }
  
  public java.lang.String queryDepartment(java.lang.String xlh, java.lang.String glbm) throws java.rmi.RemoteException{
    if (autoServicesPortType == null)
      _initAutoServicesPortTypeProxy();
    return autoServicesPortType.queryDepartment(xlh, glbm);
  }
  
  
}