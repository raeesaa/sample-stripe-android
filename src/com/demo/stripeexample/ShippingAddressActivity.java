package com.demo.stripeexample;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ShippingAddressActivity extends Activity implements
		OnClickListener {

	private EditText edit_addr_1;
	private EditText edit_addr_2;
	private EditText edit_city;
	private EditText edit_zip_code;
	private EditText edit_state;
	private EditText edit_country;
	private Button btn_proceed_to_payment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shipping_addr);

		edit_addr_1 = (EditText) findViewById(R.id.edit_addr_1);
		edit_addr_2 = (EditText) findViewById(R.id.edit_addr_2);
		edit_city = (EditText) findViewById(R.id.edit_city);
		edit_zip_code = (EditText) findViewById(R.id.edit_zip_code);
		edit_state = (EditText) findViewById(R.id.edit_state);
		edit_country = (EditText) findViewById(R.id.edit_country);
		btn_proceed_to_payment = (Button) findViewById(R.id.btn_proceed_to_payment);

		btn_proceed_to_payment.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.btn_proceed_to_payment) {
			if(validateData())
			{
				// TODO : Save shipping details to server
				// Once data is saved to server call OnlinePaymentActivity

				Intent intent = new Intent(ShippingAddressActivity.this,
						OnlinePaymentActivity.class);
				startActivity(intent);
			}
		}

	}

	private boolean validateData()
	{
		boolean isDataValid = true;
		
		if(edit_addr_1.getText().length() == 0)
		{
			isDataValid = false;
			edit_addr_1.setError("Enter Address");
		}
		
		if(edit_city.getText().length() == 0)
		{
			isDataValid = false;
			edit_city.setError("Enter City");
		}
		
		if(edit_country.getText().length() == 0)
		{
			isDataValid = false;
			edit_country.setError("Enter Country");
		}
		
		if(edit_state.getText().length() == 0)
		{
			isDataValid = false;
			edit_state.setError("Enter State");
		}
		
		if(edit_zip_code.getText().length() == 0)
		{
			isDataValid = false;
			edit_zip_code.setError("Enter Zip Code");
		}
		
		return isDataValid;
	}
}
