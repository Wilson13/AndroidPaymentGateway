package com.wilsonow.paymentgateway;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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

        init();
    }

    public void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Payment");

        setSupportActionBar(toolbar);
        findViewById(R.id.btn_submit).setOnClickListener(this);

        cardNumberEt = (EditText) findViewById(R.id.et_card_number);
        cardExpEt = (EditText) findViewById(R.id.et_card_exp);
        cardCvcEt = (EditText) findViewById(R.id.et_card_cvc);
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

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
            case R.id.btn_submit:

                if (!cardExpEt.getText().toString().equals("") && cardExpEt.getText().toString().length() == 5) {
                    try {
                        cardExpMonth = Integer.parseInt(cardExpEt.getText().toString().substring(0, 2));
                        cardExpYear = Integer.parseInt("20" + cardExpEt.getText().toString().substring(3, 5));
                    }catch (Exception e) {
                        findViewById(R.id.v_1).setVisibility(View.VISIBLE);
                        findViewById(R.id.v_2).setVisibility(View.VISIBLE);
                        findViewById(R.id.et_card_number).setBackgroundColor(ContextCompat.getColor(this, R.color.colorError));
                    }
                 }
                cardNumber = cardNumberEt.getText().toString().replace(" ", "-");
                /*cardNumber = cardNumberEt.getText().toString().substring(0, 4) +"-"
                                + cardNumberEt.getText().toString().substring(4, 8) + "-"
                                + cardNumberEt.getText().toString().substring(8, 12) + "-"
                                + cardNumberEt.getText().toString().substring(12, 16);*/
                cardCVC = cardCvcEt.getText().toString();

                Toast.makeText(getBaseContext(),
                        "cardNumber:" + cardNumber,
                        Toast.LENGTH_LONG
                ).show();

                card = new Card(
                        cardNumber,
                        cardExpMonth,
                        cardExpYear,
                        cardCVC
                );
                //card = new Card("4242-4242-4242-4242", 12, 2017, "123");

                // Validate card input, if invalid warn user with pink-colored EditText.
                if (!card.validateNumber()) {
                    //findViewById(R.id.v_1).setVisibility(View.VISIBLE);
                    //findViewById(R.id.v_2).setVisibility(View.VISIBLE);
                    findViewById(R.id.et_card_number).setBackgroundColor(ContextCompat.getColor(this, R.color.colorError));
                } else {
                    findViewById(R.id.v_1).setVisibility(View.INVISIBLE);
                    findViewById(R.id.v_2).setVisibility(View.INVISIBLE);
                    findViewById(R.id.et_card_number).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                }

                if (!card.validateExpiryDate()) {
                    //findViewById(R.id.v_3).setVisibility(View.VISIBLE);
                    //findViewById(R.id.v_4).setVisibility(View.VISIBLE);
                    findViewById(R.id.et_card_exp).setBackgroundColor(ContextCompat.getColor(this, R.color.colorError));
                } else {
                    findViewById(R.id.v_3).setVisibility(View.INVISIBLE);
                    findViewById(R.id.v_4).setVisibility(View.INVISIBLE);
                    findViewById(R.id.et_card_exp).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                }

                if (!card.validateCVC()) {
                    //findViewById(R.id.v_5).setVisibility(View.VISIBLE);
                    //findViewById(R.id.v_6).setVisibility(View.VISIBLE);
                    findViewById(R.id.et_card_cvc).setBackgroundColor(ContextCompat.getColor(this, R.color.colorError));
                } else {
                    findViewById(R.id.v_5).setVisibility(View.INVISIBLE);
                    findViewById(R.id.v_6).setVisibility(View.INVISIBLE);
                    findViewById(R.id.et_card_cvc).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                }

                if (card.validateCard()) {

                    /*findViewById(R.id.tv_card_number).setVisibility(View.GONE);
                    findViewById(R.id.prl_card_exp).setVisibility(View.GONE);
                    PercentRelativeLayout prlIvCard = (PercentRelativeLayout) findViewById(R.id.prl_iv_card);
                    findViewById(R.id.v_1).setVisibility(View.GONE);
                    findViewById(R.id.v_2).setVisibility(View.GONE);
                    findViewById(R.id.v_3).setVisibility(View.GONE);
                    findViewById(R.id.v_4).setVisibility(View.GONE);
                    findViewById(R.id.v_5).setVisibility(View.GONE);
                    findViewById(R.id.v_6).setVisibility(View.GONE);
                    cardNumberEt.setVisibility(View.GONE);
                    cardExpEt.setVisibility(View.GONE);
                    cardCvcEt.setVisibility(View.GONE);*/

                    /*findViewById(R.id.btn_submit).setBackgroundColor(ContextCompat.getColor(this,R.color.colorPurchased));*/

                    /*ValueAnimator anim = ValueAnimator.ofInt(findViewById(R.id.btn_submit).getMeasuredWidth(), 20);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int val = (Integer) valueAnimator.getAnimatedValue();
                            ViewGroup.LayoutParams layoutParams = findViewById(R.id.btn_submit).getLayoutParams();
                            layoutParams.width = val;
                            findViewById(R.id.btn_submit).setLayoutParams(layoutParams);
                        }
                    });
                    anim.setDuration(2000);
                    anim.start();*/

                    findViewById(R.id.btn_submit).setVisibility(View.GONE);
                    final View v = findViewById(R.id.tv_purchased);
                    v.setVisibility(View.VISIBLE);

                    final PercentRelativeLayout.LayoutParams params = (PercentRelativeLayout.LayoutParams) v.getLayoutParams();
                    final PercentLayoutHelper.PercentLayoutInfo info = params.getPercentLayoutInfo();
                    final float mStartWidth = info.widthPercent;

                    Animation a = new Animation()
                    {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
//                            v.getLayoutParams().height = interpolatedTime == 1
//                                    ? LayoutParams.WRAP_CONTENT
//                                    : (int)(targetHeight * interpolatedTime);
//                            v.requestLayout();
                            //int newWidth = mStartWidth;// - (int) ((mStartWidth/2) * interpolatedTime);
                            //int newWidth = mStartWidth + (int) ((500 - mStartWidth) * interpolatedTime);
                            //Log.i("Main", "newWidth: "+newWidth);
                            //v.getLayoutParams().width = newWidth;
                            //params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
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
                                        /*Toast.makeText(getBaseContext(),
                                                "Payment successful!",
                                                Toast.LENGTH_LONG
                                        ).show();*/
                                        AlertDialog.Builder dialogBuilder  = new AlertDialog.Builder(MainActivity.this, R.style.dialogBackground)
                                                .setTitle("Payment Succeed!")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // continue with delete
                                                    }
                                                });

                                        dialogBuilder.show();
                                    }
                                    public void onError(Exception error) {
                                        // Show localized error message
                                        Toast.makeText(getBaseContext(),
                                                error.toString(),
                                                Toast.LENGTH_LONG
                                        ).show();
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


