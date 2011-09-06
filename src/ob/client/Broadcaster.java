package ob.client;

import java.util.Date;
import com.google.gwt.user.client.rpc.IsSerializable;

public class Broadcaster implements IsSerializable
{

	private float[] latlng;

	private float[] orien;

	private Date timestamp;

	public Broadcaster() {};

	public Broadcaster(final float[] latlng, final float[] orien, 
					   final Date timestamp)
	{
		this.latlng = latlng;
		this.orien = orien;
		this.timestamp = timestamp;
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
