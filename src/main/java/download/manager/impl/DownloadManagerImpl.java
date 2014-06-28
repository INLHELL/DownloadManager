package download.manager.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import download.manager.api.DownloadManager;
import download.manager.api.DownloadTask;

public class DownloadManagerImpl implements DownloadManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadManagerImpl.class);

    public static final int DEFAULT_POOL_SIZE = 10;

    private Map<String, DownloadTask> downloadTasks = Collections.synchronizedMap(new HashMap<String, DownloadTask>());
    private ThreadPoolExecutor threadsPool;

    public static DownloadManagerImpl getInstance() {
	return SingletonHolder.HOLDER_INSTANCE;
    }

    public static class SingletonHolder {
	public static final DownloadManagerImpl HOLDER_INSTANCE = new DownloadManagerImpl();
    }

    private DownloadManagerImpl() {
	LOGGER.info("New thread pool with size: '{}', will be created.", DEFAULT_POOL_SIZE);
	threadsPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);
    }

    @Override
    public String add(final String url, final String fileName) {
	LOGGER.info("New download task was added.");
	DownloadTask downloadTask = new DownloadTaskImpl(url, fileName);
	downloadTasks.put(downloadTask.getId(), downloadTask);
	return downloadTask.getId();
    }

    @Override
    public String addAndDownload(final String url, final String fileName) {
	LOGGER.info("New download task will be created for URL: '{}', and will be saved to file with a name: '{}'.",
		url, fileName);
	DownloadTask downloadTask = new DownloadTaskImpl(url, fileName);
	downloadTasks.put(downloadTask.getId(), downloadTask);
	LOGGER.info("Download task was added: '{}', to set of tasks.", downloadTasks.containsKey(downloadTask.getId()));
	LOGGER.info("Download task will start downloading process.");
	downloadTask.download(threadsPool);
	return downloadTask.getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see DownloadManager#cancel(int)
     */
    @Override
    public void cancel(String downloadTaskId) {
	if (downloadTasks.containsKey(downloadTaskId)) {
	    LOGGER.info("Download task with ID: '{}' will be canceled.", downloadTaskId);
	    downloadTasks.get(downloadTaskId).cancel();
	}
	else {
	    LOGGER.warn("Download task with given ID: '{}' does not exist.", downloadTaskId);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see DownloadManager#download(int)
     */
    @Override
    public void download(String downloadTaskId) {
	if (downloadTasks.containsKey(downloadTaskId)) {
	    LOGGER.info("Download task with ID: '{}' will be downloaded.", downloadTaskId);
	    downloadTasks.get(downloadTaskId).download(threadsPool);
	}
	else {
	    LOGGER.warn("Download task with given ID: '{}' does not exist.", downloadTaskId);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see DownloadManager#forceShutdown()
     */
    @Override
    public void forceShutdown() {
	threadsPool.shutdownNow();
    }

    /*
     * (non-Javadoc)
     * 
     * @see DownloadManager#get(int)
     */
    @Override
    public DownloadTask get(String id) {
	return downloadTasks.get(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see DownloadManager#pause(int)
     */
    @Override
    public void pause(String downloadTaskId) {
	if (downloadTasks.containsKey(downloadTaskId)) {
	    LOGGER.info("Download task with ID: '{}' will be paused.", downloadTaskId);
	    downloadTasks.get(downloadTaskId).pause();
	}
	else {
	    LOGGER.warn("Download task with given ID: '{}' does not exist.", downloadTaskId);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see DownloadManager#resume(int)
     */
    @Override
    public void resume(String downloadTaskId) {
	if (downloadTasks.containsKey(downloadTaskId)) {
	    LOGGER.info("Download task with ID: '{}' will be resumed.", downloadTaskId);
	    downloadTasks.get(downloadTaskId).resume();
	}
	else {
	    LOGGER.warn("Download task with given ID: '{}' does not exist.", downloadTaskId);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see DownloadManager#setPoolSize(int)
     */
    @Override
    public void setPoolSize(int poolSize) {
	threadsPool.setCorePoolSize(poolSize);
    }

    /*
     * (non-Javadoc)
     * 
     * @see DownloadManager#shutdown()
     */
    @Override
    public void shutdown() {
	threadsPool.shutdown();
    }

}
