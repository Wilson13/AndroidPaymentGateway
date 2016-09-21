package com.wilsonow.paymentgateway;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.stripe.android.compat.AsyncTask;
import com.stripe.android.model.Card;
import com.wilsonow.paymentgateway.fragments.PaymentFragment;
import com.wilsonow.paymentgateway.fragments.ShipmentFragment;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements ShipmentFragment.ShipmentInterface, PaymentFragment.PaymentInterface{

    private Card card;
    private int cardExpMonth, cardExpYear;
    private EditText cardNumberEt ,cardExpEt, cardCvcEt;
    private String cardNumber, cardCVC, nameStr, contactStr, countryStr, stateStr, cityStr, zipStr, streetStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            ShipmentFragment firstFragment = new ShipmentFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Shipment");
        setSupportActionBar(toolbar);
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
        PaymentFragment newFragment = new PaymentFragment();
        Bundle args = new Bundle();
        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim, R.anim.back_enter_anim, R.anim.back_exit_anim);
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // Commit the transaction
    }

    @Override
    public void onShipmentInfoPass(String data[]) {
        nameStr = data[0];
        contactStr = data[1];
        countryStr = data[2];
        stateStr = data[3];
        cityStr = data[4];
        zipStr = data[5];
        streetStr = data[6];
        Log.i("MainActivity", "Shipment info: " + data[0] + " " + data[1] + " " + data[2] + " " + data[3] + " " + data[4] + " " + data[5] + " " + data[6]);
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

                        Uri.Builder builder = new Uri.Builder()
                                .appendQueryParameter("name", nameStr)
                                .appendQueryParameter("contact_no", contactStr);
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
                        Toast.makeText(MainActivity.this,
                                "Proceed to product upgrade!",
                                Toast.LENGTH_LONG
                        ).show();
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Failed to connect to server, please contact technical support",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
            }.execute();

        } else {
            // If user is not connected to the Internet
            Toast.makeText(MainActivity.this,
                    "Failed to connect to server, please contact technical support",
                    Toast.LENGTH_LONG
            ).show();
        }

    }
}


