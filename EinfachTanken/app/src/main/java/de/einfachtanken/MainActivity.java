package de.einfachtanken;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ParserListener,
        FuelChooserDialogFragment.FuelChooserDialogListener
{

    private static final String TAG = "MainActivity";

    private static final String KEY_IN_RESOLUTION = "is_in_resolution";

    private String mFuelType = "";
    private String mFuelName = "";

    private String mDestination1 = "";
    private String mDestination2 = "";

    private ViewState mViewState = ViewState.LOADING;

    private Location mLocation;

    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Google API client.
     */
    private GoogleApiClient mGoogleApiClient;

    @InjectView(R.id.contentWrapper) ViewGroup vgContentWrapper;
    @InjectView(R.id.errorWrapper) ViewGroup vgErrorWrapper;
    @InjectView(R.id.loadingWrapper) ViewGroup vgLoadingWrapper;

    @InjectView(R.id.txtName1) TextView txtName1;
    @InjectView(R.id.txtPrice1) TextView txtPrice1;
    @InjectView(R.id.txtDistance1) TextView txtDistance1;
    @InjectView(R.id.txtName2) TextView txtName2;
    @InjectView(R.id.txtPrice2) TextView txtPrice2;
    @InjectView(R.id.txtDistance2) TextView txtDistance2;

    @InjectView(R.id.btnChooseFuel) Button btnChooseFuel;


    /**
     * Determines if the client is in a resolution state, and
     * waiting for resolution intent to return.
     */
    private boolean mIsInResolution;

    /**
     * Called when the activity is starting. Restores the activity state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        if (savedInstanceState != null) {
            mIsInResolution = savedInstanceState.getBoolean(KEY_IN_RESOLUTION, false);
            restoreInstanceState(savedInstanceState);
        }

        changeViewState(mViewState);

        KeyValueStorage.init(getApplicationContext());

        if(mFuelType.length() == 0) {
            mFuelType = KeyValueStorage.getString("fuelType", "e10");
            mFuelName = KeyValueStorage.getString("fuelName", "E10");
        }

        btnChooseFuel.setText(mFuelName);
    }

    /**
     * Called when the Activity is made visible.
     * A connection to Play Services need to be initiated as
     * soon as the activity is visible. Registers {@code ConnectionCallbacks}
     * and {@code OnConnectionFailedListener} on the
     * activities itself.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    // Optionally, add additional APIs and scopes if required.
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    /**
     * Called when activity gets invisible. Connection to Play Services needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    /**
     * Saves the resolution state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
        outState.putString("mFuelType", mFuelType);
        outState.putString("mFuelName", mFuelName);
        outState.putString("mStation1Name", txtName1.getText().toString());
        outState.putString("mStation1Price", txtPrice1.getText().toString());
        outState.putString("mStation1Distance", txtDistance1.getText().toString());
        outState.putString("mStation2Name", txtName2.getText().toString());
        outState.putString("mStation2Price", txtPrice2.getText().toString());
        outState.putString("mStation2Distance", txtDistance2.getText().toString());
        outState.putString("mDestination1", mDestination1);
        outState.putString("mDestination2", mDestination2);
        outState.putSerializable("mViewState", mViewState);
        if(mLocation != null) {
            outState.putDouble("lat", mLocation.getLatitude());
            outState.putDouble("lng", mLocation.getLongitude());
        }
    }

    private void restoreInstanceState(Bundle in) {
        if(in == null) return;
        mFuelType = in.getString("mFuelType");
        mFuelName = in.getString("mFuelName");
        txtName1.setText(in.getString("mStation1Name"));
        txtPrice1.setText(in.getString("mStation1Price"));
        txtDistance1.setText(in.getString("mStation1Distance"));
        txtName2.setText(in.getString("mStation2Name"));
        txtPrice2.setText(in.getString("mStation2Price"));
        txtDistance2.setText(in.getString("mStation2Distance"));
        mDestination1 = in.getString("mDestination1");
        mDestination2 = in.getString("mDestination2");
        mViewState = (ViewState)in.getSerializable("mViewState");
        if(in.containsKey("lat") && in.containsKey("lng")) {
            mLocation = new Location("");
            mLocation.setLatitude(in.getDouble("lat"));
            mLocation.setLongitude(in.getDouble("lng"));
        }
    }

    public void onClick_startNavigation1(View view) {
        if(mDestination1 == null || mDestination1.length() == 0) return;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + mDestination1));
        startActivity(intent);
    }

    public void onClick_startNavigation2(View view) {
        if(mDestination2 == null || mDestination2.length() == 0) return;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + mDestination2));
        startActivity(intent);
    }

    /**
     * Handles Google Play Services resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                retryConnecting();
                break;
        }
    }

    private void retryConnecting() {
        mIsInResolution = false;
        if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    public void onClick_btnChooseFuel(View view) {

        DialogFragment newFragment = new FuelChooserDialogFragment();
        newFragment.show(getFragmentManager(), "fuel_chooser");
    }

    @Override
    public void onFuelChosen(String fuelType, String fuelName) {
        mFuelType = fuelType;
        mFuelName = fuelName;

        btnChooseFuel.setText(fuelName);

        KeyValueStorage.setString("fuelType", fuelType);
        KeyValueStorage.setString("fuelName", fuelName);

        changeViewState(ViewState.LOADING);

        new GasStationDownloader(String.valueOf(mLocation.getLatitude()),
                String.valueOf(mLocation.getLongitude()),
                mFuelType,
                this);
    }

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "GoogleApiClient connected");

        requestLocation(false);
    }

    private void requestLocation(boolean fromErrorState) {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        if(mLocation != null && !fromErrorState) return;
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setFastestInterval(1000);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    public void onClick_reloadFromErrorState(View view) {
        requestLocation(true);
    }

    @Override
    public void onLocationChanged(Location location) {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mLocation = location;
        changeViewState(ViewState.LOADING);
        new GasStationDownloader(String.valueOf(mLocation.getLatitude()),
                String.valueOf(mLocation.getLongitude()),
                mFuelType,
                this);

    }

    @Override
    public void onGasStationsParsed(List<GasStation> stations) {
        changeViewState(ViewState.CONTENT);

        // Station 1: Cheapest
        // Sort by price
        Collections.sort(stations, new Comparator<GasStation>() {
            @Override
            public int compare(GasStation c1, GasStation c2) {
                return Double.compare(c1.price, c2.price);
            }
        });

        GasStation cheapestStation = stations.get(0);
        txtName1.setText(cheapestStation.brand);
        txtPrice1.setText(String.format("%.3f€", cheapestStation.price));
        txtDistance1.setText(String.format("%.2f km", cheapestStation.dist));
        mDestination1 = cheapestStation.lat + "," + cheapestStation.lng;

        // Station 2: Closest;
        // Sort by distance
        Collections.sort(stations, new Comparator<GasStation>() {
            @Override
            public int compare(GasStation c1, GasStation c2) {
                return Double.compare(c1.dist, c2.dist);
            }
        });

        GasStation closestStation = stations.get(0);
        txtName2.setText(closestStation.brand);
        txtPrice2.setText(String.format("%.3f€", closestStation.price));
        txtDistance2.setText(String.format("%.2f km", closestStation.dist));
        mDestination2 = closestStation.lat + "," + closestStation.lng;
    }

    @Override
    public void onGasStationParserFailed(Throwable e) {
        changeViewState(ViewState.ERROR);
        e.printStackTrace();
    }


    private enum ViewState {
        LOADING, ERROR, CONTENT
    }

    private void changeViewState(ViewState state) {
        mViewState = state;
        switch (state) {
            case LOADING:
                vgContentWrapper.setVisibility(View.GONE);
                vgErrorWrapper.setVisibility(View.GONE);
                vgLoadingWrapper.setVisibility(View.VISIBLE);
                break;
            case ERROR:
                vgContentWrapper.setVisibility(View.GONE);
                vgLoadingWrapper.setVisibility(View.GONE);
                vgErrorWrapper.setVisibility(View.VISIBLE);
                break;
            case CONTENT:
            default:
                vgErrorWrapper.setVisibility(View.GONE);
                vgLoadingWrapper.setVisibility(View.GONE);
                vgContentWrapper.setVisibility(View.VISIBLE);
                break;

        }
    }

    /**
     * Called when {@code mGoogleApiClient} connection is suspended.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
        retryConnecting();
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution
     * available.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // Show a localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(
                    result.getErrorCode(), this, 0, new OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            retryConnecting();
                        }
                    }).show();
            return;
        }

        // If there is an existing resolution error being displayed or a resolution
        // activity has started before, do nothing and wait for resolution
        // progress to be completed.
        if (mIsInResolution) {
            return;
        }
        mIsInResolution = true;
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
            retryConnecting();
        }
    }

}
