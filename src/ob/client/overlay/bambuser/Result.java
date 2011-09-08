package ob.client.overlay.bambuser;

import ob.client.overlay.JSIterator;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import java.util.Iterator;

public class Result extends JavaScriptObject
{

	public static final native Result parse(final String json)
	/*-{
		return eval('(' + json + ')');
	}-*/;

	protected Result() {}

	public final Iterable<Video> getVideos()
	{
		return new Iterable<Video>()
		{
			@Override
			public Iterator<Video> iterator()
			{
				return new JSIterator<Video>(getVideosInternal());
			}
		};
	}

	private final native JsArray<Video> getVideosInternal()
	/*-{
		if(!('result' in this))
		{
			this.result = new Array();
		}
		return this.result;
	}-*/;

}
