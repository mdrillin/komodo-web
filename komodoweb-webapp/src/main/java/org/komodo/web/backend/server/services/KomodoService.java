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
package org.komodo.web.backend.server.services;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.jboss.errai.bus.server.annotations.Service;
import org.komodo.core.KEngine;
import org.komodo.relational.vdb.Vdb;
import org.komodo.relational.vdb.internal.VdbImpl;
import org.komodo.relational.workspace.WorkspaceManager;
import org.komodo.repository.LocalRepository;
import org.komodo.repository.LocalRepository.LocalRepositoryId;
import org.komodo.spi.KException;
import org.komodo.spi.constants.StringConstants;
import org.komodo.spi.repository.KomodoObject;
import org.komodo.spi.repository.Repository;
import org.komodo.spi.repository.RepositoryClient.State;
import org.komodo.spi.repository.RepositoryObserver;
import org.komodo.utils.KLog;
import org.komodo.web.backend.server.services.util.Utils;
import org.komodo.web.client.resources.AppResource;
import org.komodo.web.share.beans.KomodoObjectBean;
import org.komodo.web.share.exceptions.KomodoUiException;
import org.komodo.web.share.services.IKomodoService;

import com.google.gwt.resources.client.DataResource;

/**
 * Concrete implementation of the Komodo service.  This service is used to interact with the Komodo instance
 *
 * @author mdrillin@redhat.com
 */
@Service
public class KomodoService implements IKomodoService {

	private static KEngine kEngine;

	private static WorkspaceManager wsManager;
	
    /**
     * Constructor.
     */
    public KomodoService() {
    }

