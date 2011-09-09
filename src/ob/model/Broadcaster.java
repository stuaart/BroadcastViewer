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

	// Identifier for this Broadcaster's <video service> id
	@Id
	private String broadcastId;

	// Identifier for this Broadcaster's Jabber id
	private String jabberId;

	private float latlng[];

	private float orien[];

	@Temporal(TIMESTAMP)
	private Date timestamp;

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

		this.latlng = b.getLatLng();
		this.orien = b.getOrientation();
	}

/*	public Key getKey()
	{
		return key;
	}*/
	
	public void setBroadcastId(final String broadcastId)
	{
		this.broadcastId = broadcastId;
	}

	public String getBroadcastId()
	{
		return broadcastId;
	}

	public void setJabberId(final String jabberId)
	{
		this.jabberId = jabberId;
	}

	public String getJabberId()
	{
		return jabberId;
	}

	public void setLatLng(final float latlng[])
	{
		if (latlng.length != 2)
			return;

		this.latlng = latlng;
	}

	public float[] getLatLng()
	{
		return latlng;
	}

	public void setOrientation(final float orien[])
	{
		if (orien.length != 3)
			return;

		this.orien = orien;
	}

	public float[] getOrientation()
	{
		return orien;
	}

	public void setTimestamp(final Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public Date getTimestamp()
	{
		return timestamp;
	}

}
