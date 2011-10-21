package ob.model;

import com.google.appengine.api.datastore.Key;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Temporal;
import javax.persistence.Id;


@Entity
public class Broadcaster
{

	//@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
    //private Key key;

	// This is the primary key
	// Identifier for this Broadcaster's <video service> account, e.g., for 
	// Bambuser broadcastId is their username
	@Id
	private String broadcastId;

	// Identifier for this Broadcaster's video broadcast, derived from an 
	// identifier for a particular video present on their <video service> 
	// account
	private String videoId = null;

	// Identifier for this Broadcaster's Jabber id
	private String jabberId;

	private float latlng[];

	private float orien[];

	@Temporal(TIMESTAMP)
	private Date timestamp;

	private int views = 0;

	private String thumbnailURL = null;

	public Broadcaster()
	{
		timestamp = new Date(); // TODO: UTC?
	}

	public Broadcaster(final String broadcastId,
					   final String jabberId,
					   final float latlng[], 
					   final float orien[], 
					   final Date timestamp)
	{
		this();
		this.broadcastId = broadcastId;
		this.jabberId = jabberId;
		this.latlng = latlng;
		this.orien = orien;
		this.timestamp = timestamp;
	}

	public void update(final Broadcaster b)
	{
		if (b.getBroadcastId() != null)
			this.setBroadcastId(b.getBroadcastId());
		if (b.getJabberId() != null)
			this.setJabberId(b.getJabberId());
		if (b.getTimestamp() != null)
			this.timestamp = b.getTimestamp();

		setLatLng(b.getLatLng());
		setOrientation(b.getOrientation());

		this.views = b.getViews();
		if (b.getVideoId() != null)
			this.videoId = b.getVideoId();

		if (b.getThumbnailURL() != null)
			this.thumbnailURL = b.getThumbnailURL();
	}

	public void update(final ob.client.model.Broadcaster b)
	{
		if (b.getBroadcastId() != null)
			this.setBroadcastId(b.getBroadcastId());
		if (b.getJabberId() != null)
			this.setJabberId(b.getJabberId());
		if (b.getTimestamp() != null)
			this.timestamp = b.getTimestamp();

		setLatLng(b.getLatLng());
		setOrientation(b.getOrientation());

		this.views = b.getViews();
		if (b.getVideoId() != null)
			this.videoId = b.getVideoId();

		if (b.getThumbnailURL() != null)
			this.thumbnailURL = b.getThumbnailURL();

	}

/*	public Key getKey()
	{
		return key;
	}*/
	
	public void setBroadcastId(final String broadcastId)
	{
		this.broadcastId = broadcastId;
	}

	public final String getBroadcastId()
	{
		return broadcastId;
	}
	
	public void setVideoId(final String videoId)
	{
		this.videoId = videoId;
	}

	public final String getVideoId()
	{
		return videoId;
	}

	public void setJabberId(final String jabberId)
	{
		this.jabberId = jabberId;
	}

	public final String getJabberId()
	{
		return jabberId;
	}

	public void setLatLng(final float latlng[])
	{
		if (latlng.length != 2)
			return;

		this.latlng = latlng;

		System.out.println("setLatLng()=" + this.latlng[0]);
	}

	public final float[] getLatLng()
	{
		return latlng;
	}

	public void setOrientation(final float orien[])
	{
		if (orien.length != 3)
			return;

		this.orien = orien;
	}

	public final float[] getOrientation()
	{
		return orien;
	}

	public void setTimestamp(final Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public final Date getTimestamp()
	{
		return timestamp;
	}
	
	public void setViews(final int views)
	{
		this.views = views;
	}

	public final int getViews()
	{
		return views;
	}

	public final String getThumbnailURL()
	{
		return this.thumbnailURL;
	}

	public void setThumbnailURL(final String thumbnailURL)
	{
		this.thumbnailURL = thumbnailURL;
	}

}
