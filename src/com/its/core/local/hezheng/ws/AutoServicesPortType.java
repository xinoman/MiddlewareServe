/**
 * AutoServicesPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.its.core.local.hezheng.ws;

public interface AutoServicesPortType extends java.rmi.Remote {
    public java.lang.String queryArmyVehicle(java.lang.String xlh, java.lang.String pzbh) throws java.rmi.RemoteException;
    public java.lang.String queryPoliceman(java.lang.String xlh, java.lang.String jybh) throws java.rmi.RemoteException;
    public java.lang.String writeSurveil(java.lang.String xlh, java.lang.String doc) throws java.rmi.RemoteException;
    public java.lang.String writeForce(java.lang.String xlh, java.lang.String doc) throws java.rmi.RemoteException;
    public java.lang.String querySurveil(java.lang.String xlh, java.lang.String pzbh, java.lang.String hpzl, java.lang.String hphm) throws java.rmi.RemoteException;
    public java.lang.String queryVehicle(java.lang.String xlh, java.lang.String hpzl, java.lang.String hphm) throws java.rmi.RemoteException;
    public java.lang.String queryRoad(java.lang.String xlh, java.lang.String xzqh, java.lang.String glbm, java.lang.String dldm) throws java.rmi.RemoteException;
    public java.lang.String queryViolation(java.lang.String xlh, java.lang.String jdsbh, java.lang.String jszh) throws java.rmi.RemoteException;
    public java.lang.String writeViolation(java.lang.String xlh, java.lang.String doc) throws java.rmi.RemoteException;
    public java.lang.String queryVioCode(java.lang.String xlh, java.lang.String wfxwdm) throws java.rmi.RemoteException;
    public java.lang.String queryWrit(java.lang.String xlh, java.lang.String city, java.lang.String glbm, java.lang.String wslb) throws java.rmi.RemoteException;
    public java.lang.String writeArmyVehicle(java.lang.String xlh, java.lang.String doc) throws java.rmi.RemoteException;
    public java.lang.String querySurveilPhoto(java.lang.String xlh, java.lang.String xh) throws java.rmi.RemoteException;
    public java.lang.String queryForce(java.lang.String xlh, java.lang.String pzbh) throws java.rmi.RemoteException;
    public java.lang.String writeSurveilAll(java.lang.String xlh, java.lang.String doc) throws java.rmi.RemoteException;
    public java.lang.String managerSurveil(java.lang.String xlh, java.lang.String doc) throws java.rmi.RemoteException;
    public java.lang.String queryDriver(java.lang.String xlh, java.lang.String jszh, java.lang.String dabh) throws java.rmi.RemoteException;
    public java.lang.String queryDepartment(java.lang.String xlh, java.lang.String glbm) throws java.rmi.RemoteException;
}
