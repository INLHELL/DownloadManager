package download.manager.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import download.manager.api.DownloadTask;

public class DownloadTaskImpl implements Runnable, DownloadTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadTaskImpl.class);
    private static final int BUFFER_SIZE = 1024;
    private static final String DELIMITER = ".";
    private static final String TMP_FILE_EXSTENSION = "tmp";
    private final String ID = UUID.randomUUID().toString();

    private Lock lock = new ReentrantLock();
    private volatile int downloadedBytes = 0;
    private volatile int remainderContentLength = 0;
    private volatile int totalContentLength = 0;
    private volatile boolean makePause = false;
    private volatile Status status;
    private final String targetFileName;
    private final String tmpFileName;
    private final String url;
    private BufferedInputStream bufferedInputStream;
    private RandomAccessFile targetRandomAccessFile;
    private RandomAccessFile tmpRandomAccessFile;
    private ThreadPoolExecutor threadPool;
    private File targetFile;
    private File tmpFile;

    public DownloadTaskImpl(final String url, final String fileName) {
	LOGGER.info("New download task will be created, with ID: '{}'.", ID);
	this.url = url;
	targetFileName = fileName;
	tmpFileName = fileName + DELIMITER + ID + DELIMITER + TMP_FILE_EXSTENSION;
	LOGGER.info("Temporary file name: '{}', was built'", tmpFileName);

	targetFile = createFile(targetFileName);
	targetRandomAccessFile = createRandomAccessFile(targetFile);
	LOGGER.info("Target file was created.");

	tmpFile = createFile(tmpFileName);
	tmpRandomAccessFile = createRandomAccessFile(tmpFile);
	LOGGER.info("Temporary file was created.");

	status = Status.CREATED;
	LOGGER.info("Status of this task: '{}'.", status);
    }

    private void closeResources() {
	LOGGER.info("Files and input stream will be closed.");
	try {
	    bufferedInputStream.close();
	    tmpRandomAccessFile.close();
	    targetRandomAccessFile.close();
	}
	catch (IOException e) {
	    LOGGER.warn(e.getMessage());
	}
    }

    private File createFile(String fileName) {
	LOGGER.info("New file: '{}', will be created.", fileName);
	File file = null;
	try {
	    file = new File(fileName);
	    file.createNewFile();
	}
	catch (IOException e) {
	    LOGGER.error(e.getMessage());
	}
	return file;
    }

    private RandomAccessFile createRandomAccessFile(File file) {
	LOGGER.info("New random access file: '{}', will be created.", file.getName());
	RandomAccessFile randomAccessFile = null;
	try {
	    randomAccessFile = new RandomAccessFile(file.getName(), "rw");
	}
	catch (IOException e) {
	    LOGGER.error(e.getMessage());
	}
	return randomAccessFile;
    }

    private void deleteFile(File file) {
	synchronized (lock) {
	    if (file.exists()) {
		LOGGER.info("Temporary file with name: '{}' will be deleted.", file);
		if (!file.delete()) {
		    LOGGER.warn("Temporary file with name: '{}' was not deleted.", file);
		}
	    }
	}
    }

    private BufferedInputStream openConnection() {
	BufferedInputStream bufferedInputStream = null;
	try {
	    URL targetUrl = new URL(url);
	    LOGGER.info("Will try to connect to given URL: '{}'", url);
	    URLConnection connectionUrl = targetUrl.openConnection();
	    connectionUrl.setRequestProperty("Range", "bytes=" + downloadedBytes + "-");
	    if (totalContentLength == 0) {
		totalContentLength = connectionUrl.getContentLength();
	    }
	    remainderContentLength = connectionUrl.getContentLength();
	    LOGGER.info("Total content length that will be downloaded: '{}'", remainderContentLength);
	    bufferedInputStream = new BufferedInputStream(connectionUrl.getInputStream());
	}
	catch (IOException e) {
	    LOGGER.error(e.getMessage());
	}
	return bufferedInputStream;
    }

    private RandomAccessFile openFile(String fileName) {
	RandomAccessFile randomAccessFile = null;
	try {
	    randomAccessFile = new RandomAccessFile(fileName, "rw");
	}
	catch (IOException e) {
	    LOGGER.error(e.getMessage());
	}
	return randomAccessFile;
    }

    private void openResources() {
	synchronized (lock) {
	    try {
		tmpRandomAccessFile = openFile(tmpFileName);
		targetRandomAccessFile = openFile(targetFileName);
		downloadedBytes = tmpRandomAccessFile.readInt();
		targetRandomAccessFile.seek(downloadedBytes);
		bufferedInputStream = openConnection();
	    }
	    catch (IOException e) {
		LOGGER.warn(e.getMessage());
	    }
	}
    }

    private void storeProgress() {
	synchronized (lock) {
	    LOGGER.info(
		    "Progress of downloading task with ID: '{}' will be stored in temporary file, with name: '{}'.",
		    ID, tmpFileName);
	    try {
		LOGGER.info("Store current position: '{}'.", downloadedBytes);
		openFile(tmpFileName);
		tmpRandomAccessFile.writeInt(downloadedBytes);
	    }
	    catch (IOException e) {
		LOGGER.error(e.getMessage());
	    }
	}
    }

    protected boolean setStatus(Status status) {
	boolean statusInLegalState = false;
	if ((this.status == Status.CREATED) && ((status == Status.DOWNLOADING) || (status == Status.CANCELLED))) {
	    this.status = status;
	    statusInLegalState = true;
	}
	else if ((this.status == Status.DOWNLOADING)
		&& ((status == Status.CANCELLED) || (status == Status.PAUSED) || (status == Status.COMPLETED) || (status == Status.ERROR))) {
	    this.status = status;
	    statusInLegalState = true;
	}
	else if ((this.status == Status.PAUSED) && ((status == Status.CANCELLED) || (status == Status.DOWNLOADING))) {
	    this.status = status;
	    statusInLegalState = true;
	}

	return statusInLegalState;
    }

    @Override
    public synchronized void cancel() {
	if (status != Status.CANCELLED) {
	    LOGGER.info("Cancelling download task with ID: '{}'.", ID);
	    makePause = true;
	    synchronized (lock) {
		if (setStatus(Status.CANCELLED)) {
		    LOGGER.info("Download task with ID: '{}', will be canceled.", ID);
		    LOGGER.info("Status of download task was changed to: '{}'.", status);
		    closeResources();
		    threadPool.remove(this);
		    LOGGER.info("Targer file: '{}' will be deleted.", targetFileName);
		    deleteFile(targetFile);
		    makePause = false;
		}
		else {
		    LOGGER.warn(
			    "You can not change status from '{}', to '{}', of this download task right now, try one more time later.",
			    status, Status.CANCELLED);
		}
	    }
	}
	else {
	    LOGGER.warn("This download task was already in '{}' state.", Status.CANCELLED);
	}
    }

    @Override
    public void download(ThreadPoolExecutor threadPool) {
	if (status != Status.DOWNLOADING) {
	    synchronized (lock) {
		this.threadPool = threadPool;
		if (setStatus(Status.DOWNLOADING)) {
		    LOGGER.info("Status of download task was changed to: '{}'.", status);
		    LOGGER.info("Downloading process will be started.");
		    threadPool.execute(this);
		}
		else {
		    LOGGER.warn(
			    "You can not change status from '{}', to '{}', of this download task right now, try one more time later.",
			    status, Status.DOWNLOADING);
		}
	    }
	}
	else {
	    LOGGER.warn("This download task was already in '{}' state.", Status.DOWNLOADING);
	}
    }

    @Override
    public String getFileName() {
	return targetFileName;
    }

    @Override
    public String getId() {
	return ID;
    }

    @Override
    public String getProgress() {
	if (remainderContentLength == 0) {
	    return "0";
	}
	else {
	    int downloadedPercents = Math.round((downloadedBytes / ((float) totalContentLength / 100)));
	    return String.valueOf(downloadedPercents);
	}
    }

    @Override
    public Status getStatus() {
	return status;
    }

    @Override
    public String getUrl() {
	return url;
    }

    @Override
    public boolean isComplete() {
	if (status == Status.COMPLETED) {
	    return true;
	}
	return false;
    }

    @Override
    public boolean isPaused() {
	if (status == Status.PAUSED) {
	    return true;
	}
	return false;
    }

    @Override
    public void pause() {
	if (status != Status.PAUSED) {
	    makePause = true;
	    LOGGER.info("Pausing download task with ID: '{}'.", ID);
	    synchronized (lock) {
		if (setStatus(Status.PAUSED)) {
		    LOGGER.info("Status of download task was changed to: '{}'.", status);
		    storeProgress();
		    closeResources();
		}
		else {
		    LOGGER.warn(
			    "You can not change status from '{}', to '{}', of this download task right now, try one more time later.",
			    status, Status.PAUSED);
		}
	    }
	}
	else {
	    LOGGER.warn("This download task was already in '{}' state.", Status.PAUSED);
	}
    }

    @Override
    public void resume() {
	if (status != Status.DOWNLOADING) {
	    synchronized (lock) {
		if (setStatus(Status.DOWNLOADING)) {
		    LOGGER.info("Status of download task was changed to: '{}'.", status);
		    openResources();
		    makePause = false;
		}
		else {
		    LOGGER.warn(
			    "You can not change status from '{}', to '{}', of this download task right now, try one more time later.",
			    status, Status.PAUSED);
		}
	    }
	}
	else {
	    LOGGER.warn("This download task was already in '{}' state.", Status.DOWNLOADING);
	}
    }

    @Override
    public void run() {
	LOGGER.info("Downloading process of task: '{}' started.", ID);
	synchronized (lock) {
	    if ((bufferedInputStream = openConnection()) == null) {
		throw new RuntimeException("Could not open connection with given resource " + url);
	    }
	}
	try {
	    boolean endOfStreamWasReached = false;
	    byte[] buffer = new byte[BUFFER_SIZE];
	    while (((downloadedBytes < remainderContentLength) || !endOfStreamWasReached)) {
		while (makePause) {
		}
		synchronized (lock) {
		    if (Thread.currentThread().isInterrupted() || (status == Status.CANCELLED)) {
			LOGGER.info("Downloading process of task: '{}' was interrupted.", ID);
			break;
		    }
		    int numberOfReadBytes = bufferedInputStream.read(buffer, 0, BUFFER_SIZE);
		    if (numberOfReadBytes == -1) {
			endOfStreamWasReached = true;
			LOGGER.info("Downloaded bytes: {}; Total content size: {}", downloadedBytes,
				remainderContentLength);
			LOGGER.info("End of downloading resource was riched, seems that the resouces was successfully downloaded.");
		    }
		    else {
			LOGGER.trace("New portion of bytes ('{}') will be written to target file: '{}'.",
				numberOfReadBytes, targetFileName);
			targetRandomAccessFile.write(buffer, 0, numberOfReadBytes);
			downloadedBytes += numberOfReadBytes;
		    }
		}
		if ((downloadedBytes >= remainderContentLength) || endOfStreamWasReached) {
		    LOGGER.info("Resouces was successfully downloaded!");
		    endOfStreamWasReached = true;

		    if (setStatus(Status.COMPLETED)) {
			LOGGER.info("Status of download task was changed to: '{}'.", status);
		    }
		    else {
			LOGGER.warn(
				"You can not change status from '{}', to '{}', of this download task right now, try one more time later.",
				status, Status.COMPLETED);
		    }
		}
	    }

	}
	catch (IOException e) {
	    LOGGER.error(e.getMessage());
	}
	finally {
	    synchronized (lock) {
		closeResources();
		deleteFile(tmpFile);
	    }
	}
    }

}
