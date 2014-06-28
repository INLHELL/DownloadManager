package download.manager.impl;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import download.manager.impl.DownloadTaskImpl;
import download.manager.impl.Status;

public class DownloadTaskImplTest {

    @Test
    public void testgetStatus() {
	DownloadTaskImpl mockedDownloadTask = mock(DownloadTaskImpl.class);
	when(mockedDownloadTask.getStatus()).thenReturn(Status.CREATED);
    }

    // @Test
    // public void testAdd() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testAddAndDownload() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testCancel() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testDownload() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testForceShutdown() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testGet() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testPause() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testResume() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testSetPoolSize() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testShutdown() {
    // fail("Not yet implemented");
    // }

}
