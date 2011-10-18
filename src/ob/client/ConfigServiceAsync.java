package ob.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.google.gwt.maps.client.geom.LatLng;

public interface ConfigServiceAsync
{
	void getBoundsKML(AsyncCallback<String> callback);
	void getBambuserAPIURL(AsyncCallback<String> callback);
	void getDefaultMapCentre(AsyncCallback<LatLng> callback);
	void getDefaultMapZoom(AsyncCallback<Integer> callback);
	void getRefreshInterval(AsyncCallback<Integer> callback);
	void isLive(AsyncCallback<Boolean> callback);
	void getVideoDimensions(AsyncCallback<Dimension[]> callback);
	void getMapDimensions(AsyncCallback<String[]> callback);
}
