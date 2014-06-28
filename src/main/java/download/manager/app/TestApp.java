package download.manager.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import download.manager.api.DownloadManager;
import download.manager.impl.DownloadManagerImpl;

public class TestApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestApp.class);

    public static void main(String[] args) throws InterruptedException {
	LOGGER.info("Start application.");
	DownloadManager downloadManager = DownloadManagerImpl.getInstance();
	String id = downloadManager
		.addAndDownload("http://www.dopdf.com/download/pdf/dopdf-eula.pdf", "dopdf-eula.pdf");

	Thread.sleep(600);
	downloadManager.pause(id);
	downloadManager.resume(id);
	Thread.sleep(50);
	//
	// // downloadManager.cancel(id);
	// Thread.sleep(100);
	// downloadManager.pause(id);
	// downloadManager.resume(id);
	// Thread.sleep(100);
	// downloadManager.pause(id);
	// downloadManager.resume(id);
	// Thread.sleep(100);
	// downloadManager.pause(id);
	// downloadManager.resume(id);

	downloadManager.shutdown();
    }

}
