package ob.client;

import ob.shared.FieldVerifier;
import ob.client.overlay.bambuser.Video;
import ob.client.overlay.bambuser.Result;
import ob.client.model.Broadcaster;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.core.client.EntryPoint;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Button;

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

	public static final String ROUTE_KML = 
		"http://horizab1.miniserver.com/~stuart/route.kml";

	public static final String API_URL = 
		"http://api.bambuser.com/broadcast.json?"
		+ "api_key=5de47d62949952e6b3b6aa655d4a85de";

	public static final LatLng mapCentre = 
		LatLng.newInstance(52.951948, -1.170044);

	private BroadcasterServiceAsync bServ = 
		GWT.create(BroadcasterService.class);

	private final BiMap<Broadcaster, Marker> broadcasters = HashBiMap.create();

	private MapWidget map = null;

	private GeoXmlOverlay geoXml = null;
	private boolean overlayCache = false;

	private SimplePanel broadcastPanel = null;

	private boolean liveOnly = false;
	

	public void onModuleLoad() 
  	{
		if (bServ == null) 
			bServ = GWT.create(BroadcasterService.class);

		broadcastPanel = new SimplePanel();
		RootPanel.get("broadcast").add(broadcastPanel);

		final FlowPanel mainPanel = new FlowPanel();
		final Button addBroadcasters = new Button("Add Broadcasters");
		final Button getBroadcasters = new Button("Get Broadcasters");
		mainPanel.add(addBroadcasters);
		mainPanel.add(getBroadcasters);
		RootPanel.get("markers").add(mainPanel);

		addBroadcasters.addClickHandler(new ClickHandler() 
		{
			@Override
      		public void onClick(final ClickEvent event) 
			{
			   	populateBroadcasters();
      		}
	    });
		getBroadcasters.addClickHandler(new ClickHandler() 
		{
			@Override
      		public void onClick(final ClickEvent event) 
			{
				getBroadcasters();
      		}
	    });
 	
		map = new MapWidget(mapCentre, 11);
    	map.setSize("100%", "100%");
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
					final Broadcaster b = (broadcasters.inverse()).get(overlay);
					showBroadcast(b);
				}
           	}
    	});

		GeoXmlOverlay.load(ROUTE_KML, new GeoXmlLoadCallback() 
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
				}
			}
		});
 	
	    final DockLayoutPanel dock2 = new DockLayoutPanel(Unit.PX);
	    dock2.addNorth(map, 500);

    	RootPanel.get("map").add(dock2);
		GWT.log("Added Map");

  	}

	private void populateBroadcasters()
	{
		AsyncCallback<String> callback = new AsyncCallback<String>()
		{
			@Override
			public void onFailure(final Throwable caught) 
			{
				Window.alert("Error populating Broadcasters");
			}

			@Override
			public void onSuccess(final String s) 
			{
				GWT.log("Populated Broadcasters: " + s);
			}
		};
		bServ.addBroadcasters(callback);

	}

	private void getBroadcasters()
	{
    	AsyncCallback<Broadcaster[]> callback = 
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
					broadcasters.clear();
					GWT.log("Getting Broadcasters, size is " + bs.length);
					for (Broadcaster b : bs)
					{
						Marker m = new Marker(
							LatLng.newInstance(b.getLatLng()[0],
											   b.getLatLng()[1])
						);
			    		map.addOverlay(m);
						broadcasters.put(b, m);
/*					   	final InfoWindow i = map.getInfoWindow();
						i.open(m,
    					    new InfoWindowContent("TS=" + 
								b.getTimestamp().toString())
						);

						i.setVisible(false);*/

					}
					
   				}
			};

		bServ.getBroadcasters(callback);
	}

	private void showBroadcast(final Broadcaster b)
	{

		GWT.log("Showing Broadcaster = " + b.getBroadcastId());

		String typeStr = "";
		if (liveOnly)
			typeStr = "&type=live";

		String url = API_URL + "&limit=1&username=" + b.getBroadcastId() 
				     + typeStr;

		url = URL.encode(url);
 
		GWT.log("Requesting URL: " + url);

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
  
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

		//final String vars = "vid=" + "" + "&chat=no";
		final String vars = "username=" + b.getBroadcastId() + "&chat=no";
		final String html = 
			"<object id=\"bplayer\" "
		 	+ "classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" "
			+ "width=\"320\" height=\"276\"><embed name=\"bplayer\" "
			+ "src=\"http://static.bambuser.com/r/player.swf\" "
			+ "type=\"application/x-shockwave-flash\" flashvars=\"" 
			+ vars + 
			"\" width=\"320\" height=\"276\" allowfullscreen=\"true\" "
			+ "allowscriptaccess=\"always\" wmode=\"opaque\"></embed>"
			+ "<param name=\"movie\" "
			+ "value=\"http://static.bambuser.com/r/player.swf\"></param>"
			+ "<param name=\"flashvars\" value=\""
			+ vars + 
			"\"></param><param name=\"allowfullscreen\" value=\"true\">"
			+ "</param><param name=\"allowscriptaccess\" value=\"always\">"
			+"</param><param name=\"wmode\" value=\"opaque\"></param></object>";
		broadcastPanel.setWidget(new HTML(html));
	}

}
