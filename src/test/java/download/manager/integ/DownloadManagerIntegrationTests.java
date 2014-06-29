package download.manager.integ;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.madgag.hamcrest.FileExistenceMatcher;

import download.manager.api.DownloadManager;
import download.manager.impl.DownloadManagerImpl;
import download.manager.impl.Status;

public class DownloadManagerIntegrationTests {

    private static final int MAX_WAIT_TIMEOUT = 3000;

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadManagerIntegrationTests.class);

    private String targetFileName = "dopdf-eula.pdf";
    private String tmpFileName;
    private String url = "http://www.dopdf.com/download/pdf/dopdf-eula.pdf";
    private DownloadManager downloadManager = DownloadManagerImpl.getInstance();

    @After
    public void after() {
	deleteFile(targetFileName);
	deleteFile(tmpFileName);
    }

    @Test
    public void testDownload() throws InterruptedException, IOException {
	String id = downloadManager.addAndDownload(url, targetFileName);
	tmpFileName = "dopdf-eula.pdf." + id + ".tmp";

	while (downloadManager.get(id).getStatus() != Status.DOWNLOADING) {
	}
	waitForDownloadCompletion(id);
	MatcherAssert.assertThat(downloadManager.get(id).getStatus(), Matchers.equalTo(Status.COMPLETED));
	MatcherAssert.assertThat(new File(targetFileName), FileExistenceMatcher.exists());
	MatcherAssert.assertThat(getTargetFileSize(), Matchers.equalTo(getContetLength()));
	MatcherAssert.assertThat(new File(tmpFileName), FileExistenceMatcher.doesNotExist());
    }

    @Test
    public void testCancel() throws InterruptedException, IOException {
	String id = downloadManager.add(url, targetFileName);
	tmpFileName = "dopdf-eula.pdf." + id + ".tmp";

	downloadManager.cancel(id);
	MatcherAssert.assertThat(downloadManager.get(id).getStatus(), Matchers.equalTo(Status.CANCELLED));
	MatcherAssert.assertThat(new File(targetFileName), FileExistenceMatcher.doesNotExist());
	MatcherAssert.assertThat(new File(tmpFileName), FileExistenceMatcher.doesNotExist());
    }

    @Test
    public void testDownloadAndPauseAndResume() throws InterruptedException, IOException {
	String id = downloadManager.addAndDownload(url, targetFileName);
	tmpFileName = "dopdf-eula.pdf." + id + ".tmp";

	while (downloadManager.get(id).getStatus() != Status.DOWNLOADING) {
	}
	downloadManager.pause(id);
	MatcherAssert.assertThat(downloadManager.get(id).getStatus(), Matchers.equalTo(Status.PAUSED));
	downloadManager.resume(id);
	waitForDownloadCompletion(id);
	MatcherAssert.assertThat(downloadManager.get(id).getStatus(), Matchers.equalTo(Status.COMPLETED));
	MatcherAssert.assertThat(new File(targetFileName), FileExistenceMatcher.exists());
	MatcherAssert.assertThat(getTargetFileSize(), Matchers.equalTo(getContetLength()));
    }

    @Test
    public void testDownloadAndPause() throws InterruptedException, IOException {
	String id = downloadManager.addAndDownload(url, targetFileName);
	tmpFileName = "dopdf-eula.pdf." + id + ".tmp";

	while (downloadManager.get(id).getStatus() != Status.DOWNLOADING) {
	}
	downloadManager.pause(id);
	MatcherAssert.assertThat(downloadManager.get(id).getStatus(), Matchers.equalTo(Status.PAUSED));

	MatcherAssert.assertThat(new File(targetFileName), FileExistenceMatcher.exists());
	MatcherAssert.assertThat(new File(tmpFileName), FileExistenceMatcher.exists());
	MatcherAssert.assertThat(getTargetFileSize(),
		Matchers.equalTo(Long.valueOf(downloadManager.get(id).getProgress())));
    }

    @Test
    public void testDownloadAndPauseAndCancel() throws InterruptedException, IOException {
	String id = downloadManager.addAndDownload(url, targetFileName);
	tmpFileName = "dopdf-eula.pdf." + id + ".tmp";

	while (downloadManager.get(id).getStatus() != Status.DOWNLOADING) {
	}
	downloadManager.pause(id);
	MatcherAssert.assertThat(downloadManager.get(id).getStatus(), Matchers.equalTo(Status.PAUSED));

	downloadManager.cancel(id);
	MatcherAssert.assertThat(downloadManager.get(id).getStatus(), Matchers.equalTo(Status.CANCELLED));

    }

    @Test
    public void testDownloadAndPauseAndResumeAndCancel() throws InterruptedException, IOException {
	String id = downloadManager.addAndDownload(url, targetFileName);
	tmpFileName = "dopdf-eula.pdf." + id + ".tmp";

	while (downloadManager.get(id).getStatus() != Status.DOWNLOADING) {
	}
	downloadManager.pause(id);
	MatcherAssert.assertThat(downloadManager.get(id).getStatus(), Matchers.equalTo(Status.PAUSED));

	downloadManager.resume(id);
	MatcherAssert.assertThat(downloadManager.get(id).getStatus(), Matchers.equalTo(Status.DOWNLOADING));

	downloadManager.cancel(id);
	MatcherAssert.assertThat(downloadManager.get(id).getStatus(), Matchers.equalTo(Status.CANCELLED));

	MatcherAssert.assertThat(new File(targetFileName), FileExistenceMatcher.doesNotExist());
    }

    @Test
    public void testDownloadAndResume() throws InterruptedException, IOException {
	String id = downloadManager.addAndDownload(url, targetFileName);
	tmpFileName = "dopdf-eula.pdf." + id + ".tmp";

	while (downloadManager.get(id).getStatus() != Status.DOWNLOADING) {
	}
	downloadManager.resume(id);
	MatcherAssert.assertThat(downloadManager.get(id).getStatus(), Matchers.equalTo(Status.DOWNLOADING));

	MatcherAssert.assertThat(new File(targetFileName), FileExistenceMatcher.exists());
	MatcherAssert.assertThat(getTargetFileSize(),
		Matchers.equalTo(Long.valueOf(downloadManager.get(id).getProgress())));
    }

    @Test
    public void testDownloadAndCancel() throws InterruptedException, IOException {
	String id = downloadManager.addAndDownload(url, targetFileName);
	tmpFileName = "dopdf-eula.pdf." + id + ".tmp";

	while (downloadManager.get(id).getStatus() != Status.DOWNLOADING) {
	}
	downloadManager.cancel(id);
	MatcherAssert.assertThat(downloadManager.get(id).getStatus(), Matchers.equalTo(Status.CANCELLED));
	MatcherAssert.assertThat(new File(targetFileName), FileExistenceMatcher.doesNotExist());
	MatcherAssert.assertThat(new File(tmpFileName), FileExistenceMatcher.doesNotExist());
    }

    private void waitForDownloadCompletion(String id) throws InterruptedException {
	int waitTimeout = 0;
	while ((downloadManager.get(id).getStatus() != Status.COMPLETED) && (waitTimeout != MAX_WAIT_TIMEOUT)) {
	    Thread.sleep(100);
	    waitTimeout += 100;
	}
	if (waitTimeout == MAX_WAIT_TIMEOUT) {
	    Assert.fail("Wait timeout was run out!");
	}
    }

    private void deleteFile(String fileName) {
	File file = new File(fileName);
	if (file.exists()) {
	    if (!file.delete()) {
		LOGGER.warn("File with name: '{}' was not deleted.", file);
		try {
		    FileUtils.forceDelete(file);
		}
		catch (IOException e) {
		}
	    }
	    else {
		LOGGER.info("File with name: '{}' was deleted.", file);
	    }
	}
    }

    private long getContetLength() throws IOException {
	URL targetUrl = new URL(url);
	URLConnection connectionUrl = targetUrl.openConnection();
	return connectionUrl.getContentLength();
    }

    private long getTargetFileSize() throws IOException {
	File targetFile = new File(targetFileName);
	return targetFile.length();
    }

}
