package edu.msu.project3;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	
    private LocationManager locationManager = null;
    
    private ActiveListener activeListener = new ActiveListener();

    /**
     * Request code when selecting a color
     */
    private static final int SELECT_COLOR = 1;
    
    /**
     * The pencil color
     */
	private int pencilColor;
    
	/**
	 * The Drawing View
	 */
    private DrawingView drawingView = null;
    private int width = -1;
    private int height = -1;
    
    private double latitude = 0;
    private double longitude = 0;
    private boolean valid = false;
    
    private boolean isNew = true;
    private boolean drawmid = true;
    private double baseLat = 0;
    private double baseLong = 0;
    
    private int mapScale = 100000;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        
        TextView viewProvider = (TextView)findViewById(R.id.textProvider);
        viewProvider.setText("");
		
		drawingView = (DrawingView)findViewById(R.id.drawingView);
		
        // Force the screen to say on and bright
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        setUI(); 
        
	}
	
    /**
     * Handle an options menu selection
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.itemReset:
        	reset();
        	return true;
            
        case R.id.itemScale:
        	reset();
        	
        	// The drawing is done
            // Instantiate a dialog box builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Parameterize the builder
            builder.setTitle("Change Scale");
            
            // Get the layout inflater
            LayoutInflater inflater = this.getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflater.inflate(R.layout.dialog_scale, null))
            // Add action buttons
               .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
    	            	EditText scaleText = (EditText)((AlertDialog) dialog).findViewById(R.id.editTextScale);
    	            	
    	            	if (!scaleText.getText().toString().equals("")) {
    	            		mapScale = Integer.parseInt(scaleText.getText().toString());
    	            	}
                   }
               })
               .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       
                   }
               }); 
            
            // Create the dialog box and show it
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
	 @Override
	 public void onWindowFocusChanged(boolean hasFocus) {
	    super.onWindowFocusChanged(hasFocus);
		width = drawingView.getWidth();
		height = drawingView.getHeight();
		if (drawmid) {
			drawingView.onMove(width/2, height/2);
			drawmid = false;
		}
	 }
	 
	 private void reset() {
     	drawingView.clear();
     	isNew = true;
     	drawmid = true;
	 }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
        if(requestCode == SELECT_COLOR && resultCode == Activity.RESULT_OK) {
            // This is a color response
            pencilColor = data.getIntExtra(ColorSelectActivity.COLOR, Color.BLACK);
            drawingView.setCurrentPaintColor(pencilColor);
        }
	}
	
	/**
     * Handle a Color button press
     */
    public void onColor(View view) {
        // Get a color
        Intent intent = new Intent(this, ColorSelectActivity.class);
        startActivityForResult(intent, SELECT_COLOR);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    
	private void setUI() {
		TextView textLatitude = (TextView)findViewById(R.id.textLatitude);
		TextView textLongitude = (TextView)findViewById(R.id.textLongitude);
		
		if (!valid) {
			textLatitude.setText("");
			textLongitude.setText("");
		} else {
			textLatitude.setText(Double.toString(latitude));
			textLongitude.setText(Double.toString(longitude));
		}
		
		if (width > 0 && height > 0) {
			double dLat = (latitude - baseLat) * mapScale + drawingView.getHeight()/2;
			double dLong = (longitude - baseLong) * mapScale + drawingView.getWidth()/2;
			
			drawingView.onMove((float)dLong, (float)dLat);
		}
    }
	
	private void registerListeners() {
		unregisterListeners();
        // Create a Criteria object
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(false);
        
        String bestAvailable = locationManager.getBestProvider(criteria, true);
        
        if(bestAvailable != null) {
            locationManager.requestLocationUpdates(bestAvailable, 500, 1, activeListener);
            TextView viewProvider = (TextView)findViewById(R.id.textProvider);
            viewProvider.setText(bestAvailable);
            Location location = locationManager.getLastKnownLocation(bestAvailable);
            onLocation(location);
        }
    }

	private void unregisterListeners() {
		locationManager.removeUpdates(activeListener);
    }
	
    private void onLocation(Location location) {
    	if(location == null) {
            return;
        }
        
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        valid = true;
        
        if (isNew) {
        	baseLat = latitude;
        	baseLong = longitude;
        	isNew = false;
        }

        setUI();
    }

	
    /* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
        unregisterListeners();
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        registerListeners();
	}
	
	private class ActiveListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			onLocation(location);
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			registerListeners();
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

        
    };
    
	

}
