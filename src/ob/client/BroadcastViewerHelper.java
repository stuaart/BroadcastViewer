package ob.client;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;

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

}
