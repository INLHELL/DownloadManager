package download.manager.api;

/**
 * Main API that provided to the end user.
 * 
 * @author Vladislav Fedotov
 * 
 */
public interface DownloadManager {

    public String add(String url, String fileName);

    public String addAndDownload(String url, String fileName);

    /**
     * Cancels download task by given identifier.
     * 
     * @param downloadTaskId
     *            download task identifier that should be canceled
     */
    public void cancel(String downloadTaskId);

    /**
     * Tells {@link DownloadManager} to start loading specified by provided download task identifier.
     * 
     * @param downloadTaskId
     *            task identifier that should be downloaded
     */
    public void download(String downloadTaskId);

    /**
     * Forces to Shutdown current instance of {@link DownloadManager}.
     */
    public void forceShutdown();

    /**
     * Returns instance of {@link DownloadTask} class by provided download task identifier.
     * 
     * @param downloadTaskId
     *            download task identifier
     * @return instance of {@link DownloadTask} class
     */
    public DownloadTask get(String downloadTaskId);

    /**
     * Pauses download task by given identifier.
     * 
     * @param downloadTaskId
     *            download task identifier that should be paused
     */
    public void pause(String downloadTaskId);

    /**
     * Resumes particular download task by provided download task identifier.
     * 
     * @param downloadTaskId
     *            download task identifier
     */
    public void resume(String downloadTaskId);

    /**
     * Sets pool size for internal thread pool.
     * 
     * @param poolSize
     *            number of pooled threads
     */
    public void setPoolSize(int poolSize);

    /**
     * Shutdowns current instance of {@link DownloadManager}.
     */
    public void shutdown();

}
