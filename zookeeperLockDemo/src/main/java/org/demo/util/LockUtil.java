package org.demo.util;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.demo.model.LockModel;

public class LockUtil {
	private static final Logger logger = Logger.getLogger(LockUtil.class);

	private static ZooKeeper zookeeper;
	private final static CountDownLatch countdownLatch = new CountDownLatch(1);
	private static String zookeeperUrl= "localhost:2181";

	private static String getPath(LockModel model) {
		return "/"+model.getApplicationName()+ model.getAppIdentifier();
	}

	//connect to zookeeper
	public static void connect() throws IOException, InterruptedException {
		if(zookeeper != null) {
			logger.info("Returning existing connection...");
			return;
		}
		logger.info("Try to get new connection...");
		zookeeper = new ZooKeeper(zookeeperUrl, 5000, new Watcher() {
			public void process(WatchedEvent event) {
				if(event.getState() == KeeperState.SyncConnected) {
					countdownLatch.countDown();
				}				
			}
		});
		logger.info("Connection established...");
		countdownLatch.await();
	}

	public static void delete(LockModel model) throws InterruptedException, KeeperException {
		String path = getPath(model);
		zookeeper.delete(path, zookeeper.exists(path, true).getVersion());
		logger.info("lock removed from zookeeper: "+path);
	}

	public static void create(LockModel model) throws KeeperException, InterruptedException {
		zookeeper.create(getPath(model), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
	}

	public static void close() throws InterruptedException {
		logger.info("Shutting down the current zookeeper session/connection.");
		if(zookeeper != null) {
			zookeeper.close();
			zookeeper = null;
		}
	}

	public static boolean acquireLock(LockModel model)  {
		String path = getPath(model);
		try {
			LockUtil.connect();

			Stat currentStat = zookeeper.exists(path, true);
			if(currentStat == null) {
				logger.info("no lock found, aquiring lock for path: "+path);
				create(model);
				return true;
			}
			logger.info("Skipping as Lock exists for path: "+path);
		} catch (Exception ex) {
			//error message
			logger.info("Failed to aquire lock for path: "+path);
			ex.printStackTrace();
		}
		return false;
	}

	public static boolean releaseLock(LockModel model) {
		String path = getPath(model);
		try {
			Stat currentStat = zookeeper.exists(path, true);
			if(currentStat != null) {
				logger.info("Release lock for path: "+path);
				delete(model);
				return true;
			}
			logger.info("Lock not found, nothing to release lock for path: "+path);
		}catch (Exception ex) {
			//error message
			logger.info("Failed to release lock for path: "+path);
			ex.printStackTrace();
		}
		return false;
	}
	
	public static void main(String[] args) {
		LockModel model = new LockModel();
		model.setApplicationName("DemoService");
		model.setAppIdentifier("1");
		
		try {
			LockUtil.acquireLock(model);
			LockUtil.acquireLock(model);
			LockUtil.close();
			LockUtil.acquireLock(model);
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {			
			try {
				LockUtil.releaseLock(model);
				LockUtil.close();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}

