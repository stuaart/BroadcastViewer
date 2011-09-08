package ob.client.model;

import java.util.Date;
import com.google.gwt.user.client.rpc.IsSerializable;

public class Broadcaster implements IsSerializable
{

	private long key;

	// Identifier for this Broadcaster's <video service> id
	private String broadcastId;

	// Identifier for this Broadcaster's Jabber id
	private String jabberId;

	private float[] latlng;

	private float[] orien;

	private Date timestamp;

	public Broadcaster() {};

	public Broadcaster(final long key, 
					   final String broadcastId,
					   final String jabberId,
					   final float[] latlng, 
					   final float[] orien, 
					   final Date timestamp)
	{
		this.key = key;
		this.broadcastId = broadcastId;
		this.jabberId = jabberId;
		this.latlng = latlng;
		this.orien = orien;
		this.timestamp = timestamp;
	}

	public long getKey()
	{
		return key;
	}

	public String getBroadcastId()
	{
		return broadcastId;
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

}
