package com.example.amado.traveltracker;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class

        MainActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapClickListener, MemoryDialogFragment.Listener,
        GoogleMap.OnMarkerDragListener, GoogleMap.OnInfoWindowClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private GoogleMap mMap;
    private final static String TAG = "MainActivity";
    private final static String MEMORY_TAG = "MemoryDialog";
    private GoogleApiClient mGoogleApiClient;
    private HashMap<String, Memory> mMemories = new HashMap<>();
    private MemoriesDataSource mDataSource;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDataSource = new MemoriesDataSource(this);
        getLoaderManager().initLoader(0,null,this);

        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mMap=mapFragment.getMap();
        onMapReady(mMap);

        addGoogleAPIClient();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setInfoWindowAdapter(new MarkerAdapter(getLayoutInflater(), mMemories));

    }


    @Override
    public void onMapClick(LatLng latLng) {
        Memory memory = new Memory();

        updateMemoryPosition(memory, latLng);
        MemoryDialogFragment.newInstance(memory).show(getFragmentManager(), MEMORY_TAG);

    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        final Memory memory = mMemories.get(marker.getId());
        String[] actions = {"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(memory.city+", "+memory.country)
                .setItems(actions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 1) {
                            marker.remove();
                            mDataSource.deleteMemory(memory);
                        }
                    }
                });

        builder.create().show();;

    }

    private void updateMemoryPosition(Memory memory, LatLng latLng) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> matches = null;

        try {
            matches= geocoder.getFromLocation(latLng.latitude, latLng.longitude,1);

        } catch (IOException e) {
            e.printStackTrace();

        }
        Address bestMatch = (matches.isEmpty()) ? null : matches.get(0);
        int maxLine = bestMatch.getMaxAddressLineIndex();

        memory.city = bestMatch.getAddressLine(maxLine - 1);
        memory.country = bestMatch.getAddressLine(maxLine);
        memory.latitude = latLng.latitude;
        memory.longitude= latLng.longitude;
    }

    private void addMarker(Memory memory) {
        Marker marker =mMap.addMarker(new MarkerOptions()
                .draggable(true)
                .position(new LatLng(memory.latitude, memory.longitude)));

        mMemories.put(marker.getId(), memory);
    }

    private void onFetchedMemories(List<Memory> memories) {
        for(Memory memory : memories){
           addMarker(memory);
         }
    }

    private void addGoogleAPIClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void OnSaveClicked(Memory memory) {
        addMarker(memory);
        mDataSource.createMemory(memory);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        double latitude =location.getLatitude();
        double longitude = location.getLongitude();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    public void OnCancelClicked(Memory memory) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Memory memory = mMemories.get(marker.getId());
        updateMemoryPosition(memory, marker.getPosition());
        mDataSource.updateMemory(memory);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {


    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MemoriesLoader(this, mDataSource);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        onFetchedMemories(mDataSource.cursorToMemories(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
