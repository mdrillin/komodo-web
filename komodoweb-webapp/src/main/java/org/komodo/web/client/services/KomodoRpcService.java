/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.komodo.web.client.services;

import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.komodo.web.client.services.rpc.DelegatingErrorCallback;
import org.komodo.web.client.services.rpc.DelegatingRemoteCallback;
import org.komodo.web.client.services.rpc.IRpcServiceInvocationHandler;
import org.komodo.web.share.beans.KomodoObjectBean;
import org.komodo.web.share.beans.KomodoObjectPropertyBean;
import org.komodo.web.share.exceptions.KomodoUiException;
import org.komodo.web.share.services.IKomodoService;

/**
 * Client-side service for making RPC calls to the remote Komodo service.
 *
 * @author mdrillin@redhat.com
 */
@ApplicationScoped
public class KomodoRpcService {
	
    /**
     * @return the service instance
     */
    public static KomodoRpcService get() {
        Collection<IOCBeanDef<KomodoRpcService>> beans = IOC.getBeanManager().lookupBeans(KomodoRpcService.class);
        IOCBeanDef<KomodoRpcService> beanDef = beans.iterator().next();
        return beanDef.getInstance();
    }

    @Inject
    private Caller<IKomodoService> remoteKomodoService;

    /**
     * Constructor.
     */
    public KomodoRpcService() {
    }

    /**
     * Start the Komodo Engine
     *
     * @param handler rpc handler
     */
    public void startKEngine(final IRpcServiceInvocationHandler<Void> handler) {
        RemoteCallback<Void> successCallback = new DelegatingRemoteCallback<Void>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteKomodoService.call(successCallback, errorCallback).startKEngine();
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * Shutdown the Komodo Engine
     *
     * @param handler rpc handler
     */
    public void shutdownKEngine(final IRpcServiceInvocationHandler<Void> handler) {
        RemoteCallback<Void> successCallback = new DelegatingRemoteCallback<Void>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteKomodoService.call(successCallback, errorCallback).shutdownKEngine();
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * Retrieve the komodo node at the given path
     *
     * @param path komodo object path
     * @param handler rpc handler
     */
    public void getKomodoNode(final String path, final IRpcServiceInvocationHandler<KomodoObjectBean> handler) {
        RemoteCallback<KomodoObjectBean> successCallback = new DelegatingRemoteCallback<KomodoObjectBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteKomodoService.call(successCallback, errorCallback).getKomodoNode(path);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * Retrieve the komodo nodes at the given path
     *
     * @param kObjectPath komodo object path
     * @param handler rpc handler
     */
    public void getKomodoNodes(final String kObjectPath, final IRpcServiceInvocationHandler<List<KomodoObjectBean>> handler) {
        RemoteCallback<List<KomodoObjectBean>> successCallback = new DelegatingRemoteCallback<List<KomodoObjectBean>>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteKomodoService.call(successCallback, errorCallback).getKomodoNodes(kObjectPath);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * Derive a json tree of the given object path and its children
     *
     * @param kObjectPath the root object
     * @param handler the callback handler
     */
    public void deriveJsonTree(String kObjectPath, final IRpcServiceInvocationHandler<String> handler) {
        RemoteCallback<String> successCallback = new DelegatingRemoteCallback<String>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteKomodoService.call(successCallback, errorCallback).deriveJsonTree(kObjectPath);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * Adds a new property
     *
     * @param parentPath the path of the property parent
     * @param name the name
     * @param value the value
     * @param handler rpc handler
     */
    public void addProperty(String parentPath, String name, String value, IRpcServiceInvocationHandler<KomodoObjectBean> handler) {
        RemoteCallback<KomodoObjectBean> successCallback = new DelegatingRemoteCallback<KomodoObjectBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteKomodoService.call(successCallback, errorCallback).addProperty(parentPath, name, value);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * Removes the given komodo property
     *
     * @param propertyBean property to be removed
     * @param handler rpc handler
     */
    public void removeProperty(KomodoObjectPropertyBean propertyBean, IRpcServiceInvocationHandler<KomodoObjectBean> handler) {
        RemoteCallback<KomodoObjectBean> successCallback = new DelegatingRemoteCallback<KomodoObjectBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteKomodoService.call(successCallback, errorCallback).removeProperty(propertyBean);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * Updates the given komodo property
     *
     * @param propertyBean property to be updated
     * @param newValue the new value
     * @param handler rpc handler
     */
    public void updateProperty(KomodoObjectPropertyBean propertyBean, Object newValue, final IRpcServiceInvocationHandler<KomodoObjectPropertyBean> handler) {
        RemoteCallback<KomodoObjectPropertyBean> successCallback = new DelegatingRemoteCallback<KomodoObjectPropertyBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteKomodoService.call(successCallback, errorCallback).updateProperty(propertyBean, newValue);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * Create a new vdb
     *
     * @param vdbName the name of the vdb
     * @param handler rpc handler
     */
    public void createVdb(final String vdbName, final IRpcServiceInvocationHandler<KomodoObjectBean> handler) {
        RemoteCallback<KomodoObjectBean> successCallback = new DelegatingRemoteCallback<KomodoObjectBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteKomodoService.call(successCallback, errorCallback).createVdb(vdbName);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * Delete the given vdb
     *
     * @param vdbName the name of the vdb
     * @param handler rpc handler
     */
    public void deleteVdb(final String vdbName, final IRpcServiceInvocationHandler<List<KomodoObjectBean>> handler) {
        RemoteCallback<List<KomodoObjectBean>> successCallback = new DelegatingRemoteCallback<List<KomodoObjectBean>>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteKomodoService.call(successCallback, errorCallback).deleteVdb(vdbName);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * Retrieve the DDL for the vdb at the given path
     *
     * @param vdbPath the path of the vdb
     * @param handler rpc handler
     */
    public void getVdbDDL(final String vdbPath, final IRpcServiceInvocationHandler<String> handler) {
        RemoteCallback<String> successCallback = new DelegatingRemoteCallback<String>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteKomodoService.call(successCallback, errorCallback).getVdbDDL(vdbPath);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }

}
