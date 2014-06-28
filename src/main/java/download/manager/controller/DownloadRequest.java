package download.manager.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class DownloadRequest {

    private Map<String, String> urlAndFileNames = new HashMap<>();

    public DownloadRequest() {
    }

    @JsonGetter(value = "urlAndFileNames")
    public Map<String, String> get() {
	return urlAndFileNames;
    }

    public int size() {
	return urlAndFileNames.size();
    }

    @JsonSetter
    public void setUrlAndFileNames(Map<String, String> urlAndFileNames) {
	this.urlAndFileNames = urlAndFileNames;
    }

    public void setUrlAndFileNames(String url, String fileName) {
	urlAndFileNames.put(url, fileName);
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	Iterator<Entry<String, String>> iter = urlAndFileNames.entrySet().iterator();
	while (iter.hasNext()) {
	    Entry<String, String> entry = iter.next();
	    sb.append(entry.getKey());
	    sb.append('=').append('"');
	    sb.append(entry.getValue());
	    sb.append('"');
	    if (iter.hasNext()) {
		sb.append(',').append(' ');
	    }
	}
	return sb.toString();
    }

}
