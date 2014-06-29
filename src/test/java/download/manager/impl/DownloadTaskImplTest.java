package download.manager.impl;

import java.io.BufferedInputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DownloadTaskImpl.class })
public class DownloadTaskImplTest {

    @Mock
    private ThreadPoolExecutor threadPoolExecutor;

    @Mock
    Lock lock = new ReentrantLock();

    @Mock
    Status status = Status.CREATED;

    @Mock
    BufferedInputStream bufferedInputStream;

    @Mock(name = "targetRandomAccessFile")
    RandomAccessFile targetRandomAccessFile;

    @Mock(name = "tmpRandomAccessFile")
    RandomAccessFile tmpRandomAccessFile;

    @InjectMocks
    private DownloadTaskImpl downloadTask = Mockito.mock(DownloadTaskImpl.class);

    @Before
    public void before() {
	Mockito.reset(downloadTask);
    }

    @Test
    public void testgetStatus() throws Exception {
	PowerMockito.whenNew(DownloadTaskImpl.class).withArguments(Matchers.anyString(), Matchers.anyString())
		.thenReturn(downloadTask);
	downloadTask = new DownloadTaskImpl("test", "test");
	PowerMockito.when(downloadTask, "download", Matchers.anyObject()).thenCallRealMethod();
	PowerMockito.when(downloadTask, "setStatus", Matchers.any(Status.class)).thenCallRealMethod();
	PowerMockito.when(downloadTask.getStatus()).thenCallRealMethod();
	PowerMockito.doNothing().when(threadPoolExecutor, "execute", Matchers.anyObject());
	downloadTask.download(threadPoolExecutor);

	PowerMockito.verifyPrivate(downloadTask).invoke("setStatus", Status.DOWNLOADING);
	MatcherAssert.assertThat(downloadTask.getStatus(), org.hamcrest.Matchers.equalTo(Status.DOWNLOADING));

    }

}
