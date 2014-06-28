package download.manager.api;

import java.util.concurrent.ThreadPoolExecutor;

import download.manager.Status;

/**
 * Contains information of a particular download process.
 * 
 * @author Vladislav Fedotov
 * 
 */
public interface DownloadTask {
    /**
     * Sets identifier of current task.
     * 
     * @param downloadTaskId
     *            download task identifier
     */
    // public void setId(String downloadTaskId);

    /**
     * Gets identifier of current task
     * 
     * @return task identifier
     */
    public String getId();

    /**
     * Returns URL of resource that corresponds to this task.
     * 
     * @return corresponding URL
     */
    public String getUrl();

    /**
     * Starts loading specified resource with the help of provided thread pool.
     * 
     * @param threadPool
     *            thread pool
     */
    public void download(ThreadPoolExecutor threadPool);

    /**
     * Pauses current download task.
     */
    public void pause();

    /**
     * Tells whether the current download task was paused {@code true} or not {@code false} {@link Status#PAUSED}.
     * 
     * @return
     */
    boolean isPaused();

    /**
     * Cancels current download task.
     */
    public void cancel();

    /**
     * Returns status of current download task {@link Status}.
     * 
     * @return status of current download task
     */
    public Status getStatus();

    /**
     * Marks this download task as completed {@link Status#COMPLETE}.
     */
    // public void complete();

    /**
     * Tells whether the current download task was completed {@code true} or not {@code false}.
     * 
     * @return whether the current download task was completed
     */
    public boolean isComplete();

    /**
     * Marks this download task as task that proceeded with some errors {@link Status#ERROR}.
     */
    // public void error();

    /**
     * Resumes downloading of current task.
     */
    public void resume();

    public String getProgress();

    public String getFileName();

}
