package download.manager.integ;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.junit.AfterClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.madgag.hamcrest.FileExistenceMatcher;

import download.manager.api.DownloadManager;
import download.manager.impl.DownloadManagerImpl;
import download.manager.impl.Status;

public class DownloadManagerConcurrentTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadManagerConcurrentTest.class);

    private DownloadManager downloadManager = DownloadManagerImpl.getInstance();
    private static String TARGET_FILE_NAME = "dopdf-eula";
    private static String FILE_EXTENSION = ".pdf";
    private String URL = "http://www.dopdf.com/download/pdf/dopdf-eula.pdf";

    CountDownLatch addLatch = new CountDownLatch(1);
    CountDownLatch downloadLatch = new CountDownLatch(1);
    CountDownLatch resultLatch = new CountDownLatch(3);

    @Test
    public void testDownloadAndPauseAndCancel() throws InterruptedException, IOException {
	ExecutorService executorService = Executors.newCachedThreadPool();
	submitDownloadtasks(executorService, 0, 10);
	submitDownloadtasks(executorService, 10, 20);
	submitDownloadtasks(executorService, 20, 30);

	addLatch.countDown();
	downloadLatch.countDown();
	resultLatch.await(2, TimeUnit.MINUTES);

	for (int i = 0; i < 30; i++) {
	    MatcherAssert.assertThat(new File(TARGET_FILE_NAME + "_" + i + FILE_EXTENSION),
		    FileExistenceMatcher.exists());

	}

    }

    private void submitDownloadtasks(ExecutorService executorService, final int start, final int end) {
	executorService.execute(new Runnable() {

	    @Override
	    public void run() {
		Set<String> ids = new HashSet<>();
		try {
		    addLatch.await();
		    for (int i = start; i < end; i++) {
			ids.add(downloadManager.add(URL, TARGET_FILE_NAME + "_" + i + FILE_EXTENSION));
		    }
		    downloadLatch.await();
		    for (String id : ids) {
			downloadManager.download(id);
		    }
		    boolean allHaveBeenDownloaded = false;
		    while (!allHaveBeenDownloaded) {
			allHaveBeenDownloaded = true;
			for (String id : ids) {
			    if (downloadManager.get(id).getStatus() != Status.COMPLETED) {
				allHaveBeenDownloaded = false;
			    }
			}
		    }
		    resultLatch.countDown();
		}
		catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    @AfterClass
    public static void afterClass() {
	for (int i = 0; i < 30; i++) {
	    deleteFile(TARGET_FILE_NAME + "_" + i + FILE_EXTENSION);
	}
    }

    private static void deleteFile(String fileName) {
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
}
