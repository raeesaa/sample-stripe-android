package com.demo.stripeexample;

import java.util.HashMap;
import java.util.Map;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class OnlinePaymentActivity extends Activity implements OnClickListener {

	private EditText edit_card_number;
	private Spinner spinner_exp_month;
	private Spinner spinner_exp_year;
	private EditText edit_cvc_number;
	private Button btn_make_payment;

	private ProgressDialog progress_creating_token;

	private static final String PUBLISHABLE_KEY = "YOUR_TEST_PUBLISHABLE_KEY";
	private static final String API_KEY = "YOUR_TEST_SECRET_KEY";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card_details);

		edit_card_number = (EditText) findViewById(R.id.edit_card_number);
		spinner_exp_month = (Spinner) findViewById(R.id.spinner_exp_month);
		spinner_exp_year = (Spinner) findViewById(R.id.spinner_exp_year);
		edit_cvc_number = (EditText) findViewById(R.id.edit_cvc_number);
		btn_make_payment = (Button) findViewById(R.id.btn_make_payment);

		btn_make_payment.setOnClickListener(this);

		progress_creating_token = new ProgressDialog(this);
		progress_creating_token.setMessage("Creating Token...");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		stopProgress();
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.btn_make_payment) {
			if (isDataComplete()) {
				String cardNumber = edit_card_number.getText().toString();
				int cardExpMonth = Integer.parseInt(spinner_exp_month
						.getSelectedItem().toString());
				int cardExpYear = Integer.parseInt(spinner_exp_year
						.getSelectedItem().toString());
				String cardCVC = edit_cvc_number.getText().toString();

				try {
					Stripe stripe = new Stripe(PUBLISHABLE_KEY);

					// Create Card instance containing customer's payment
					// information obtained
					Card card = new Card(cardNumber, cardExpMonth, cardExpYear,
							cardCVC);

					// Check if card is valid. If valid, create token
					if (card.validateCard()) {

						startProgress();
						stripe.createToken(card, new TokenCallback() {

							@Override
							public void onSuccess(Token token) {
								stopProgress();
								
								Toast.makeText(OnlinePaymentActivity.this, "Token created successfully!", Toast.LENGTH_SHORT).show();
								chargeCustomer(token);

							}

							@Override
							public void onError(Exception error) {
								stopProgress();
								showAlert("Validation Error",
										error.getLocalizedMessage());

								Log.e("Error in creating token.",
										error.toString());
							}
						});
					} else {
						showAlert("Invalid Details",
								"Card details are invalid. Enter valid data");
						edit_card_number.setText(null);
						edit_cvc_number.setText(null);
						spinner_exp_month.setSelection(0);
						spinner_exp_year.setSelection(0);
					}

				} catch (AuthenticationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	public void chargeCustomer(Token token) {
		final Map<String, Object> chargeParams = new HashMap<String, Object>();
		chargeParams.put("amount", 400);
		chargeParams.put("currency", "usd");
		chargeParams.put("card", token.getId()); // obtained with Stripe.js

		new AsyncTask<Void, Void, Void>() {

			Charge charge;
			
			@Override
			protected Void doInBackground(Void... params) {
				try {
					com.stripe.Stripe.apiKey = API_KEY;
					charge = Charge.create(chargeParams);
					
					Log.i("IsCharged", charge.getCreated().toString());
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					showAlert("Exception while charging the card!",
							e.getLocalizedMessage());
				}
				return null;
			}
			
			protected void onPostExecute(Void result) {
				
				Toast.makeText(OnlinePaymentActivity.this, "Card Charged : " + charge.getCreated() + "\nPaid : " +charge.getPaid(), Toast.LENGTH_LONG).show();
			};

		}.execute();

	}

	private boolean isDataComplete() {
		boolean isDataComplete = true;

		if (edit_card_number.getText().length() == 0) {
			isDataComplete = false;
			edit_card_number.setError("Enter Card Number");
		}

		if (edit_cvc_number.getText().length() == 0) {
			isDataComplete = false;
			edit_cvc_number.setError("Enter CVC Number");
		}

		if (spinner_exp_month.getSelectedItemPosition() == 0
				|| spinner_exp_year.getSelectedItemPosition() == 0) {
			isDataComplete = false;

			if (spinner_exp_month.getSelectedItemPosition() == 0
					&& spinner_exp_year.getSelectedItemPosition() == 0) {
				showAlert("Incomplete Data!", "Enter expiry month and year");
			} else if (spinner_exp_month.getSelectedItemPosition() == 0) {
				showAlert("Incomplete Data!", "Enter expiry month");
			} else if (spinner_exp_year.getSelectedItemPosition() == 0) {
				showAlert("Incomplete Data!", "Enter Expiry Year");
			}
		}

		return isDataComplete;

	}

	private void showAlert(String title, String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// dialog.dismiss();

			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public void startProgress() {
		progress_creating_token.show();
	}

	public void stopProgress() {
		if (progress_creating_token.isShowing()) {
			progress_creating_token.dismiss();
		}
	}

}
