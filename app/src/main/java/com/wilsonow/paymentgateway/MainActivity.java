package com.wilsonow.paymentgateway;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.stripe.android.model.Card;
import com.wilsonow.paymentgateway.fragments.PaymentFragment;
import com.wilsonow.paymentgateway.fragments.ShipmentFragment;

public class MainActivity extends AppCompatActivity {

    private Card card;
    private String cardNumber, cardCVC;
    private int cardExpMonth, cardExpYear;

    private EditText cardNumberEt;
    private EditText cardExpEt;
    private EditText cardCvcEt;

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

    public void proceedPayment() {
        // Create fragment and give it an argument specifying the article it should show
        PaymentFragment newFragment = new PaymentFragment();
        Bundle args = new Bundle();
        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }
}


