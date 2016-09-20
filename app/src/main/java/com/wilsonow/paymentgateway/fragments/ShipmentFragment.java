package com.wilsonow.paymentgateway.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.stripe.android.model.Card;
import com.wilsonow.paymentgateway.MainActivity;
import com.wilsonow.paymentgateway.R;

public class ShipmentFragment extends Fragment implements View.OnClickListener {

    private Card card;
    private String cardNumber, cardCVC;

    private int country, cardExpYear;
    private int errorColor, whiteColor;

    private EditText countryEt, stateEt, cityEt, zipEt, streetEt;
    private Button proceedBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_shipment, container, false);

        countryEt = (EditText) mView.findViewById(R.id.et_country_add);
        stateEt = (EditText) mView.findViewById(R.id.et_state_add);
        cityEt = (EditText) mView.findViewById(R.id.et_city_add);
        zipEt = (EditText) mView.findViewById(R.id.et_zip_add);
        streetEt = (EditText) mView.findViewById(R.id.et_street_add);
        proceedBtn = (Button) mView.findViewById(R.id.btn_proceed);

        errorColor = ContextCompat.getColor(getActivity().getBaseContext(), R.color.colorError);
        whiteColor = ContextCompat.getColor(getActivity().getBaseContext(), R.color.white);

        proceedBtn.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        //toolbar.setNavigationIcon(android.ic_toolbar_arrow);
        /*toolbar.setNavigationOnClickListener(
                /*new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(AndroidToolbarExample.this, "clicking the toolbar!", Toast.LENGTH_SHORT).show();
                    }
                }

        );*/
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_proceed:

                String countryStr= countryEt.getText().toString();
                String stateStr = stateEt.getText().toString();
                String cityStr = cityEt.getText().toString();
                String zipStr = zipEt.getText().toString();
                String streetStr = streetEt.getText().toString();

                if ( countryStr.equals("") )
                    countryEt.setBackgroundColor(errorColor);
                else
                    countryEt.setBackgroundColor(whiteColor);

                if ( stateStr.equals("") )
                    stateEt.setBackgroundColor(errorColor);
                else
                    stateEt.setBackgroundColor(whiteColor);

                if ( cityStr.equals("") )
                    cityEt.setBackgroundColor(errorColor);
                else
                    cityEt.setBackgroundColor(whiteColor);

                if ( zipStr.equals("") )
                    zipEt.setBackgroundColor(errorColor);
                else
                    zipEt.setBackgroundColor(whiteColor);

                if ( streetStr.equals("") )
                    streetEt.setBackgroundColor(errorColor);
                else
                    streetEt.setBackgroundColor(whiteColor);

                if ( !countryStr.equals("") && !stateStr.equals("") && !cityStr.equals("") && !zipStr.equals("") &&!streetStr.equals("") ) {
                    ((MainActivity)getActivity()).proceedPayment();
                }
                break;
        }
    }
}


