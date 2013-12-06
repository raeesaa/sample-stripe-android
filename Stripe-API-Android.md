# Stripe for Android

## Introduction :

[Stripe](https://stripe.com/) is a set of powerful API's that enable businesses to accept and manage online payments. Stripe provides libraries for most of the web and mobile platforms.

We will looking into stripe-android library. Stripe mobile library shoulders the burden of PCI compliance by helping developers avoid the need to send the card data directly to their server. Instead stripe libraries directly send data to their servers, which stripe can then convert into **tokens**. Developers can then charge these tokens at their server-side code.

## Android Integration :


### Basic Setup:
Stripe for android can be installed by following instructions given in official stripe site here: [Stripe Android Installation](https://stripe.com/docs/mobile/android#installation).

I am simply copying the installation instructions given in above link here:

    1. First download the stripe-android libraries.
    2. Be sure you've installed the Android SDK with API Level 17 and android-support-v4.
    3. Import the stripe folder into Eclipse.
    4. In your project settings, add the stripe project under the "Libraries" section of the "Android" category.

As we are going to integrate stripe with an android application, it is assumed that latest android SDK and android-support library are already installed(**Instruction 2** in above list) and the project to whom stripe android library is to be linked is already created.

**Instruction 1:**

Download stripe-android libraries from [link](https://github.com/stripe/stripe-android/archive/master.zip). The download link is also provided in first installation instruction in [Stripe Android Installation](https://stripe.com/docs/mobile/android#installation) page. 

**Instruction 3:**

'stripe' folder from downloaded .zip file can be imported into eclipse as follows:

      1.Open Eclipse
      2.Right click on project explorer 
      3.Click Import 
      4.Select Android in import window-> Existing Android Code into Workspace 
      5.Browse for 'stripe' folder in downloaded .zip file (extracted_zip_folder\stripe-android-master\stripe)
      6.Click Finish

**Instruction 4:**

Android project can be linked with stripe-android library as follows:

      1. Right click on project to link stripe with
      2. Select Properties
      3. Click on Android in property window
      4. In the Library section at bottom right of property window, click on Add
      5. Select 'stripe' from the list
      6. Click Ok

Apart from this basic setup, you also need to register at Stripe.com in order to obtain the secret key(API Key) and publishable key that we will be needing in the code. Once registered, you can go to the dashboard and obtain these keys from 'Account Settings' section of 'Your account'. There are two version of keys: Test and Live. Test secret and publishable key can be used for testing purposes and has to be replaced by Live secret and publishable key before publishing the app.


### Working with stripe-android library:

#### Creating Stripe Instance:

First one needs to create instance of `Stripe` class by passing publishable key as argument in its constructor.

   

    Stripe stripe = new Stripe("YOUR_TEST_PUBLISHABLE_KEY");

Remember to replace test publishable key with live before deploying app. Publishable key can be obtained from [account page](https://manage.stripe.com/account/apikeys).

#### Creating and Validating Card:

Stripe library provides an important class called `Card` which contains a bunch of useful helpers for validating card input on client-side before we try to create a charge(i.e. perform transaction).

The card instance can be created as follows:

    
    Card card = new Card(cardNumber,
                         cardExpMonth,
                         cardExpYear,
                         cardCVC);

where,<br />
 `cardNumber(String)` is 12 digit Credit/Debit Card number,<br />
      `cardExpMonth(int)` is expiry month of card,<br/>
      `cardExpYear(int)` is expiry year of card and<br/>
      `cardCVC(String)` is 3 digit card CVC.

An example,
    
    Card card = new Card("4242424242424242", 5, 14, "123");

Card number and other details that can be used for testing are provided here :  [Testing](https://stripe.com/docs/testing)


**Validating Card:**

`Card` provides some validation helpers for validating card at client-side. These valdation helper methods are:

***1. validateNumber():***

This method checks if card number passes Luhn check and returns boolean result.

    card.validateNumber();

***2. validateExpiryDate():***

This helper checks whether expiry date is in future and returns boolean result.

    card.validateExpiryDate();

***3. validateCVC():***

Checks whether the CVC number provided is a valid verification code and returns boolean.

    card.validateCVC();

***4. validateCard():***

This method validates all the above thingstogether i.e. card number, expiry date and CVC and hence is more convenient.

    if(card.validateCard()){
       //Card is valid. Create token
    }else{
      //Card is invalid
    }


#### Creating token:

Stripe libraries send card data directly to the Stripe servers, where Stripe can convert it into tokens. These tokens can then later be used to charge the card by the developers in their server-side code.

Once the card is created, next step is to send this sensitive payment information securely to the Stripe server where stripe will exchange it for token.

Token can be created using `createToken()` method of `Stripe` class. On invoking `createToken()` method, an **asynchronous** network request will be executed and an approriate callback gets invoked upon its completion.

     stripe.createToken(
     card,
     new TokenCallback() {
         public void onSuccess(Token token) {
              //Token successfully created. 
              //Create a charge or save token to the server and use it later
         }
         public void onError(Exception error) {
             // Show localized error message
             Toast.makeText(getContext(),
             error.getLocalizedString(getContext()),
             Toast.LENGTH_LONG
             ).show();
          }
       }
    );

where `stripe` is an instance of `Stripe` class which we initially created by passing the publishable key and `card` is an instance of 'Card' class we created above.

If token is successfully created, onSuccess() TokenCallback method gets called otherwise onError() callback gets called. Token thus obtained in onSuccess() callback can be used to charge the card.

#### Charging the card:

Once token is created, you can charge the card. This process happen at application's native server and not at stripe server. 

Stripe API provides a class called Charge` for charging the card. Using this class you can either :<br />
1. Capture charge immediately or<br />
2. Capture charge later

Full API reference for `Charge` can be found here : [API Charges](https://stripe.com/docs/api#charges)

Charge object has many attributes. These attributes are explained in detail in API document. Some of the important attributes are:

1. id : String<br/>
Unique ID that is assigned to every charge.
    
2. amount : positive integer or zero<br/>
required<br/>
Amount to be charged or refunded.
    
3. currency : String<br/>
required<br/>
Three letter ISO currency code representing currency in which charge is to be made.
    
4. captured : boolean<br/>
optional(default : true)<br/>
Represents if charge is to be captured immediately or later (default is **true**).
    
5. customer : String<br/>
optional, either customer or card attribute is required<br/>
ID of customer to be charged.
    
6. card : String<br/>
optional, either customer or card attribute is required<br/>
ID of created card token

##### Capturing Charge Immediately:

Lets first create a HashMap for required attributes of `Charge`.


    final Map<String, Object> chargeParams = new HashMap<String, Object>();
    		chargeParams.put("amount", 400);
    		chargeParams.put("currency", "usd");
    		chargeParams.put("card", token.getId()); //Token obtained in onSuccess() TokenCallback method of 
                                                       //stripe.createToken() asynchronous call


where, `token` is an instance of `Token` that was created by making an asynchronous call stripe.createToken(); above.

Now creating charge,
         

    com.stripe.Stripe.apiKey = "YOUR_TEST_STRIPE_SECRET_KEY";
    Charge charge = Charge.create(chargeParams);

where, Stripe.apiKey should be set to the test stripe secret key from Stripe [account page](https://manage.stripe.com/account/apikeys).

Charge.create() creates a charge instance which either captures the charge immediately or later depending upon the value of `captured` attribute. Since `captured` parameter is not specified in `chargeParams` HashMap above, it takes its default value i.e. true. And hence, the charge is captured immediately here. 

You can check if the card was charged either by calling method `charge.getPaid()` which returns a boolean value or by looking into the [stripe dashboard](https://manage.stripe.com/test/dashboard).

Note:<br/>
A call to `Charge.create()` should be made in background thread as network operations cannot be performed in main thread in android. And therefore,

    new AsyncTask<Void, Void, Void>() {
    
    			Charge charge;
    			
    			@Override
    			protected Void doInBackground(Void... params) {
    				try {
    					com.stripe.Stripe.apiKey = "YOUR_TEST_STRIPE_SECRET_KEY";
    					charge = Charge.create(chargeParams);
    				} catch (Exception e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    					showAlert("Exception while charging the card!",
    							e.getLocalizedMessage());
    				}
    				return null;
    			}
    			
    			protected void onPostExecute(Void result) {
    				Toast.makeText(OnlinePaymentActivity.this, 
                                  "Card Charged : " + charge.getCreated() + "\nPaid : " +charge.getPaid(),
                                   Toast.LENGTH_LONG
                                   ).show();
    			};
    
    		}.execute();

##### Capturing Charge Later:

There are two ways of capturing charge later. 

**First method:** Setting `captured` atribute of 'Charge' to false <br />
For capturing charge later, you need to set `captured` attribute of `Charge` to false while creating Charge instance, save it's ID and capture it at some other time.

**The uncaptured charge should be captured within seven days of charge creation otherwise charge(amount) will be refunded to the customer.**   

Now `chargeParams` :
 

    final Map<String, Object> chargeParams = new HashMap<String, Object>();
        		chargeParams.put("amount", 400);
        		chargeParams.put("currency", "usd");
        		chargeParams.put("card", token.getId() //Token obtained in onSuccess() TokenCallback method of 
                                                           //stripe.createToken() asynchronous call
                chargeParams.put("captured", false));

Creating uncaptured charge :

    
    com.stripe.Stripe.apiKey = "YOUR_TEST_STRIPE_SECRET_KEY";
    Charge charge = Charge.create(chargeParams);
    //Save charge ID for later use. Can be obtained as charge.getId();


Later (Code to be executed after some time maybe days or in some other Activity)...

    com.stripe.Stripe.apiKey = "YOUR_TEST_STRIPE_SECRET_KEY";
    Charge ch = Charge.retrieve(charge.getId()); //Use saved charged Id instead of charge.getId()
                                                 //Used it here for demonstration
    ch.capture();

`Charge.retrieve()` returns Charge instance having ID provided as parameter. We can then call `capture()` method on retrieved `Charge` instance to capture charge.

**Second Method:** Creating 'Customer' instance and attaching card details to it<br/>

In this method, we don't need create `Charge` instance in advance instead we create `Customer` instance once token is obtained, link card(token) to this `Customer` and later when we want to capture the charge, create `Charge` instance to charge this customer.

Complete API reference for `Customer` can be found here : [Stripe Customer API](https://stripe.com/docs/api#customers)

Code Snippet:

      //Stripe API key
      com.stripe.Stripe.apiKey = "YOUR_TEST_STRIPE_SECRET_KEY";
      
      //Customer Parameters HashMap
      Map<String, Object> customerParams = new HashMap<String, Object>();
      customerParams.put("description", "Customer for test@example.com");
      customerParams.put("card", token.getId()); // Obtained in onSuccess() method of TokenCallback
                                                 // while creating token above 
    
      //Create a Customer
      Customer cust = Customer.create(customerParams);

     /*
        save Customer Id into database for later use. 
        Customer ID can be obtained as cust.getId()
     */

Later charge customer :

     

     //Stripe API key
     com.stripe.Stripe.apiKey = "YOUR_TEST_STRIPE_SECRET_KEY";
    
     //Retrieve saved customer ID from database
     String cust_id = getSavedCustomerId(); //getSavedCustomerId() method should retrieve saved customer Id from db
         
     //Charge Parameters HashMap
     final Map<String, Object> chargeParams = new HashMap<String, Object>();
       		chargeParams.put("amount", 400);
            chargeParams.put("currency", "usd");
            chargeParams.put("customer", cust_id)); //Use customer instead of card
    
     //Charge customer
     Charge.create(chargeParams);  


#### Refunding Charge: 

It is also possible to refund the captured charges. Funds will be refunded to the credit or debit card that was originally charged. You can either refund entire charge amount or only a part of it. You can refund the part of a particular charge as many times as you want until the entire charge has been refunded.

Documentation for refunding charges can be found [here](https://stripe.com/docs/api#refund_charge). 

##### Refund entire charge:

Charge that was previously captured can be refunded as:

    //Stripe API key
    com.stripe.Stripe.apiKey = "YOUR_TEST_STRIPE_SECRET_KEY";
   
    //Retrieve the charge you previously captured
    Charge ch = Charge.retrieve(charge_id); //charge_id is the Id of charge that is to be refunded
                                            //It should be saved in db and fetched from db here
    Charge charge = ch.refund();

`ch.refund()` refunds the entire ammount to the card that was charged.
   

##### Partially refund charge:

It is also possible to partially refund a charge. Let us look at following snippet for partially refunding a charge:

 

    //Refund Paramaters HashMap
     Map<String, Object> refundParams = new HashMap<String, Object>();
     refundParams.put("amount", 50);
    
     //Retrieve the charge that was previously captured
     Charge ch = Charge.retrieve(charge_id); //charge_id is the Id of charge that is to be refunded
                                                //It should be saved in db and fetched from db here
    
      
     //Partially refunding charge
     Charge charge = ch.refund(refundParams);

The only additional thing we need to do for partial refund is to pass the amount to be refunded as a parameter to `refund()` method.

We can check if refund was successful by calling `charge.getRefunded()` which returns a boolean value. On success, it returns true.
 


     


