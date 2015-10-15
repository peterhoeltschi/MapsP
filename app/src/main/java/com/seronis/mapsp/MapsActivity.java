package com.seronis.mapsp;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;

import java.util.List;

public class MapsActivity extends Activity {

    private GoogleMap map;
    private StreetViewPanorama mSvp;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        LatLng ZURICH = new LatLng(47.377455, 8.536715);
        mSvp = ((StreetViewPanoramaFragment)
                getFragmentManager().findFragmentById(R.id.streetviewpanorama))
                .getStreetViewPanorama();
        mSvp.setPosition(ZURICH);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        if (map != null) {
            marker = map.addMarker(new MarkerOptions().position(ZURICH).title("Züri").snippet("Züri isch cool")
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_launcher)));
            marker.setRotation(15);
            // Move the camera instantly to Zurich with a zoom of 10.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(ZURICH,11));
            // Zoom in, animating the camera.
            //map.animateCamera(CameraUpdateFactory.zoomTo(8), 2000, null);

        }

        ((Button)findViewById(R.id.button1)).setOnClickListener(
                new View.OnClickListener(){
                    @Override public void onClick(View arg0) {
                        Editable address = ((EditText) findViewById(R.id.text1)).getText();
                        LatLng latLng = getLocationFromAddress(address.toString());
                        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mSvp.setPosition(latLng);
                    }
                }
        );


        mSvp.setOnStreetViewPanoramaChangeListener(
                new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
                    @Override
                    public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {
                        Log.i("SVP", "SVP clicked");
                        try {
                            LatLng latLng = streetViewPanoramaLocation.position;
                            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            String address = getAddressFromLocation(latLng);
                            Log.i("Address: ", address);
                            ((EditText) findViewById(R.id.text1)).setText(address, TextView.BufferType.EDITABLE);
                        }catch(Exception e){
                            Log.e("Error SVP Location",e.toString());
                        }
                     }
                }
        );

        map.setOnMapClickListener(
                new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        Log.i("Map", "Map clicked");
                        marker.setPosition(latLng);
                        mSvp.setPosition(latLng);
                        String address = getAddressFromLocation(latLng);
                        Log.i("Address: ", address);
                        ((EditText) findViewById(R.id.text1)).setText(address, TextView.BufferType.EDITABLE);
                    }
                }
        );
    }

    public String getAddressFromLocation(LatLng latLng){
        String address = null;
        Geocoder coder = new Geocoder(this);
        //List<Address> address;
        Log.i("Adress","enter meth");
        try{
            List<Address> addressL;
            addressL = coder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if(addressL == null){
                Log.i("Address","null");
                return "No address found";
            }
            Address adr = addressL.get(0);
            address = adr.getAddressLine(0);
            String subAdminArea = adr.getSubAdminArea();
            String adminArea = adr.getAdminArea();
            if (subAdminArea != null){
                address = address+", "+subAdminArea;
            }else if(adminArea != null){
                address = address+", "+adminArea;
            }else{
                address = address+", NO ADMIN ARE";
            }


            Log.i("Address fields: ",address);
        }catch(Exception e) {
            Log.e("Error Address", e.toString());
        }

        return address;
    }



    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng latLng = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
           latLng = new LatLng(location.getLatitude(),location.getLongitude());
        } catch(Exception e){
            Log.e("Error",e.toString());
        }
        return latLng;
    }



  //  private void setUpStreetViewPanoramaIfNeeded(LatLng latLng) {
    //private void setUpStreetViewPanoramaIfNeeded(LatLng ZURICH, Bundle savedInstanceState) {
        //if (mSvp == null) {
   //         mSvp = ((StreetViewPanoramaFragment)
  //                  getFragmentManager().findFragmentById(R.id.streetviewpanorama))
    //                .getStreetViewPanorama();

      //      if (mSvp != null) {
      //          if (savedInstanceState == null) {
  //                  mSvp.setPosition(latLng);
      //          }
      //      }
       // }
 //   }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
    }

  /*  *//**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     *//*
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    *//**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     *//*
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }*/
}
