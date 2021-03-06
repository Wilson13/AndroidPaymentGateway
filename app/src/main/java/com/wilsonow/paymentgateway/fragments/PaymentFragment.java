package com.wilsonow.paymentgateway.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.exception.AuthenticationException;
import com.wilsonow.paymentgateway.R;

public class PaymentFragment extends Fragment implements View.OnClickListener {

    public interface PaymentInterface{
        void onPaymentInfoPass();
    }

    private PaymentInterface paymentInterface;
    private Card card;
    private String cardNumber, cardCVC;

    private int cardExpMonth, cardExpYear;
    private int errorColor, whiteColor;

    private EditText cardNumberEt;
    private EditText cardExpEt;
    private EditText cardCvcEt;
    private Button submitBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_payment, container, false);

        cardNumberEt = (EditText) mView.findViewById(R.id.et_card_number);
        cardExpEt = (EditText) mView.findViewById(R.id.et_card_exp);
        cardCvcEt = (EditText) mView.findViewById(R.id.et_zip);
        submitBtn = (Button) mView.findViewById(R.id.btn_submit);

        errorColor = ContextCompat.getColor(getActivity().getBaseContext(), R.color.colorError);
        whiteColor = ContextCompat.getColor(getActivity().getBaseContext(), R.color.white);

        submitBtn.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        paymentInterface = (PaymentInterface) context;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:

                if (!cardExpEt.getText().toString().equals("") && cardExpEt.getText().toString().length() == 5) {
                    try {
                        cardExpMonth = Integer.parseInt(cardExpEt.getText().toString().substring(0, 2));
                        cardExpYear = Integer.parseInt("20" + cardExpEt.getText().toString().substring(3, 5));
                    }catch (Exception e) {
                        cardNumberEt.setBackgroundColor(ContextCompat.getColor(getActivity().getBaseContext(), R.color.colorError));
                    }
                }
                cardNumber = cardNumberEt.getText().toString().replace(" ", "-");
                /*cardNumber = cardNumberEt.getText().toString().substring(0, 4) +"-"
                                + cardNumberEt.getText().toString().substring(4, 8) + "-"
                                + cardNumberEt.getText().toString().substring(8, 12) + "-"
                                + cardNumberEt.getText().toString().substring(12, 16);*/
                cardCVC = cardCvcEt.getText().toString();

                /*Toast.makeText(getActivity().getBaseContext(),
                        "cardNumber:" + cardNumber,
                        Toast.LENGTH_LONG
                ).show();*/

                card = new Card(
                        cardNumber,
                        cardExpMonth,
                        cardExpYear,
                        cardCVC
                );

                // Validate card input, if invalid warn user with pink-colored EditText.
                if (!card.validateNumber()) {
                    cardNumberEt.setBackgroundColor(errorColor);
                } else {
                    cardNumberEt.setBackgroundColor(whiteColor);
                }

                if (!card.validateExpiryDate()) {
                    cardExpEt.setBackgroundColor(errorColor);
                } else {
                    cardExpEt.setBackgroundColor(whiteColor);
                }

                if (!card.validateCVC()) {
                    cardCvcEt.setBackgroundColor(errorColor);
                } else {
                    cardCvcEt.setBackgroundColor(whiteColor);
                }

                if (card.validateCard()) {

                    cardNumberEt.setEnabled(false);
                    cardExpEt.setEnabled(false);
                    cardCvcEt.setEnabled(false);

                    submitBtn.setVisibility(View.GONE);
                    final View v = getActivity().findViewById(R.id.tv_verifying);
                    v.setVisibility(View.VISIBLE);

                    final PercentRelativeLayout.LayoutParams params = (PercentRelativeLayout.LayoutParams) v.getLayoutParams();
                    final PercentLayoutHelper.PercentLayoutInfo info = params.getPercentLayoutInfo();
                    final float mStartWidth = info.widthPercent;

                    Animation a = new Animation()
                    {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            info.widthPercent = mStartWidth - ((mStartWidth/2) * interpolatedTime);
                            v.setLayoutParams(params);
                            v.requestLayout();
                        }

                        @Override
                        public boolean willChangeBounds() {
                            return true;
                        }
                    };
                    a.setDuration(1000);
                    a.setFillEnabled(true);
                    a.setFillAfter(true);
                    v.startAnimation(a);

                    Stripe stripe = null;
                    try {
                        stripe = new Stripe("pk_test_6pRNASCoBOKtIshFeQd4XMUh");
                        stripe.createToken(
                                card,
                                new TokenCallback() {
                                    public void onSuccess(Token token) {
                                        AlertDialog.Builder dialogBuilder  = new AlertDialog.Builder(getActivity(), R.style.dialogBackground)
                                                .setTitle("Payment Succeed!")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // continue with delete
                                                    }
                                                });

                                        dialogBuilder.show();
                                        paymentInterface.onPaymentInfoPass();
                                    }
                                    public void onError(Exception error) {
                                        // Show localized error message
                                        Toast.makeText(getActivity(),
                                                error.toString(),
                                                Toast.LENGTH_LONG
                                        ).show();

                                        cardNumberEt.setEnabled(true);
                                        cardExpEt.setEnabled(true);
                                        cardCvcEt.setEnabled(true);
                                    }
                                }
                        );
                    } catch (AuthenticationException e) {
                        e.printStackTrace();
                    }


                }

                break;
        }
    }
}