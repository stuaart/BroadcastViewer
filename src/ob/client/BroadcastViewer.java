package ob.client;

import ob.shared.FieldVerifier;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Button;

public class BroadcastViewer implements EntryPoint 
{

	public static final String API_KEY = 
		"ABQIAAAAtF5SboVnhVj0WotUQSsLbBQm6mIxqHkxQ0GIaQ76A7J1HXMTaRSQDqXDW1pwpcAQ4NLcvKk0nBzNJQ";
	private BroadcasterServiceAsync bServ = 
		GWT.create(BroadcasterService.class);

	private MapWidget map = null;

	public void onModuleLoad() 
  	{
		if (bServ == null) 
			bServ = GWT.create(BroadcasterService.class);

		final FlowPanel mainPanel = new FlowPanel();
		final Button addBroadcasters = new Button("Add Broadcasters");
		final Button getBroadcasters = new Button("Get Broadcasters");
		mainPanel.add(addBroadcasters);
		mainPanel.add(getBroadcasters);
		RootPanel.get("markers").add(mainPanel);

		addBroadcasters.addClickHandler(new ClickHandler() 
		{
      		public void onClick(final ClickEvent event) 
			{
			   	populateBroadcasters();
      		}
	    });
		getBroadcasters.addClickHandler(new ClickHandler() 
		{
      		public void onClick(final ClickEvent event) 
			{
				getBroadcasters();
      		}
	    });


		Maps.loadMapsApi(API_KEY, "2", false, new Runnable() 
		{
    	  	public void run() 
			{
        		buildMap();
      		}
    	});
  	}

	private void populateBroadcasters()
	{
		AsyncCallback<String> callback = new AsyncCallback<String>()
		{
			public void onFailure(final Throwable caught) 
			{
			}
			public void onSuccess(final String s) 
			{
				GWT.log("Populated broadcasters: " + s);
			}
		};
		bServ.addBroadcasters(callback);

	}

	private void getBroadcasters()
	{
    	AsyncCallback<Broadcaster[]> callback = 
			new AsyncCallback<Broadcaster[]>()
			{
				public void onFailure(final Throwable caught) 
			  	{
		      	}
				public void onSuccess(final Broadcaster[] bs) 
				{
					GWT.log("Getting Broadcasters, size is " + bs.length);
					for (Broadcaster b : bs)
					{
						final Marker m = new Marker(
							LatLng.newInstance(b.getLatLng()[0],
											   b.getLatLng()[1])
						);
			    		map.addOverlay(m);
					   	final InfoWindow i = map.getInfoWindow();
						i.open(m,
    					    new InfoWindowContent("TS=" + 
								b.getTimestamp().toString())
						);

						i.setVisible(false);
					}
					
   				}
			};

		bServ.getBroadcasters(callback);
	}

    private void buildMap() 
	{

		//LatLng cawkerCity = LatLng.newInstance(39.509, -98.434);

    	map = new MapWidget();
    	map.setSize("100%", "100%");
    	map.addControl(new LargeMapControl());

//    	map.addOverlay(new Marker(cawkerCity));

 	
	    final DockLayoutPanel dock2 = new DockLayoutPanel(Unit.PX);
	    dock2.addNorth(map, 500);

    	RootPanel.get("map").add(dock2);
  	}

}
