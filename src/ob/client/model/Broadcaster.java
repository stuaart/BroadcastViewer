package ob.client.model;

import java.util.Date;
import com.google.gwt.user.client.rpc.IsSerializable;

public class Broadcaster implements IsSerializable
{
	// This is the primary key
	// Identifier for this Broadcaster's <video service> account, e.g., for 
	// Bambuser broadcastId is their username
	private String broadcastId;

	// Identifier for this Broadcaster's video broadcast, derived from an 
	// identifier for a particular video present on their <video service> 
	// account
	private String videoId = null;

	// Identifier for this Broadcaster's Jabber id
	private String jabberId;

	private float[] latlng;

	private float[] orien;

	private Date timestamp;

	private int views = 0;

	private String thumbnailURL = null;


	public Broadcaster() {};

	public Broadcaster(final String broadcastId,
					   final String jabberId,
					   final float[] latlng, 
					   final float[] orien, 
					   final Date timestamp)
	{
		this.broadcastId = broadcastId;
		this.jabberId = jabberId;
		this.latlng = latlng;
		this.orien = orien;
		this.timestamp = timestamp;
	}

	public String getBroadcastId()
	{
		return broadcastId;
	}

	public void setVideoId(final String videoId)
	{
		this.videoId = videoId;
	}

	public String getVideoId()
	{
		return videoId;
	}

	public String getJabberId()
	{
		return jabberId;
	}

	public float[] getLatLng()
	{
		return latlng;
	}

	public float[] getOrientation()
	{
		return orien;
	}

	public Date getTimestamp()
	{
		return timestamp;
	}

	public int getViews()
	{
		return views;
	}

	public void setViews(final int views)
	{
		this.views = views;
	}

	public String getThumbnailURL()
	{
		return this.thumbnailURL;
	}

	public void setThumbnailURL(final String thumbnailURL)
	{
		this.thumbnailURL = thumbnailURL;
	}

}
