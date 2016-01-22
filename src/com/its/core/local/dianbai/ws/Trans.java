/**
 * Trans.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.its.core.local.dianbai.ws;

public interface Trans extends java.rmi.Remote {
    public int initTrans(java.lang.String deviceId, java.lang.String deviceKey) throws java.rmi.RemoteException;
    public boolean closeTrans() throws java.rmi.RemoteException;
    public boolean writeVehicleInfo(java.lang.String deviceId, java.lang.String directionId, java.lang.String driveWay, java.lang.String licence, java.lang.String licenceType, java.lang.String passDateTime, long speed, long limitSpeed, java.lang.String violationType, long carLength, java.lang.String licenceColor, java.lang.String carType, java.lang.String picPath1, java.lang.String picPath2, java.lang.String picPath3, java.lang.String picPath4, java.lang.String violate, java.lang.String sendType) throws java.rmi.RemoteException;
    public boolean writeVehicleInfo2(java.lang.String deviceId, java.lang.String directionId, java.lang.String driveWay, java.lang.String licence, java.lang.String licenceType, java.lang.String passDateTime, long speed, long limitSpeed, java.lang.String violationType, long carLength, java.lang.String licenceColor, java.lang.String carType, java.lang.String backszLicense, java.lang.String backLicenseColor, java.lang.String backHpyz, java.lang.String clpp, java.lang.String clwx, java.lang.String csys, java.lang.String picPath1, java.lang.String picPath2, java.lang.String picPath3, java.lang.String picPath4, java.lang.String violate, java.lang.String sendType) throws java.rmi.RemoteException;
    public boolean writeDeviceStatus(java.lang.String deviceId, java.lang.String status, java.lang.String gzsj, java.lang.String gzzt1, java.lang.String gzzt2, java.lang.String gzzt3, java.lang.String gzzt4) throws java.rmi.RemoteException;
    public long queryLimitSpeed(java.lang.String deviceId, java.lang.String carType) throws java.rmi.RemoteException;
    public long queryViolateSpeed(java.lang.String deviceId, java.lang.String carType) throws java.rmi.RemoteException;
    public java.lang.String querySyncTime() throws java.rmi.RemoteException;
    public java.lang.String getLastMessage() throws java.rmi.RemoteException;
    public java.lang.String flushPassFailMessage() throws java.rmi.RemoteException;
}
