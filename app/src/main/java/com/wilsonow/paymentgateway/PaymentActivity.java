package com.wilsonow.paymentgateway;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.wilsonow.paymentgateway.fragments.BillingFragment;
import com.wilsonow.paymentgateway.fragments.PaymentFragment;
import com.wilsonow.paymentgateway.fragments.ShipmentFragment;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PaymentActivity extends AppCompatActivity implements PaymentFragment.PaymentInterface, BillingFragment.BillingInterface, ShipmentFragment.ShipmentInterface {

    private String devModelStr, deviceBtAdd;
    private String billNameStr, billContactStr, billCountryStr, billStateStr, billCityStr, billZipStr, billStreetStr;
    private String shipNameStr, shipContactStr, shipCountryStr, shipStateStr, shipCityStr, shipZipStr, shipStreetStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        devModelStr = getIntent().getStringExtra("devModel");
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            //PaymentFragment firstFragment = new PaymentFragment();
            BillingFragment firstFragment = new BillingFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("Shipment");
        //setSupportActionBar(toolbar);
    }

    @Override
    public void onResume(){
        super.onResume();

        //deviceBtAdd = mGaiaLink.getBluetoothAddress();
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // De Morgan’s Theorem (A && B)' = A' + B'
        //if (networkInfo != null && networkInfo.isConnected()) {
        if (networkInfo == null || !networkInfo.isConnected()) {
            // If user is not connected to the Internet
            //showMessageBackToMain(getResources().getString(R.string.update_internet_connection),getResources().getString(R.string.update_internet_connection_msg) );
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.update_internet_connection)+getResources().getString(R.string.update_internet_connection_msg));
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    PaymentActivity.this.finish();
                }
            });
            builder.show();
        }
    }

    @Override
    public void onBackPressed(){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void proceedPayment() {
        // Create fragment and give it an argument specifying the article it should show
        PaymentFragment paymentFragment = new PaymentFragment();
        Bundle args = new Bundle();
        paymentFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim, R.anim.back_enter_anim, R.anim.back_exit_anim);
        transaction.replace(R.id.fragment_container, paymentFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // Commit the transaction
    }

    private void proceedShipment() {
        ShipmentFragment shipmentFragment = new ShipmentFragment();
        Bundle args = new Bundle();
        shipmentFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim, R.anim.back_enter_anim, R.anim.back_exit_anim);
        transaction.replace(R.id.fragment_container, shipmentFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // Commit the transaction
    }

    @Override
    public void onBillingInfoPass(String data[], Boolean sameAdd) {

        if ( data.length == 14 && sameAdd ) {
            billNameStr = data[0];
            billContactStr = data[1];
            billCountryStr = data[2];
            billStateStr = data[3];
            billCityStr = data[4];
            billZipStr = data[5];
            billStreetStr = data[6];

            shipNameStr = data[7];
            shipContactStr = data[8];
            shipCountryStr = data[9];
            shipStateStr = data[10];
            shipCityStr = data[11];
            shipZipStr = data[12];
            shipStreetStr = data[13];

            // Proceed to payment fragment if addresses are the same
            proceedPayment();
            //Log.i("MainActivity", "Shipment info: " + data[0] + " " + data[1] + " " + data[2] + " " + data[3] + " " + data[4] + " " + data[5] + " " + data[6]);
        } else if ( data.length == 14 && !sameAdd) {
            billNameStr = data[0];
            billContactStr = data[1];
            billCountryStr = data[2];
            billStateStr = data[3];
            billCityStr = data[4];
            billZipStr = data[5];
            billStreetStr = data[6];

            // Proceed to shipment fragment if addresses are the not same
            proceedShipment();
        }
    }

    @Override
    public void onShipmentInfoPass(String[] data) {
        if ( data.length == 7 ) {
            shipNameStr = data[0];
            shipContactStr = data[1];
            shipCountryStr = data[2];
            shipStateStr = data[3];
            shipCityStr = data[4];
            shipZipStr = data[5];
            shipStreetStr = data[6];
            // Proceed to payment fragment if addresses are the same
            proceedPayment();
        }
    }

    @Override
    public void onPaymentInfoPass() {

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            // If user is connected to the Internet
            new AsyncTask<Void, Void, String>() {

                protected String doInBackground(Void... params) {
                    try {
                        URL url = new URL("http://iclear-digital.com/payment/insert.php");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout(15000);
                        conn.setConnectTimeout(15000);
                        conn.setRequestMethod("POST");
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.connect();

                        // POST variables to PHP file on server
                        Uri.Builder builder = new Uri.Builder()
                                .appendQueryParameter("bluetooth_add", deviceBtAdd)
                                .appendQueryParameter("bill_name", billNameStr)
                                .appendQueryParameter("bill_contact_no", billContactStr)
                                .appendQueryParameter("bill_country", billCountryStr)
                                .appendQueryParameter("bill_state", billStateStr)
                                .appendQueryParameter("bill_city", billCityStr)
                                .appendQueryParameter("bill_zip", billZipStr)
                                .appendQueryParameter("bill_street", billStreetStr)
                                .appendQueryParameter("ship_name", shipNameStr)
                                .appendQueryParameter("ship_contact_no", shipContactStr)
                                .appendQueryParameter("ship_country", shipCountryStr)
                                .appendQueryParameter("ship_state", shipStateStr)
                                .appendQueryParameter("ship_city", shipCityStr)
                                .appendQueryParameter("ship_zip", shipZipStr)
                                .appendQueryParameter("ship_street", shipStreetStr);

                        String query = builder.build().getEncodedQuery();

                        OutputStream os = conn.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(os, "UTF-8"));
                        writer.write(query);
                        writer.flush();
                        writer.close();
                        os.close();

                        int response = conn.getResponseCode();
                        Log.d("MainActivity", "The response is: " + response);

                        return "Success";
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
                @Override
                protected void onPostExecute(String result) {
                    if ( result.equals("Success")) {
                        Toast.makeText(PaymentActivity.this,
                                "Proceed to product upgrade!",
                                Toast.LENGTH_LONG
                        ).show();
                    } else {
                        Toast.makeText(PaymentActivity.this,
                                "Failed to connect to server, please contact technical support",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
            }.execute();

        } else {
            // If user is not connected to the Internet
            Toast.makeText(PaymentActivity.this,
                    "Failed to connect to server, please contact technical support",
                    Toast.LENGTH_LONG
            ).show();
        }

    }
}
