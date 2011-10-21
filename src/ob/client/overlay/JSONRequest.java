// Taken from http://www.gwtsite.com/

package ob.client.overlay;
import com.google.gwt.core.client.GWT;


public class JSONRequest 
{

	public static void get(String url, JSONRequestHandler handler)
	{
		String callbackName = "JSONCallback" + handler.hashCode();
		get(url + callbackName, callbackName, handler);
	}

	public static void get(String url, String callbackName, 
						   JSONRequestHandler handler)
	{
		createCallbackFunction(handler, callbackName);
		addScript(url);
		GWT.log("get(): " + url);
	}

	public static native void addScript(String url) 
	/*-{
		var scr = document.createElement("script");
		scr.setAttribute("language", "JavaScript");
		scr.setAttribute("src", url);
		document.getElementsByTagName("body")[0].appendChild(scr);
	}-*/;
	
	private native static void createCallbackFunction(JSONRequestHandler obj, 
													  String callbackName)
	/*-{
		tmpcallback = function(j)
		{
			obj.@ob.client.overlay.JSONRequestHandler::onRequestComplete(Lcom/google/gwt/core/client/JavaScriptObject;)(j);
		};
		eval("window." + callbackName + "=tmpcallback");
	}-*/;
}
