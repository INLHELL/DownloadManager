package download.manager.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import download.manager.DownloadManagerImpl;
import download.manager.api.DownloadManager;

@Path("/dm")
public class DownloadManagerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadManagerController.class);

    private final DownloadManager DOWNLOAD_MANAGER = DownloadManagerImpl.getInstance();

    @GET
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDownloadTask(@QueryParam("url") String url, @QueryParam("filename") String fileName) {
	LOGGER.info("Passed URL: '{}', passed file name: '{}'.", url, fileName);
	String id = DOWNLOAD_MANAGER.add(url, fileName);
	DownloadResponse downloadResponse = new DownloadResponse(DOWNLOAD_MANAGER.get(id));
	return Response.status(200).entity(downloadResponse).build();
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addDownloadTask(DownloadRequest downloadRequest) {
	// ObjectMapper objectMapper = new ObjectMapper();
	// DownloadRequest downloadRequest2 = new DownloadRequest();
	// Map<String, String> a = new HashMap<String, String>();
	// a.put("1", "a");
	// a.put("2", "b");
	// downloadRequest2.setUrlAndFileNames(a);
	// try {
	// System.out.println(objectMapper.writeValueAsString(downloadRequest2));
	// }
	// catch (JsonProcessingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	LOGGER.info("Download reuqest was passed: '{}'.", downloadRequest);
	List<DownloadResponse> downloadResponses = new ArrayList<>(downloadRequest.size());
	for (Entry<String, String> urlAndFileName : downloadRequest.get().entrySet()) {
	    LOGGER.info("New download task will be add with URL: '{}', and file name: '{}'.", urlAndFileName.getKey(),
		    urlAndFileName.getValue());
	    String id = DOWNLOAD_MANAGER.add(urlAndFileName.getKey(), urlAndFileName.getValue());
	    DownloadResponse downloadResponse = new DownloadResponse(DOWNLOAD_MANAGER.get(id));
	    downloadResponses.add(downloadResponse);
	}
	// JSONObject.fromObject(downloadResponses.toString()));
	return Response.status(200).entity(downloadResponses).build();
    }

    @GET
    @Path("/addload")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAndDownload(@QueryParam("url") String url, @QueryParam("filename") String fileName) {
	LOGGER.info("Passed URL: '{}', passed file name: '{}'.", url, fileName);
	String id = DOWNLOAD_MANAGER.addAndDownload(url, fileName);
	DownloadResponse downloadResponse = new DownloadResponse(DOWNLOAD_MANAGER.get(id));
	return Response.status(200).entity(downloadResponse).build();
    }

    @GET
    @Path("/download")
    @Produces(MediaType.APPLICATION_JSON)
    public Response downloadDownloadTask(@QueryParam("id") String id) {
	LOGGER.info("Download task with given ID: '{}', will be downloaded.", id);
	DOWNLOAD_MANAGER.download(id);
	DownloadResponse downloadResponse = new DownloadResponse(DOWNLOAD_MANAGER.get(id));
	return Response.status(200).entity(downloadResponse).build();
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response statusOfDownloadTask(@QueryParam("id") String id) {
	LOGGER.info("Status of download task with given ID: '{}', will returned.", id);
	DownloadResponse downloadResponse = new DownloadResponse(DOWNLOAD_MANAGER.get(id));
	return Response.status(200).entity(downloadResponse).build();
    }

    @GET
    @Path("/cancel")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelDownloadTask(@QueryParam("id") String id) {
	LOGGER.info("Download task with given ID: '{}', will be cancled.", id);
	DOWNLOAD_MANAGER.cancel(id);
	DownloadResponse downloadResponse = new DownloadResponse(DOWNLOAD_MANAGER.get(id));
	return Response.status(200).entity(downloadResponse).build();
    }

    @GET
    @Path("/pause")
    @Produces(MediaType.APPLICATION_JSON)
    public Response pauseDownloadTask(@QueryParam("id") String id) {
	LOGGER.info("Download task with given ID: '{}', will be paused.", id);
	DOWNLOAD_MANAGER.pause(id);
	DownloadResponse downloadResponse = new DownloadResponse(DOWNLOAD_MANAGER.get(id));
	return Response.status(200).entity(downloadResponse).build();
    }

    @GET
    @Path("/resume")
    @Produces(MediaType.APPLICATION_JSON)
    public Response resumeDownloadTask(@QueryParam("id") String id) {
	LOGGER.info("Download task with given ID: '{}', will be resumed.", id);
	DOWNLOAD_MANAGER.resume(id);
	DownloadResponse downloadResponse = new DownloadResponse(DOWNLOAD_MANAGER.get(id));
	return Response.status(200).entity(downloadResponse).build();
    }
}
