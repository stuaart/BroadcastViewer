package ob.client;

import ob.client.overlay.bambuser.Video;
import ob.client.overlay.bambuser.Result;

import ob.client.model.bambuser.Oembed;
import ob.client.model.Broadcaster;
import ob.client.model.Config;
import ob.client.model.Dimension;

import ob.client.overlay.JSONRequest;
import ob.client.overlay.JSONRequestHandler;

import java.util.Set;
import java.util.HashSet;
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
import com.google.gwt.user.client.ui.Label;

import com.google.gwt.maps.client.event.MapClickHandler;

import com.google.gwt.maps.client.control.LargeMapControl;

import com.google.gwt.maps.client.geom.LatLng;

import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.GeoXmlLoadCallback;
import com.google.gwt.maps.client.overlay.GeoXmlOverlay;

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

	private static Config config = null;

	// Services
	private BroadcasterServiceAsync bServ = null;
	private ConfigServiceAsync cServ = null;

	private final Map<String, Broadcaster> broadcasters = 
		new HashMap<String, Broadcaster>();

	// NOTE: Broadcaster ID == Bambuser ID
	// Our own Broadcasters. Broadcaster ID <-> Google Maps marker
	private final BiMap<String, Marker> markersInt = HashBiMap.create();
	// External broadcasters on Bambuser only. 
	// Bambuser ID <-> Google Maps marker
	private final BiMap<String, Marker> markersExt = HashBiMap.create();

	private MapWidget map = null;

	private GeoXmlOverlay geoXml = null;
	private boolean overlayCache = false;

	private SimplePanel broadcastPanel = null;
	private FlexTable broadcastGrid = null;

	
	public void clickHandler(final String id)
	{
		if (id != null && config != null)
			showBroadcast(id, config.isLive());
	}
	
	public native void clickHandlerExport(final String id)
	/*-{
		var that = this;
		$wnd.clickHandler = $entry(function(id) 
		{
			that.@ob.client.BroadcastViewer::clickHandler(Ljava/lang/String;)(id)
		});
	}-*/;

	public void onModuleLoad() 
  	{

		clickHandlerExport(null);

		if (bServ == null) 
			bServ = GWT.create(BroadcasterService.class);
		if (cServ == null) 
			cServ = GWT.create(ConfigService.class);


		final AsyncCallback<Config> callback = 
			new AsyncCallback<Config>()
			{
				@Override
				public void onFailure(final Throwable t) 
				{
					GWT.log("Error getting Config");
				}
				@Override
				public void onSuccess(final Config config_) 
				{
					config = config_;
					GWT.log("Retrieved Config");
					setupUI();
					setupDebugUI();
				}
			};
		cServ.getConfig(callback);

		GWT.log("Calling initial getBroadcasters()");
		getBroadcasters();
	}

	private void setupUI()
	{

		broadcastPanel = new SimplePanel();
		RootPanel.get("selected_broadcast").add(broadcastPanel);
		// TODO: set default text
		// "Select a Broadcaster from the map or grid"
		broadcastPanel.setWidget(
			new Label("Select a Broadcaster from the map or grid")
		);

		if (config.isGWTGrid())
		{
			broadcastGrid = new FlexTable();
			RootPanel.get(config.getGridName()).add(broadcastGrid);
		}


		map = new MapWidget(LatLng.newInstance(config.getDefaultMapCentre()[0], 
											   config.getDefaultMapCentre()[1]),
											   config.getDefaultMapZoom());
    	map.setSize(config.getMapDimensions()[0], config.getMapDimensions()[1]);

    	map.addControl(new LargeMapControl());

		map.addMapClickHandler(new MapClickHandler() 
		{
			@Override
      		public void onClick(final MapClickEvent e) 
			{
        		MapWidget sender = e.getSender();
		        Overlay overlay = e.getOverlay();

		        if (overlay != null && overlay instanceof Marker)
				{
					String bid = (markersInt.inverse()).get(overlay);
					if (bid == null)
						bid = (markersExt.inverse()).get(overlay);

					if (bid != null)
						showBroadcast(bid, config.isLive());
					else
						Window.alert("Error handling marker selection");
				}
           	}
    	});

		GeoXmlOverlay.load(config.getBoundsKML(), new GeoXmlLoadCallback() 
		{

			@Override
			public void onFailure(String url, Throwable e) 
			{
				StringBuffer msg = new StringBuffer("KML File " + url
													+ " failed to load");
				if (e != null)
					msg.append(e.toString());

				GWT.log(msg.toString());
			}

			@Override
			public void onSuccess(final String url, final GeoXmlOverlay overlay)
			{
				// NOTE: workaround due to bug; Issue 459 on gwt-google-apis
				if (!overlayCache) 
				{
					geoXml = overlay;
                  	overlayCache = true;
					geoXml.setVisible(config.isOverlayVisible());
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


		// Refresh the list of Broadcasters and the display Grid
		Timer refreshTimer = new Timer() 
		{
			public void run()
			{
				getBroadcasters();
				if (config.isGWTGrid())
				{
					broadcastGrid.clear();
				   	populateBroadcastGrid(markersInt.keySet(), config.isLive());
				}
			}
		};
		refreshTimer.scheduleRepeating(config.getRefreshInterval());


		if (config.isServerPoll())
			startServerPoll();
  	}

	private void startServerPoll()
	{
		final AsyncCallback<Void> callback = new AsyncCallback<Void>()
		{
			@Override
			public void onFailure(final Throwable caught) 
			{
				//Window.alert("Error starting poll");
			}

			@Override
			public void onSuccess(final Void v) 
			{
				GWT.log("Server poll initiated");
			}
		};
		bServ.startServerPoll(callback);
	}


	private void getBroadcasters()
	{
    	final AsyncCallback<Broadcaster[]> callback = 
			new AsyncCallback<Broadcaster[]>()
			{
				@Override
				public void onFailure(final Throwable caught) 
			  	{
					//Window.alert("Error getting Broadcasters");
		      	}

				@Override
				public void onSuccess(final Broadcaster[] bs) 
				{
					GWT.log("Getting Broadcaster Markers, received size is " 
							+ bs.length + ", Broadcaster Marker cache size is " 
							+ markersInt.size());
					final Set<String> dregs = new HashSet<String>();
					dregs.addAll(markersInt.keySet());

					broadcasters.clear();
					for (final Broadcaster b : bs)
					{
						broadcasters.put(b.getBroadcastId(), b);
						final Marker m = markersInt.get(b.getBroadcastId());
						if (m == null)
						{
							final Marker m_ = 
								BroadcastViewerHelper.createMarker(
									LatLng.newInstance(b.getLatLng()[0],
													   b.getLatLng()[1]),
									'B'
								);
			    			map.addOverlay(m_);
							markersInt.put(b.getBroadcastId(), m_);
							GWT.log("Added new marker for Broadcaster, id=" 
									+ b.getBroadcastId() + ", at latlng=("
									+ b.getLatLng()[0] + ","
									+ b.getLatLng()[1] + ")");

						}
						else
						{
							m.setLatLng(LatLng.newInstance(b.getLatLng()[0],
														   b.getLatLng()[1]));
							dregs.remove(b.getBroadcastId());
						}

/*					   	final InfoWindow i = map.getInfoWindow();
						i.open(m,
    					    new InfoWindowContent("TS=" + 
								b.getTimestamp().toString())
						);

						i.setVisible(false);
*/

					}

					for (final String d : dregs)
					{
						map.removeOverlay(markersInt.get(d));
						markersInt.remove(d);
					}
					
   				}
			};

		bServ.getBroadcasters_(true, callback);
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

		String url = config.getBambuserAPIURL() + typeStr + spatialStr 
					 + "&callback=";

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
					final Marker m = markersExt.get(v.getUsername());
					if (m == null)
					{
						GWT.log("Getting video, id=" 
							+ String.valueOf(v.getVid())
							+ ", username=" + v.getUsername() + ", latlng=("
							+ Double.parseDouble(v.getLat()) + ","
							+ Double.parseDouble(v.getLon()) + ")");
						final Marker m_ = BroadcastViewerHelper.createMarker(
							LatLng.newInstance(Double.parseDouble(v.getLat()),
											   Double.parseDouble(v.getLon())),
							'E'
						);
			    		map.addOverlay(m_);
						markersExt.put(v.getUsername(), m_);
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

	private final Dimension scaleVideo(final int scale)
	{
		GWT.log("scale = " + scale);
		final Dimension[] dims = config.getVideoDimensionsLimits();
		int width = dims[0].getWidth() + scale * 10;
		int height = dims[0].getHeight() + scale * 10;
		if (width > dims[1].getWidth())
			width = dims[1].getWidth();
		if (height > dims[1].getHeight())
			height = dims[1].getHeight();

		return new Dimension(width, height);
	}
	
	// [row, col]
	private final int[] getGridCell()
	{
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

		return new int[] {row, col};
	}

	private void thumbnailHandler(final Broadcaster b)
	{
		thumbnailHandler(b, null);
	}

	private void thumbnailHandler(final String videoId)
	{
		thumbnailHandler(null, videoId);
	}

	private void thumbnailHandler(final Broadcaster b, final String videoId)
	{
		if (b != null && b.getThumbnailURL() != null)
		{
			GWT.log("Got cached thumbnail");
			final HTML embed = new HTML("<img src=\"" + b.getThumbnailURL() 
							   			+ "\">");
			int ref[] = getGridCell();
			broadcastGrid.setWidget(ref[0], ref[1], embed);
			return;
		}

		final AsyncCallback<Oembed> callback = new AsyncCallback<Oembed>()
		{
			@Override
			public void onFailure(final Throwable e) 
			{
				Window.alert("Error getting thumbnail URL: " + e.toString());
			}

			@Override
			public void onSuccess(final Oembed o) 
			{
				GWT.log("Got thumbnail URL: " + o.getThumbnailURL());
				final HTML embed = new HTML("<img src=\"" 
					+ o.getThumbnailURL() + "\">");

				GWT.log("embed=" + embed.toString());

				if (b != null)
					b.setThumbnailURL(o.getThumbnailURL());

				int ref[] = getGridCell();
				broadcastGrid.setWidget(ref[0], ref[1], embed);

			}
		};
		if (b != null && b.getVideoId() != null)
			bServ.getOembed(b.getVideoId(), callback);
		else
			bServ.getOembed(videoId, callback);

	/*	String url = 
			config.getBambuserOembedURL() + "?url=" 
			+ "http%3A%2F%2Fbambuser.com%2Fv%2F" + vidId;

	
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		builder.setHeader("Content-Type", "application/json");
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
						Window.alert("Error getting JSON for Oembed");
					}

					@Override
					public void onResponseReceived(final Request request, 
												   final Response response) 
					{
						GWT.log("Header: " + response.getHeadersAsString());
						GWT.log("Content: " + response.getText());
						if (response.getStatusCode() == Response.SC_OK) 
						{
							try 
							{
            					response.getText();
								final Oembed result = 
									Oembed.parse(response.getText());
								GWT.log("oembed=" + result.getThumbnailURL());

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
/*
		JSONRequest.get(url, new JSONRequestHandler() 
		{
			@Override
			public void onRequestComplete(JavaScriptObject json)
			{
				
				final Oembed oembed = (Oembed)json;
				
				final HTML embed = new HTML("<img src=\"" 
					+ oembed.getThumbnailURL() + "\">");
				int ref[] = getGridCell();
				GWT.log("embed=" + embed.toString());
				broadcastGrid.setWidget(ref[0], ref[1], embed);

			}
		});
*/
	}

	private void populateBroadcastGrid(final Set<String> usernames, 
									   final boolean liveOnly)
	{
		
		int elems = 0;
		for (final String username : usernames)
		{
			GWT.log("Username = " + username + ", elem = " + elems);
	
			final Broadcaster b = broadcasters.get(username);
			if (b != null && b.getVideoId() != null)
			{
				final Dimension d = scaleVideo(b.getViews());
/*
				final HTML embed = 
					BroadcastViewerHelper.createBambuserEmbed("vid=" 
						+ String.valueOf(b.getVideoId()) + "&chat=no",
						d.getWidth(), d.getHeight()
					);

				int ref[] = getGridCell();

    	    	broadcastGrid.setWidget(ref[0], ref[1], embed);
*/
				thumbnailHandler(b);

				GWT.log("populateBroadcastGrid() Used cached embed");
				continue;
			}

			String typeStr = "";
			if (liveOnly)
				typeStr = "&type=live";

			String url = config.getBambuserAPIURL() + "&limit=1&username=" 
						 + username + typeStr + "&callback=";

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

						final Broadcaster b = 
							broadcasters.get(vid.getUsername());
						Dimension d = null;
						if (b != null)
						{
							b.setVideoId(String.valueOf(vid.getVid()));
							d = scaleVideo(b.getViews());
							thumbnailHandler(b);
						}
						else
						{
							d = scaleVideo(vid.getViewsLive());
							thumbnailHandler(String.valueOf(vid.getVid()));
						}
/*
						final HTML embed = 
							BroadcastViewerHelper.createBambuserEmbed("vid=" 
								+ String.valueOf(vid.getVid()) + "&chat=no",
								d.getWidth(), d.getHeight()
							);

						int ref[] = getGridCell();

    	    			broadcastGrid.setWidget(ref[0], ref[1], embed);
*/
					
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

	
	private void showBroadcast(final String username, final boolean liveOnly)
	{
		// First see if we know about this as a Broadcaster object
		// Otherwise this must be an external or a Broadcaster without a cache
		final Broadcaster b = broadcasters.get(username);
		if (b != null && b.getVideoId() != null)
		{
			final Dimension d = config.getBroadcastWindowDimension();
			final HTML embed = 
					BroadcastViewerHelper
						.createBambuserEmbed("vid=" + b.getVideoId() 
											 + "&chat=yes", d.getWidth(), 
											 d.getHeight());
			broadcastPanel.setWidget(embed);
			GWT.log("showBroadcast() Used cached embed");
			b.setViews(b.getViews() + 1);
			BroadcastViewerHelper.updateBroadcaster(bServ, b);
			return;
		}

		String typeStr = "";
		if (liveOnly)
			typeStr = "&type=live";

		String url = config.getBambuserAPIURL() + "&limit=1&username=" 
					 + username + typeStr + "&callback=";

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
					final Dimension d = config.getBroadcastWindowDimension();
					final HTML embed = 
						BroadcastViewerHelper
							.createBambuserEmbed("vid=" + vid.getVid()
												 + "&chat=yes",  d.getWidth(), 
												 d.getHeight());
					broadcastPanel.setWidget(embed);
					final Broadcaster b = broadcasters.get(vid.getUsername());
					if (b != null)
					{
						GWT.log("Setting Video ID");
						b.setVideoId(String.valueOf(vid.getVid()));
						b.setViews(b.getViews() + 1);
						BroadcastViewerHelper.updateBroadcaster(bServ, b);	
					}
					else
					{
						GWT.log("b is null for broadcasters.get(" 
							    + vid.getUsername() + ")");
					}

				}
				else
					Window.alert("Error loading live video for Broadcaster");

			}
		});

	}


	// ADMIN METHODS
	private void setupDebugUI()
	{
		final FlowPanel mainPanel = new FlowPanel();
		final Button addBroadcasters = new Button("Add Broadcasters");
		final Button delBroadcaster = new Button("Remove at random");
		final Button getExternals = new Button("Get Externals");
		final Button popGridE = new Button("Populate Grid - Externals");

			
		mainPanel.add(addBroadcasters);
		mainPanel.add(delBroadcaster);
		mainPanel.add(getExternals);
		mainPanel.add(popGridE);

		addBroadcasters.addClickHandler(new ClickHandler() 
		{
			@Override
      		public void onClick(final ClickEvent event) 
			{
			   	addFakeBroadcasters();
      		}
	    });

		delBroadcaster.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event) 
			{
			   	delRandomBroadcaster();
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
					updateExternalBroadcasters(config.isLive(),
											   geoXml.getDefaultCenter(),
											   span);
				}
      		}
	    });

		popGridE.addClickHandler(new ClickHandler() 
		{
			@Override
      		public void onClick(final ClickEvent event) 
			{
			   	populateBroadcastGrid(markersExt.keySet(), config.isLive());
      		}
	    });

		RootPanel.get("debug_panel").add(mainPanel);
	}

	private void addFakeBroadcasters()
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

	private void delRandomBroadcaster()
	{
		final AsyncCallback<Broadcaster[]> callback = 
			new AsyncCallback<Broadcaster[]>()
			{
				@Override
				public void onFailure(final Throwable caught) 
			  	{
					//Window.alert("Error getting Broadcasters");
		      	}

				@Override
				public void onSuccess(final Broadcaster[] bs) 
				{
					if (bs.length == 0)
					{
						GWT.log("Error deleting anything: no Broadcasters");
						return;
					}

					int n = (int)Math.ceil((Math.random() * bs.length)) - 1;

					final AsyncCallback<Void> callback = 
						new AsyncCallback<Void>()
					{
						@Override
						public void onFailure(final Throwable caught) 
						{
							Window.alert("Error deleting Broadcaster");
						}
						@Override
						public void onSuccess(Void v) 
						{
							GWT.log("Deleted Broadcaster");
						}
					};
					bServ.deleteBroadcaster(bs[n].getBroadcastId(), callback);
				}
			};

		bServ.getBroadcasters_(true, callback);

	}

}
