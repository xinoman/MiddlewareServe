/**
 * AutoServicesLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.its.core.local.hezheng.ws;

public class AutoServicesLocator extends org.apache.axis.client.Service implements com.its.core.local.hezheng.ws.AutoServices {

    public AutoServicesLocator() {
    }


    public AutoServicesLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AutoServicesLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for autoServicesHttpPort
    private java.lang.String autoServicesHttpPort_address = "http://10.178.99.36:9080/dzjcWeb/services/autoServices";

    public java.lang.String getautoServicesHttpPortAddress() {
        return autoServicesHttpPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String autoServicesHttpPortWSDDServiceName = "autoServicesHttpPort";

    public java.lang.String getautoServicesHttpPortWSDDServiceName() {
        return autoServicesHttpPortWSDDServiceName;
    }

    public void setautoServicesHttpPortWSDDServiceName(java.lang.String name) {
        autoServicesHttpPortWSDDServiceName = name;
    }

    public com.its.core.local.hezheng.ws.AutoServicesPortType getautoServicesHttpPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(autoServicesHttpPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getautoServicesHttpPort(endpoint);
    }

    public com.its.core.local.hezheng.ws.AutoServicesPortType getautoServicesHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.its.core.local.hezheng.ws.AutoServicesHttpBindingStub _stub = new com.its.core.local.hezheng.ws.AutoServicesHttpBindingStub(portAddress, this);
            _stub.setPortName(getautoServicesHttpPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setautoServicesHttpPortEndpointAddress(java.lang.String address) {
        autoServicesHttpPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.its.core.local.hezheng.ws.AutoServicesPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.its.core.local.hezheng.ws.AutoServicesHttpBindingStub _stub = new com.its.core.local.hezheng.ws.AutoServicesHttpBindingStub(new java.net.URL(autoServicesHttpPort_address), this);
                _stub.setPortName(getautoServicesHttpPortWSDDServiceName());
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
        if ("autoServicesHttpPort".equals(inputPortName)) {
            return getautoServicesHttpPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://pdaservice.zlkj.com", "autoServices");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://pdaservice.zlkj.com", "autoServicesHttpPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("autoServicesHttpPort".equals(portName)) {
            setautoServicesHttpPortEndpointAddress(address);
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
