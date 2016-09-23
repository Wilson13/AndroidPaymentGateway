package com.wilsonow.paymentgateway.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.bitwave.developer.uclearhub2.R;

public class BillingFragment extends Fragment implements View.OnClickListener {

    public interface BillingInterface {
        void onBillingInfoPass(String data[], Boolean sameAdd);
    }

    private int errorColor, whiteColor;
    private EditText countryEt, stateEt, cityEt, zipEt, streetEt, nameEt, contactEt;
    private Button proceedBtn;
    private CheckBox sameAddCb;
    private BillingInterface billInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_billing, container, false);

        nameEt = (EditText) mView.findViewById(R.id.et_customer_name);
        contactEt = (EditText) mView.findViewById(R.id.et_contact_no);
        countryEt = (EditText) mView.findViewById(R.id.et_country);
        stateEt = (EditText) mView.findViewById(R.id.et_state);
        cityEt = (EditText) mView.findViewById(R.id.et_city);
        zipEt = (EditText) mView.findViewById(R.id.et_zip);
        streetEt = (EditText) mView.findViewById(R.id.et_street);
        proceedBtn = (Button) mView.findViewById(R.id.btn_proceed);
        sameAddCb = (CheckBox) mView.findViewById(R.id.cb_same_add);

        nameEt.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        contactEt.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        countryEt.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        stateEt.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        cityEt.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        zipEt.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        streetEt.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        errorColor = ContextCompat.getColor(getActivity().getBaseContext(), R.color.colorError);
        whiteColor = ContextCompat.getColor(getActivity().getBaseContext(), R.color.white);

        proceedBtn.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        billInterface = (BillingInterface) context;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_proceed:

                String nameStr= nameEt.getText().toString();
                String contactStr = contactEt.getText().toString();
                String countryStr= countryEt.getText().toString();
                String stateStr = stateEt.getText().toString();
                String cityStr = cityEt.getText().toString();
                String zipStr = zipEt.getText().toString();
                String streetStr = streetEt.getText().toString();

                nameEt.setBackgroundColor(nameStr.equals("") ? (errorColor) : (whiteColor));

                if ( contactStr.equals("") )
                    contactEt.setBackgroundColor(errorColor);
                else
                    contactEt.setBackgroundColor(whiteColor);

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
                    String data[] = { nameStr, contactStr, countryStr, stateStr, cityStr, zipStr, streetStr,
                                      nameStr, contactStr, countryStr, stateStr, cityStr, zipStr, streetStr };

                    if ( sameAddCb.isChecked() ) {
                        // If shipping address is same as billing address, pass information to PaymentActivity.
                        billInterface.onBillingInfoPass(data, true);
                    } else {
                        billInterface.onBillingInfoPass(data, false);
                    }
                    //((PaymentActivity)getActivity()).proceedPayment();
                }
                break;
        }
    }
}


