package ob.client.model;

import java.util.Date;
import com.google.gwt.user.client.rpc.IsSerializable;

public class Broadcaster implements IsSerializable
{
	// This is the primary key
	// Identifier for this Broadcaster's <video service> id
	private String broadcastId;

	// Identifier for this Broadcaster's Jabber id
	private String jabberId;

	private float[] latlng;

	private float[] orien;

	private Date timestamp;

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

}
