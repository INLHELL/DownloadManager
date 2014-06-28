package download.manager.controller;

import download.manager.Status;
import download.manager.api.DownloadTask;

public class DownloadResponse {
    private String id;
    private String url;
    private Status status;
    private String progress;
    private String fileName;

    public DownloadResponse(DownloadTask downloadTask) {
	id = downloadTask.getId();
	url = downloadTask.getUrl();
	status = downloadTask.getStatus();
	progress = downloadTask.getProgress();
	fileName = downloadTask.getFileName();
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    public Status getStatus() {
	return status;
    }

    public void setStatus(Status status) {
	this.status = status;
    }

    public String getProgress() {
	return progress;
    }

    public void setProgress(String progress) {
	this.progress = progress;
    }

    public String getFileName() {
	return fileName;
    }

    public void setFileName(String fileName) {
	this.fileName = fileName;
    }

}
