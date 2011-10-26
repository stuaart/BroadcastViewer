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
	private Dimension[] videoDimensionsLimits;
	private String[] mapDimensions;
	private boolean isServerPoll;
	private boolean isGWTGrid;
	private String gridName;
	private boolean gridFiller;
	private Dimension broadcastWindowDimension;
	private boolean isOverlayVisible;
	private int maxGrid;

	public Config() {};

	public Config(final String boundsKML, final String bambuserAPIURL, 
				  final String bambuserOembedURL,
				  final double[] defaultMapCentre, final int defaultMapZoom,
				  final int refreshInterval, final boolean isLive,
				  final Dimension[] videoDimensions, 
				  final String[] mapDimensions, final boolean isServerPoll,
				  final boolean isGWTGrid, final String gridName,
				  final boolean gridFiller, 
				  final Dimension broadcastWindowDimension,
				  final boolean isOverlayVisible, final int maxGrid)
	{
		this.boundsKML = boundsKML;
		this.bambuserOembedURL = bambuserOembedURL;
		this.bambuserAPIURL = bambuserAPIURL;
		this.defaultMapCentre = defaultMapCentre;
		this.defaultMapZoom = defaultMapZoom;
		this.refreshInterval = refreshInterval;
		this.isLive = isLive;
		this.videoDimensionsLimits = videoDimensionsLimits;
		this.mapDimensions = mapDimensions;
		this.isServerPoll = isServerPoll;
		this.isGWTGrid = isGWTGrid;
		this.gridName = gridName;
		this.gridFiller = gridFiller;
		this.broadcastWindowDimension = broadcastWindowDimension;
		this.isOverlayVisible = isOverlayVisible;
		this.maxGrid = maxGrid;
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

	public final Dimension[] getVideoDimensionsLimits() 
	{
		return videoDimensionsLimits;
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

	public final boolean gridFiller()
	{
		return gridFiller;
	}

	public final Dimension getBroadcastWindowDimension() 
	{
		return broadcastWindowDimension;
	}

	public final boolean isOverlayVisible()
	{
		return isOverlayVisible;
	}

	public final int getMaxGrid() 
	{
		return maxGrid;
	}

}
