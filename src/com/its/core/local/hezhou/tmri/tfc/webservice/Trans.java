/**
 * Trans.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.its.core.local.hezhou.tmri.tfc.webservice;

public interface Trans extends java.rmi.Remote {
    public long initTrans(java.lang.String kkbh, java.lang.String fxlx, long cdh, java.lang.String info) throws java.rmi.RemoteException;
    public long writeVehicleInfo(java.lang.String kkbh, java.lang.String fxlx, long cdh, java.lang.String hphm, java.lang.String hpzl, java.lang.String gcsj, long clsd, long clxs, java.lang.String wfdm, long cwkc, java.lang.String hpys, java.lang.String cllx, java.lang.String fzhpzl, java.lang.String fzhphm, java.lang.String fzhpys, java.lang.String clpp, java.lang.String clwx, java.lang.String csys, java.lang.String tplj, java.lang.String tp1, java.lang.String tp2, java.lang.String tp3, java.lang.String tztp) throws java.rmi.RemoteException;
    public long queryLimitSpeed(java.lang.String kkbh, java.lang.String fxlx, long cdh, java.lang.String cllx) throws java.rmi.RemoteException;
    public java.lang.String querySyncTime() throws java.rmi.RemoteException;
    public java.lang.String getLastMessage() throws java.rmi.RemoteException;
}
