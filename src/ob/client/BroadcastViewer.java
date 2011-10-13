package ob.client;

import ob.shared.FieldVerifier;
import ob.client.overlay.bambuser.Video;
import ob.client.overlay.bambuser.Result;
import ob.client.model.Broadcaster;
import ob.client.overlay.JSONRequest;
import ob.client.overlay.JSONRequestHandler;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import com.google.gwt.core.client.JavaScriptObject;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import com.google.gwt.user.client.Timer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.core.client.EntryPoint;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Button;

import com.google.gwt.maps.client.event.MapClickHandler;

import com.google.gwt.maps.client.control.LargeMapControl;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;

import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.GeoXmlLoadCallback;
import com.google.gwt.maps.client.overlay.GeoXmlOverlay;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.MarkerOptions;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.dom.client.Style.Unit;

import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

public class BroadcastViewer implements EntryPoint 
{

	public static final String BOUNDS_KML = 
		"http://horizab1.miniserver.com/~stuart/route.kml";

	public static final String API_URL = 
		"http://api.bambuser.com/broadcast.json?"
		+ "api_key=5de47d62949952e6b3b6aa655d4a85de";

	public static final LatLng DEFAULT_MAP_CENTRE = 
		LatLng.newInstance(52.951948, -1.170044);
	public static final int DEFAULT_MAP_ZOOM = 11;

	private static final int REFRESH_INTERVAL = 5000;

	// true = live video, false = latest video
	private static final boolean LIVE_ONLY = false;

	public static final int VIDEO_WIDTH_MIN = 150;
	public static final int VIDEO_HEIGHT_MIN = 150;
	public static final int VIDEO_WIDTH_MAX = 500;
	public static final int VIDEO_HEIGHT_MAX = 500;

	public static final String MAP_WIDTH = "300px";
	public static final String MAP_HEIGHT = "300px";


	private BroadcasterServiceAsync bServ = 
		GWT.create(BroadcasterService.class);

	// NOTE: Broadcaster ID == Bambuser ID
	// Broadcaster ID <-> Google Maps marker
	private final BiMap<String, Marker> broadcasters = HashBiMap.create();
	// Bambuser ID <-> Google Maps marker
	private final BiMap<String, Marker> externals = HashBiMap.create();

	private MapWidget map = null;

	private GeoXmlOverlay geoXml = null;
	private boolean overlayCache = false;

	private SimplePanel broadcastPanel = null;
	private FlexTable broadcastGrid = null;

	

