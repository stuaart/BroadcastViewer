package ob.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import com.google.gwt.maps.client.geom.LatLng;

@RemoteServiceRelativePath("config")
public interface ConfigService extends RemoteService
{
	String getBoundsKML() throws IllegalArgumentException;
	String getBambuserAPIURL() throws IllegalArgumentException;
	LatLng getDefaultMapCentre() throws IllegalArgumentException;
	Integer getDefaultMapZoom() throws IllegalArgumentException;
	Integer getRefreshInterval() throws IllegalArgumentException;
	Boolean isLive() throws IllegalArgumentException;
	Dimension[] getVideoDimensions() throws IllegalArgumentException;
	String[] getMapDimensions() throws IllegalArgumentException;
}
