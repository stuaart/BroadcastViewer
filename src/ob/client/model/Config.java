package ob.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Config implements IsSerializable
{
	private String boundsKML;
	private String bambuserAPIURL;
	private String bambuserOembedURL;	
	private double[] defaultMapCentre;
	private int defaultMapZoom;
	private int refreshInterval;
	private boolean isLive;
	private Dimension[] videoDimensions;
	private String[] mapDimensions;
	private boolean isServerPoll;
	private boolean isGWTGrid;
	private String gridName;

	public Config() {};

	public Config(final String boundsKML, final String bambuserAPIURL, 
				  final String bambuserOembedURL,
				  final double[] defaultMapCentre, final int defaultMapZoom,
				  final int refreshInterval, final boolean isLive,
				  final Dimension[] videoDimensions, 
				  final String[] mapDimensions, final boolean isServerPoll,
				  final boolean isGWTGrid, final String gridName)
	{
		this.boundsKML = boundsKML;
		this.bambuserOembedURL = bambuserOembedURL;
		this.bambuserAPIURL = bambuserAPIURL;
		this.defaultMapCentre = defaultMapCentre;
		this.defaultMapZoom = defaultMapZoom;
		this.refreshInterval = refreshInterval;
		this.isLive = isLive;
		this.videoDimensions = videoDimensions;
		this.mapDimensions = mapDimensions;
		this.isServerPoll = isServerPoll;
		this.isGWTGrid = isGWTGrid;
		this.gridName = gridName;
	}

	public final String getBoundsKML()
	{
		return boundsKML;
	}

	public final String getBambuserOembedURL()
	{
		return bambuserOembedURL;
	}

	public final String getBambuserAPIURL() 
	{
		return bambuserAPIURL;
	}

	public final double[] getDefaultMapCentre() 
	{
		return defaultMapCentre;
	}

	public final int getDefaultMapZoom() 
	{
		return defaultMapZoom;
	}

	public final int getRefreshInterval() 
	{
		return refreshInterval;
	}

	public final boolean isLive()
	{
		return isLive;
	}

	public final Dimension[] getVideoDimensions() 
	{
		return videoDimensions;
	}

	public final String[] getMapDimensions() 	
	{
		return mapDimensions;
	}

	public final boolean isServerPoll()
	{
		return isServerPoll;
	}

	public final boolean isGWTGrid()
	{
		return isGWTGrid;
	}

	public final String getGridName()
	{
		return gridName;
	}

}
