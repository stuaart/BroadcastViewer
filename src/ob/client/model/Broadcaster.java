package ob.client.model;

import java.util.Date;
import com.google.gwt.user.client.rpc.IsSerializable;

public class Broadcaster implements IsSerializable
{

	private long key;

	private String broadcastId;	

	private float[] latlng;

	private float[] orien;

	private Date timestamp;

	public Broadcaster() {};

	public Broadcaster(final long key, 
					   final String broadcastId,
					   final float[] latlng, 
					   final float[] orien, 
					   final Date timestamp)
	{
		this.key = key;
		this.broadcastId = broadcastId;
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