	public void onModuleLoad() 
  	{
		if (bServ == null) 
			bServ = GWT.create(BroadcasterService.class);

		broadcastPanel = new SimplePanel();
		RootPanel.get("selected_broadcast").add(broadcastPanel);

		broadcastGrid = new FlexTable();
		RootPanel.get("broadcast_grid").add(broadcastGrid);

		final FlowPanel mainPanel = new FlowPanel();
		final Button addBroadcasters = new Button("[Add Broadcasters]");
		final Button getExternals = new Button("Get Externals");		
		final Button popGridE = new Button("Populate Grid - Externals");		
		final Button popGridI = new Button("Populate Grid - Internals");
		mainPanel.add(addBroadcasters);
		mainPanel.add(getExternals);
		mainPanel.add(popGridE);
		mainPanel.add(popGridI);
		RootPanel.get("markers").add(mainPanel);

		addBroadcasters.addClickHandler(new ClickHandler() 
		{
			@Override
      		public void onClick(final ClickEvent event) 
			{
			   	populateBroadcasters();
      		}
	    });
		popGridE.addClickHandler(new ClickHandler() 
		{
			@Override
      		public void onClick(final ClickEvent event) 
			{
			   	populateBroadcastGrid(externals.keySet(), LIVE_ONLY);
      		}
	    });
		popGridI.addClickHandler(new ClickHandler() 
		{
			@Override
      		public void onClick(final ClickEvent event) 
			{
			   	populateBroadcastGrid(broadcasters.keySet(), LIVE_ONLY);
      		}
	    });
 		getExternals.addClickHandler(new ClickHandler() 
		{
			@Override
      		public void onClick(final ClickEvent event) 
			{
				if (geoXml != null)
				{
					final int span = 
						(int)Math.ceil(
							geoXml.getDefaultSpan()
								  .distanceFrom(LatLng.newInstance(0, 0))
						);
					updateExternalBroadcasters(LIVE_ONLY,
											   geoXml.getDefaultCenter(),
											   span);
				}
      		}
	    });

		map = new MapWidget(DEFAULT_MAP_CENTRE, DEFAULT_MAP_ZOOM);
    	map.setSize(MAP_WIDTH, MAP_HEIGHT);
    	map.addControl(new LargeMapControl());

		map.addMapClickHandler(new MapClickHandler() 
		{
			@Override
      		public void onClick(MapClickEvent e) 
			{
        		MapWidget sender = e.getSender();
		        Overlay overlay = e.getOverlay();

		        if (overlay != null && overlay instanceof Marker)
				{
					String bid = (broadcasters.inverse()).get(overlay);
					if (bid == null)
						bid = (externals.inverse()).get(overlay);

					if (bid != null)
						showBroadcast(bid, LIVE_ONLY);
					else
						Window.alert("Error handling marker selection");
				}
           	}
    	});

		GeoXmlOverlay.load(BOUNDS_KML, new GeoXmlLoadCallback() 
		{

			@Override
			public void onFailure(String url, Throwable e) 
			{
				StringBuffer msg = new StringBuffer("KML File " + url
													+ " failed to load");
				if (e != null)
					msg.append(e.toString());

				Window.alert(msg.toString());
			}

			@Override
			public void onSuccess(String url, GeoXmlOverlay overlay) 
			{
				// NOTE: workaround due to bug; Issue 459 on gwt-google-apis
				if (!overlayCache) 
				{
					geoXml = overlay;
                  	overlayCache = true;
                  	map.addOverlay(geoXml);
					GWT.log("KML File " + url + "loaded successfully");
					GWT.log("Default Center=" + geoXml.getDefaultCenter());
					GWT.log("Default Span=" + geoXml.getDefaultSpan());
					GWT.log("Default Bounds=" + geoXml.getDefaultBounds());
					GWT.log("Supports hide=" + geoXml.supportsHide());
					map.setCenter(geoXml.getDefaultCenter());
					map.setZoomLevel(
						map.getBoundsZoomLevel(geoXml.getDefaultBounds())
					);
				}
			}
		});
 	
	    final DockLayoutPanel dock2 = new DockLayoutPanel(Unit.PX);
	    dock2.addNorth(map, 500);

    	RootPanel.get("map").add(dock2);
		GWT.log("Added Map");

		Timer refreshTimer = new Timer() {
			public void run()
			{
				getAllBroadcasters();
			}
		};
		refreshTimer.scheduleRepeating(REFRESH_INTERVAL);


  	}

	private final Marker createMarker(final LatLng pos, final char letter)
	{
		final Icon baseIcon = Icon.newInstance();
		baseIcon.setShadowURL("http://www.google.com/mapfiles/shadow50.png");
		baseIcon.setIconSize(Size.newInstance(20, 34));
		baseIcon.setShadowSize(Size.newInstance(37, 34));
		baseIcon.setIconAnchor(Point.newInstance(9, 34));
		baseIcon.setInfoWindowAnchor(Point.newInstance(9, 2));

    	Icon icon = Icon.newInstance(baseIcon);
		icon.setImageURL("http://www.google.com/mapfiles/marker" + letter 
						 + ".png");
	    MarkerOptions options = MarkerOptions.newInstance();
    	options.setIcon(icon);

	    return new Marker(pos, options);
	}

	private void populateBroadcasters()
	{
		final AsyncCallback<Void> callback = new AsyncCallback<Void>()
		{
			@Override
			public void onFailure(final Throwable caught) 
			{
				Window.alert("Error populating Broadcasters");
			}

			@Override
			public void onSuccess(Void v) 
			{
				GWT.log("Populated Broadcasters");
			}
		};
		bServ.addBroadcasters(callback);

	}

