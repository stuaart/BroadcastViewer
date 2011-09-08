// Taken from http://www.gwtsite.com/

package ob.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;

public interface JSONRequestHandler 
{
	public void onRequestComplete(JavaScriptObject json);
}
