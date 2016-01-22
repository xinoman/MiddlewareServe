/**
 * TransServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.its.core.local.dianbai.ws;

public class TransServiceLocator extends org.apache.axis.client.Service implements com.its.core.local.dianbai.ws.TransService {

    public TransServiceLocator() {
    }


    public TransServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public TransServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for Trans
    private java.lang.String Trans_address = "http://10.47.98.188:9080/jcbktrans/services/Trans";

    public java.lang.String getTransAddress() {
        return Trans_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String TransWSDDServiceName = "Trans";

    public java.lang.String getTransWSDDServiceName() {
        return TransWSDDServiceName;
    }

    public void setTransWSDDServiceName(java.lang.String name) {
        TransWSDDServiceName = name;
    }

    public com.its.core.local.dianbai.ws.Trans getTrans() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(Trans_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getTrans(endpoint);
    }

    public com.its.core.local.dianbai.ws.Trans getTrans(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.its.core.local.dianbai.ws.TransSoapBindingStub _stub = new com.its.core.local.dianbai.ws.TransSoapBindingStub(portAddress, this);
            _stub.setPortName(getTransWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setTransEndpointAddress(java.lang.String address) {
        Trans_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.its.core.local.dianbai.ws.Trans.class.isAssignableFrom(serviceEndpointInterface)) {
                com.its.core.local.dianbai.ws.TransSoapBindingStub _stub = new com.its.core.local.dianbai.ws.TransSoapBindingStub(new java.net.URL(Trans_address), this);
                _stub.setPortName(getTransWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("Trans".equals(inputPortName)) {
            return getTrans();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://webservice.dc.ht.com", "TransService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://webservice.dc.ht.com", "Trans"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("Trans".equals(portName)) {
            setTransEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
