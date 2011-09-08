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
    	return this.vid;
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
    	return this.created;
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
    	return this.width;
    }-*/;

	public final native int getHeight()
	/*-{ 
    	return this.height;
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
    	return this.position_accuracy;
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
    	return this.views_live;
    }-*/;

	public final native int getViewsTotal()
	/*-{ 
    	return this.views_total;
    }-*/;

	public final native int getCommentCount()
	/*-{ 
    	return this.comment_count;
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
