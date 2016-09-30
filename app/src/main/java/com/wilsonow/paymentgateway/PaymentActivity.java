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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.onesignal.OneSignal;
import com.wilsonow.paymentgateway.fragments.BillingFragment;
import com.wilsonow.paymentgateway.fragments.PaymentFragment;
import com.wilsonow.paymentgateway.fragments.ShipmentFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class PaymentActivity extends AppCompatActivity implements PaymentFragment.PaymentInterface, BillingFragment.BillingInterface, ShipmentFragment.ShipmentInterface {

    private static String TAG = "PaymentActivity";
    private String devModelStr, deviceBtAdd;
    private String billNameStr, billContactStr, billCountryStr, billStateStr, billCityStr, billZipStr, billStreetStr;
    private String shipNameStr, shipContactStr, shipCountryStr, shipStateStr, shipCityStr, shipZipStr, shipStreetStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize OneSignal
        //  - Call inFocusDisplaying with None to disable OneSignal's in app AlertBox.
        //  - If want to change how a notification is displayed in the notification shade
        //    or process a silent notification when your app isn't running see our Background
        //    Data and Notification Overriding guide.
        OneSignalNRHandler osNRhandler = new OneSignalNRHandler(this);
        OneSignal.startInit(this)
            .setNotificationReceivedHandler(osNRhandler)
            .setNotificationOpenedHandler(osNRhandler)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.None)
            .init();
        devModelStr = getIntent().getStringExtra("devModel");

        // Obtain user's location
        determineRegion();

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
        //sendFCMDataMessage();
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

    private void determineRegion() {
        // Function to determine user's region
        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String countryISO = mTelephonyManager.getNetworkCountryIso() + "countryISO";

        // If no telephony services is available (no SIM card), set the default country to US.
        countryISO = countryISO.equals("") ? countryISO : "SG";
        try {

            BufferedReader bufferedReader;
            InputStreamReader inputStreamReader = new InputStreamReader(getAssets().open("country_list.txt"));
            bufferedReader = new BufferedReader(inputStreamReader);
            String readLine;

            while ( (readLine = bufferedReader.readLine()) != null) {
                String userCountry = readLine.substring(3, 5);
                /*Log.i(TAG, "country: " + userCountry);
                Log.i(TAG, "continent: " + readLine.substring(0, 2));*/
                if ( userCountry.equalsIgnoreCase(countryISO) ) {
                    // If this country exists (is valid)
                    switch ( userCountry.toUpperCase() ) {
                        case "SG":
                            OneSignal.sendTag("Region", "ASIA");
                            Log.i(TAG, "country found!");
                            break;
                    }
                    break; // exit while loop
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendFCMDataMessage() {

            new AsyncTask<Void, Void, String>() {

                protected String doInBackground(Void... params) {
                    try {
                        URL url = new URL("https://fcm.googleapis.com/fcm/send");
                        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                        httpConn.setRequestProperty("Content-Type", "application/json");
                        httpConn.setRequestProperty("Authorization", "key=AIzaSyDZB86_E1LL5MzmcKk43b9La5M1aC2_Nxs");
                        httpConn.setReadTimeout(15000);
                        httpConn.setConnectTimeout(15000);
                        httpConn.setRequestMethod("POST");
                        httpConn.setDoOutput(true);
                        httpConn.connect();

                        JSONObject jObjData = new JSONObject();
                        JSONObject jObjContent = new JSONObject();
                        jObjContent.put("content1", "content1~~");
                        jObjContent.put("content2", "content2~~");
                        jObjData.put("data", jObjContent);
                        //Log.i(TAG, jObjData.toString());

                        JSONObject jObjNotification = new JSONObject();
                        JSONObject jObjNContent = new JSONObject();
                        jObjNContent.put("body", "5 to 1");
                        jObjNContent.put("title", "Portugal vs. Denmark");
                        jObjNotification.put("notification", jObjNContent);
                        Log.i(TAG, jObjNotification.toString());
                        /*
                        { "notification": {
                            "title": "Portugal vs. Denmark",
                                    "body": "5 to 1"
                        }*/

                        OutputStream os = httpConn.getOutputStream();
                        BufferedWriter bWriter = new BufferedWriter(
                                new OutputStreamWriter(os, "UTF-8"));
                        bWriter.write(URLEncoder.encode(jObjNotification.toString(), "UTF-8"));
                        bWriter.flush();
                        bWriter.close();
                        os.close();

                        int response = httpConn.getResponseCode();
                        Log.i(TAG, "response: " + response);
                        //httpConn.disconnect();

                        return "Success";
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
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


            /*{ "data": {
                "score": "5x1",
                        "time": "15:10"
            },
                "to" : "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1..."
            }*/

            /*Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("bluetooth_add", deviceBtAdd)*/

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
