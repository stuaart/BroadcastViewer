package ob.client.overlay.bambuser;

import com.google.gwt.core.client.JavaScriptObject;

public class Video extends JavaScriptObject
{
	public static final native Video parse(final String json)
	/*-{
		return eval('(' + json + ')');
	}-*/;

	protected Video() {}

	public final native int getVid()
	/*-{ 
    	return parseInt(this.vid);
    }-*/;
	
	public final native String getTitle()
	/*-{ 
    	return this.title;
    }-*/;

 	public final native String getType()
	/*-{ 
    	return this.type;
    }-*/;

	public final native String getUsername()
	/*-{ 
    	return this.username;
    }-*/;

	public final native int getCreated()
	/*-{ 
    	return parseInt(this.created);
    }-*/;

	public final native String getPreview()
	/*-{ 
    	return this.preview;
    }-*/;

	public final native String getPage()
	/*-{ 
    	return this.page;
    }-*/;

	public final native int getWidth()
	/*-{ 
    	return parseInt(this.width);
    }-*/;

	public final native int getHeight()
	/*-{ 
    	return parseInt(this.height);
    }-*/;

	public final native String getLat()
	/*-{ 
    	return this.lat;
    }-*/;

	public final native String getLon()
	/*-{ 
    	return this.lon;
    }-*/;

	public final native float getPositionAccuracy()
	/*-{ 
    	return parseFloat(this.position_accuracy);
    }-*/;

	public final native String getPositionType()
	/*-{ 
    	return this.position_type;
    }-*/;

	public final native String getTrail()
	/*-{ 
    	return this.trail;
    }-*/;

	public final native int getViewsLive()
	/*-{ 
    	return parseInt(this.views_live);
    }-*/;

	public final native int getViewsTotal()
	/*-{ 
    	return parseInt(this.views_total);
    }-*/;

	public final native int getCommentCount()
	/*-{ 
    	return parseInt(this.comment_count);
    }-*/;

	public final native String getDeviceClass()
	/*-{ 
    	return this.device_class;
    }-*/;

	public final native String getDeviceName()
	/*-{ 
    	return this.device_name;
    }-*/;

}