    /**
     * Start the KEngine
     * @throws KomodoUiException
     */
    @Override
    public void startKEngine( ) throws KomodoUiException {
    	// If KEngine already started, return
    	if(isKEngineStarted()) return;

		/*
		 * Ensure Logging on Modeshape Engine is set to a sane level By default,
		 * it seems to revert to TRACE or ALL.
		 */
		try {
			KLog.getLogger().setLevel(Level.INFO);
		} catch (Exception e) {
			throw new KomodoUiException(e);
		}

		kEngine = KEngine.getInstance();
		final Repository defaultRepo = kEngine.getDefaultRepository();

		// Latch for awaiting the start of the default repository
		final CountDownLatch updateLatch = new CountDownLatch(1);

		// Observer attached to the default repository for listening for the change of state
		RepositoryObserver stateObserver = new RepositoryObserver() {

			@Override
			public void eventOccurred() {
				updateLatch.countDown();
			}
		};
		defaultRepo.addObserver(stateObserver);

		// Start KEngine
		try {
			kEngine.start();
		} catch (KException e) {
			throw new KomodoUiException(e);
		}

    	TimerTask progressTask = new TimerTask() {
    		@Override
    		public void run() {
    		    // Do Nothing
    		}
    	};

		Timer timer = new Timer();
		timer.schedule(progressTask, 0, 500);

		// Block the thread until the latch has counted down or timeout has been reached
		boolean localRepoWaiting = true;
		try {
			localRepoWaiting = updateLatch.await(3, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new KomodoUiException(e);
		}

    	// Cancel timer and display repository message
    	timer.cancel();

    	if (localRepoWaiting)
    		System.out.println("Started");
    	else
    		System.out.println("Local Repo Timeout");

    	wsManager = WorkspaceManager.getInstance(defaultRepo);
    	
    	//loadTestWorkspace();
    }
    
    /**
     * Shutdown the KEngine
     * @throws KomodoUiException
     */
    @Override
    public void shutdownKEngine( ) throws KomodoUiException {
    	if(kEngine!=null) {
        	// Stop KEngine
        	try {
    			kEngine.shutdown();
    		} catch (KException e) {
    			throw new KomodoUiException(e);
    		}
    	}
    	return;
    }
    
    private static boolean isKEngineStarted() {
    	boolean isStarted = false;
    	
    	if(kEngine!=null) {
    		State engineState = kEngine.getState();
    		if(engineState==State.STARTED) {
    			isStarted=true;
    		}
    	}
    	
    	return isStarted;
    }
    
    @Override
    public List<KomodoObjectBean> getKomodoNodes(final String kObjPath) throws KomodoUiException {
    	if(!isKEngineStarted()) {
    		startKEngine();
    	}
   	    Utils utils = Utils.getInstance();
    	List<KomodoObjectBean> result = new ArrayList<KomodoObjectBean>();
    	
  		Repository repo = kEngine.getDefaultRepository();
  		
  		// If kObjPath is null, get the root Vdbs
  		
    	KomodoObject[] children = null;
		try {
			if(kObjPath==null) {
				children = wsManager.findVdbs(null);
			} else {
				KomodoObject kObj = repo.getFromWorkspace(null, kObjPath);
				children = kObj.getChildren(null);
			}
		} catch (KException e) {
			throw new KomodoUiException(e);
		}
		if(children!=null && children.length>0) {
			for(KomodoObject child : children) {
				result.add(utils.createKomodoObjectBean(child));
			}
		}

		return result;
	}

	public KomodoObjectBean createVdb(final String vdbName) throws KomodoUiException {
		if(!isKEngineStarted()) {
			startKEngine();
		}
		Utils utils = Utils.getInstance();
		KomodoObjectBean result = null;

		Vdb newVdb;
		try {
			newVdb = wsManager.createVdb(null, null, vdbName, "extPath");
			result = utils.createKomodoObjectBean(newVdb);
		} catch (KException ex) {
			throw new KomodoUiException(ex);
		}

		return result;
	}

	public List<KomodoObjectBean> deleteVdb(final String vdbName) throws KomodoUiException {
		if(!isKEngineStarted()) {
			startKEngine();
		}

		try {
			KomodoObject[] vdbs = wsManager.findVdbs(null);
			for(KomodoObject vdb : vdbs) {
				String kVdbName = vdb.getName(null);
				if(vdbName.equals(kVdbName)) {
					wsManager.delete(null, vdb);
					break;
				}
			}
		} catch (KException ex) {
			throw new KomodoUiException(ex);
		}

		return getKomodoNodes(null);
	}

	public static void initLocalRepository() throws Exception {
		DataResource config = AppResource.INSTANCE.data().repositoryConfig();
		URL configUrl = new URL(config.getUrl());

		LocalRepositoryId id = new LocalRepositoryId(configUrl, StringConstants.DEFAULT_LOCAL_WORKSPACE_NAME);
		_repo = new LocalRepository(id);

		_repoObserver = new LocalRepositoryObserver();
		_repo.addObserver(_repoObserver);


		// Wait for the starting of the repository or timeout of 1 minute
		if (!_repoObserver.getLatch().await(1, TimeUnit.MINUTES)) {
			throw new RuntimeException("Local repository did not start");
		}
	}    

	public String getVdbDDL(final String vdbPath) throws KomodoUiException {
		if(!isKEngineStarted()) {
			startKEngine();
		}

		Repository repo = kEngine.getDefaultRepository();

		// If kObjPath is null, get the root Vdbs

		String vdbDdl = null;
		try {
			Vdb[] vdbs = wsManager.findVdbs(null);
			for(Vdb vdb : vdbs) {
				String thePath = vdb.getAbsolutePath();
				if(thePath.equals(vdbPath)) {
					vdbDdl = ((VdbImpl)vdb).export(null);
				}
			}
		} catch (KException e) {
			throw new KomodoUiException(e);
		}

		return vdbDdl;
	}

	protected static LocalRepository _repo = null;

	protected static LocalRepositoryObserver _repoObserver = null;

	protected static class LocalRepositoryObserver implements RepositoryObserver {

		private CountDownLatch latch;

		public LocalRepositoryObserver() {
			resetLatch();
		}

		public void resetLatch() {
			latch = new CountDownLatch(1);
		}

		/**
		 * @return the latch
		 */
		 public CountDownLatch getLatch() {
			 return this.latch;
		 }

		 @Override
		 public void eventOccurred() {
			 latch.countDown();
		 }
	}

}