	private void getAllBroadcasters()
	{
    	final AsyncCallback<Broadcaster[]> callback = 
			new AsyncCallback<Broadcaster[]>()
			{
				@Override
				public void onFailure(final Throwable caught) 
			  	{
					Window.alert("Error getting Broadcasters");
		      	}

				@Override
				public void onSuccess(final Broadcaster[] bs) 
				{
					GWT.log("Getting Broadcasters, received size is " 
							+ bs.length + ", Broadcasters cache size is " 
							+ broadcasters.size());
					for (final Broadcaster b : bs)
					{
						final Marker m = broadcasters.get(b.getBroadcastId());
						if (m == null)
						{
							final Marker m_ = createMarker(
								LatLng.newInstance(b.getLatLng()[0],
												   b.getLatLng()[1]),
								'B'
							);
			    			map.addOverlay(m_);
							broadcasters.put(b.getBroadcastId(), m_);
							GWT.log("Added new marker for Broadcaster, id=" 
									+ b.getBroadcastId() + ", at latlng=("
									+ b.getLatLng()[0] + ","
									+ b.getLatLng()[1] + ")");

						}
						else
						{
							m.setLatLng(LatLng.newInstance(b.getLatLng()[0],
														   b.getLatLng()[1]));
							GWT.log("Updated marker for Broadcaster, id=" 
									+ b.getBroadcastId());

						}
/*					   	final InfoWindow i = map.getInfoWindow();
						i.open(m,
    					    new InfoWindowContent("TS=" + 
								b.getTimestamp().toString())
						);

						i.setVisible(false);*/

					}
					//TODO do something for deleted Broadcasters
					
   				}
			};

		bServ.getAllBroadcasters(callback);
	}

	private void updateExternalBroadcasters(final boolean liveOnly,
											final LatLng centre,
											final int radius)
	{

		String spatialStr = "";
		if (centre != null)
		{
			spatialStr = "&lat=" + Double.toString(centre.getLatitude()) 
						 + "&lon=" + Double.toString(centre.getLongitude()) 
						 + "&geo_distance=" + Integer.toString(radius);
		}

		String typeStr = "";
		if (liveOnly)
			typeStr = "&type=live";

		String url = API_URL + typeStr + spatialStr + "&callback=";

		url = URL.encode(url);
 
		GWT.log("Requesting URL: " + url);

		JSONRequest.get(url, new JSONRequestHandler() 
		{
			@Override
			public void onRequestComplete(JavaScriptObject json)
			{
				final Result result = (Result)json;

				for (final Video v : result.getVideos())
				{
					final Marker m = externals.get(v.getUsername());
					if (m == null)
					{
						GWT.log("Getting video, id=" 
							+ String.valueOf(v.getVid())
							+ ", username=" + v.getUsername() + ", latlng=("
							+ Double.parseDouble(v.getLat()) + ","
							+ Double.parseDouble(v.getLon()) + ")");
						final Marker m_ = createMarker(
							LatLng.newInstance(Double.parseDouble(v.getLat()),
											   Double.parseDouble(v.getLon())),
							'E'
						);
			    		map.addOverlay(m_);
						externals.put(v.getUsername(), m_);
					}
					else
					{
						m.setLatLng(LatLng.newInstance(
							Double.parseDouble(v.getLat()),
							Double.parseDouble(v.getLon()))
						);
						GWT.log("Updated marker for video, id=" + v.getVid());

					}
				}

			
			}
		});

	}

	private final Dimension scaleVideo(final Video v)
	{
		final int scale = v.getViewsLive();
		GWT.log("scale = " + scale);
		int width = VIDEO_WIDTH_MIN + scale * 10;
		int height = VIDEO_HEIGHT_MIN + scale * 10;
		if (width > VIDEO_WIDTH_MAX)
			width = VIDEO_WIDTH_MAX;
		if (height > VIDEO_HEIGHT_MAX)
			height = VIDEO_HEIGHT_MAX;

		return new Dimension(width, height);
	}

