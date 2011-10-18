package ob.server;

import ob.client.ConfigService;
import ob.client.Dimension;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.google.gwt.maps.client.geom.LatLng;

public class ConfigServiceImpl extends RemoteServiceServlet 
							   implements ConfigService

{
	public final String getBoundsKML() throws IllegalArgumentException
	{
		return null;
	}

	public final String getBambuserAPIURL() 
		throws IllegalArgumentException
	{
		return null;
	}

	public final LatLng getDefaultMapCentre() 
		throws IllegalArgumentException
	{
		return null;
	}

	public final Integer getDefaultMapZoom() 
		throws IllegalArgumentException
	{
		return null;
	}

	public final Integer getRefreshInterval() 
		throws IllegalArgumentException
	{
		return null;
	}

	public final Boolean isLive() throws IllegalArgumentException
	{
		return null;
	}

	public final Dimension[] getVideoDimensions() 
		throws IllegalArgumentException
	{
		return null;
	}

	public final String[] getMapDimensions() 	
		throws IllegalArgumentException
	{
		return null;
	}
}
