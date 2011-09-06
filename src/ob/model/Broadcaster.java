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

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key key;

	private float latlng[];

	private float orien[];

	@Temporal(TIMESTAMP)
	private Date timestamp;

	public Broadcaster()
	{
		timestamp = new Date(); // TODO: UTC?
	}

	public Broadcaster(final float latlng[], final float orien[], 
					   final Date timestamp)
	{
		this();
		this.latlng = latlng;
		this.orien = orien;
		this.timestamp = timestamp;
	}

	public void setKey(final Key key)
	{
		this.key = key;
	}

	public Key getKey()
	{
		return key;
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
