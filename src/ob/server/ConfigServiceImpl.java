package ob.server;

import ob.client.ConfigService;
import ob.client.model.Dimension;
import ob.client.model.Config;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ConfigServiceImpl extends RemoteServiceServlet 
							   implements ConfigService

{

	public static final String BOUNDS_KML = 
		"http://horizab1.miniserver.com/~stuart/route.kml";

	public static final String API_URL = 
		"http://api.bambuser.com/broadcast.json?"
		+ "api_key=5de47d62949952e6b3b6aa655d4a85de";

	public static final String API_OEMBED_URL = 
		"http://api.bambuser.com/oembed.json?url=http%3A%2F%2Fbambuser.com%2Fv%2F";

	public static final double[] DEFAULT_MAP_CENTRE = 
		new double[] {52.951948, -1.170044};
	public static final int DEFAULT_MAP_ZOOM = 11;

	private static final int REFRESH_INTERVAL = 5000;

	// true = live video, false = latest video
	private static final boolean IS_LIVE = false;

	public static final Dimension[] VIDEO_DIMENSIONS_LIMITS = 
		{new Dimension(150, 150), new Dimension(500, 500)};

	public static final String[] MAP_DIMENSIONS = 
		new String[] {"600px", "300px"};

	
	// Is a poll conducted on the server (devmode) or is it a cron job?
	public static final boolean IS_SERVER_POLL = false;//true;

	// Draw GWT-based video grid or Javascript one?
	public static final boolean IS_GWT_GRID = false;

	public static final String GRID_NAME = "broadcast_grid";

	public static final boolean GRID_FILLER = true;

	public static final Dimension BROADCAST_WINDOW_DIMENSION = 
		new Dimension(400, 800);

	public static final boolean IS_OVERLAY_VISIBLE = false;

	public static final int MAX_GRID = 16;

	private static final Config c = 
		new Config(BOUNDS_KML, API_URL, API_OEMBED_URL, DEFAULT_MAP_CENTRE, 
				   DEFAULT_MAP_ZOOM, REFRESH_INTERVAL, IS_LIVE, 
				   VIDEO_DIMENSIONS_LIMITS, MAP_DIMENSIONS, IS_SERVER_POLL,
				   IS_GWT_GRID, GRID_NAME, GRID_FILLER, 
				   BROADCAST_WINDOW_DIMENSION, IS_OVERLAY_VISIBLE, MAX_GRID);
	

	@Override
	public final Config getConfig() throws IllegalArgumentException
	{
		return c;
	}

	public static final Config getConfigStatic() 
	{
		return c;
	}

}
