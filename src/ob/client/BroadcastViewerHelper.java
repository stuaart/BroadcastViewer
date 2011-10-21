package ob.client;

import ob.client.model.Broadcaster;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.core.client.GWT;


public class BroadcastViewerHelper
{

	public static final String MARKER_SHADOW_URL = 
		"http://www.google.com/mapfiles/shadow50.png";
	public static final String MARKER_URL_PREFIX = 
		"http://www.google.com/mapfiles/marker";

	public static final Marker createMarker(final LatLng pos, final char letter)
	{
		final Icon baseIcon = Icon.newInstance();
		baseIcon.setShadowURL(MARKER_SHADOW_URL);
		baseIcon.setIconSize(Size.newInstance(20, 34));
		baseIcon.setShadowSize(Size.newInstance(37, 34));
		baseIcon.setIconAnchor(Point.newInstance(9, 34));
		baseIcon.setInfoWindowAnchor(Point.newInstance(9, 2));

    	Icon icon = Icon.newInstance(baseIcon);
		icon.setImageURL(MARKER_URL_PREFIX + letter + ".png");
	    MarkerOptions options = MarkerOptions.newInstance();
    	options.setIcon(icon);

	    return new Marker(pos, options);
	}

	public static void updateBroadcaster(final BroadcasterServiceAsync service,
										 final Broadcaster b)
	{

		final AsyncCallback<Void> callback = 
			new AsyncCallback<Void>()
		{
			@Override
			public void onFailure(final Throwable caught) 
			{
				Window.alert("Error updating Broadcaster");
			}

			@Override
			public void onSuccess(Void v) 
			{
				GWT.log("Updated Broadcaster id=" 
						+ b.getBroadcastId());
			}
		};
		service.updateBroadcaster(b, callback);
	}

	public static final HTML createBambuserEmbed(final String vars, 
												 final int width, 
										  		 final int height)
	{
		return new HTML(
			"<object id=\"bplayer\" "
			+ "classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" "
			+ "width=\"" + String.valueOf(width) + "\" height=\"" 
			+ String.valueOf(height) + "\"><embed name=\"bplayer\" "
			+ "src=\"http://static.bambuser.com/r/player.swf\" "
			+ "type=\"application/x-shockwave-flash\" flashvars=\"" 
			+ vars + 
			"\" width=\"" + String.valueOf(width) + 
			"\" height=\"" + String.valueOf(height) 
			+ "\" allowfullscreen=\"true\" "
			+ "allowscriptaccess=\"always\" wmode=\"opaque\"></embed>"
			+ "<param name=\"movie\" "
			+ "value=\"http://static.bambuser.com/r/player.swf\"></param>"
			+ "<param name=\"flashvars\" value=\""
			+ vars + 
			"\"></param><param name=\"allowfullscreen\" value=\"true\">"
			+ "</param><param name=\"allowscriptaccess\" value=\"always\">"
			+"</param><param name=\"wmode\" value=\"opaque\"></param>"
			+ "</object>"
		);
	}


}