	private void populateBroadcastGrid(final Set<String> usernames, 
									   final boolean liveOnly)
	{
		
		int elems = 0;
		for (final String username : usernames)
		{
			GWT.log("Username = " + username + ", elem = " + elems);
	
			String typeStr = "";
			if (liveOnly)
				typeStr = "&type=live";

			String url = API_URL + "&limit=1&username=" + username
						 + typeStr + "&callback=";

			url = URL.encode(url);
 
			GWT.log("populateBroadcastGrid() Requesting URL: " + url);
			
		
			JSONRequest.get(url, new JSONRequestHandler() 
			{
				@Override
				public void onRequestComplete(JavaScriptObject json)
				{
					final Result result = (Result)json;

					Video vid = null;
					for (final Video v : result.getVideos())
					{
						vid = v;
						break;
					}

					if (vid != null)
					{
						GWT.log("Getting video, id=" 
								+ String.valueOf(vid.getVid()) 
								+ ", username=" + vid.getUsername());

						final Dimension d = scaleVideo(vid);

						final HTML embed = 
							createBambuserEmbed("vid=" 
								+ String.valueOf(vid.getVid()) + "&chat=no",
								d.getWidth(), d.getHeight()
							);
						
						int row = broadcastGrid.getRowCount();
						
						int col = 0;
						try
						{
							if (row > 0)
								row -= 1;
							col = broadcastGrid.getCellCount(row);
							if (col > 3)
							{
								row += 1;
								col = 0;
							}
							else
								col += 1;
						}
						catch (final IndexOutOfBoundsException e) { }
												
    	    			broadcastGrid.setWidget(row, col, embed);
					}
					else
					{
						Window.alert(
							"Error loading live video for Broadcaster");
					}

				}
			});


			if (elems++ > 9)
			{
				GWT.log("Grid size exceeded");
				break;
			}
		}
	}


	private void showBroadcast(final Broadcaster b, final boolean liveOnly)
	{
		GWT.log("Showing Broadcaster = " + b.getBroadcastId());
		showBroadcast(b.getBroadcastId(), liveOnly);
	}


	private void showBroadcast(final String username, final boolean liveOnly)
	{

		String typeStr = "";
		if (liveOnly)
			typeStr = "&type=live";

		String url = API_URL + "&limit=1&username=" + username
				     + typeStr + "&callback=";

		url = URL.encode(url);
 
		GWT.log("showBroadcast() Requesting URL: " + url);
/*
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
  		builder.setTimeoutMillis(2000);

		try 
		{
    		final Request request = 
				builder.sendRequest(null, new RequestCallback() 
				{
					@Override
					public void onError(final Request request, 
									    final Throwable exception) 
					{
						Window.alert("Error getting JSON for video");
					}

					@Override
					public void onResponseReceived(final Request request, 
												   final Response response) 
					{
						GWT.log("Header: " + response.getHeadersAsString());
						if (response.getStatusCode() == Response.SC_OK) 
						{
							try 
							{
            					response.getText();
								final Result result = 
									Result.parse(response.getText());
								for (Video v : result.getVideos())
									GWT.log("Video, vid id = " + v.getVid());

            				}
							catch (final Throwable e) 
							{
								Window.alert("Could not parse JSON");
          					}
        				} 
						else
						{
							Window.alert("Couldn't retrieve JSON " 
										 + response.getStatusCode() + ":[" 
										 + response.getStatusText() + "]");
						}
					}
      			});
  		} 
		catch (final Throwable e) 
		{
			Window.alert("Couldn't retrieve JSON");         
		}
*/

		JSONRequest.get(url, new JSONRequestHandler() 
		{
			@Override
			public void onRequestComplete(JavaScriptObject json)
			{
				final Result result = (Result)json;

				// Should only get one result if URL request set to live only
				Video vid = null;
				for (Video v : result.getVideos())
				{
					vid = v;
					break;
				}

				if (vid != null)
				{
					GWT.log("Getting video, id=" + vid.getVid() 
							+ ", username=" + vid.getUsername());
					final HTML embed = 
						createBambuserEmbed("vid=" + vid.getVid() + "&chat=yes",
											300, 300);
					broadcastPanel.setWidget(embed);
				}
				else
					Window.alert("Error loading live video for Broadcaster");

			}
		});

	}

	private final HTML createBambuserEmbed(final String vars, final int width, 
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
