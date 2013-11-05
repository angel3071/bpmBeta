package com.bpm.bpmpayment;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.bpm.bpmpayment.json.JSONParser;

public class LoginActivity extends Activity {
	private UserLoginTask mAuthTask = null;
	public static String mEmail;
	private String mPassword;

	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	public boolean login;
	public String storedMail;
	public String storedPass;
	public String pass;
	public String email;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		SharedPreferences storedPreferences = getSharedPreferences("datos",Context.MODE_PRIVATE);
		storedMail = storedPreferences.getString("mail", "");
		storedPass = storedPreferences.getString("pass", "");
		
		if(!storedMail.equals("") && !storedPass.equals("")){
			pass = storedPass;
			email = storedMail;
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("email", email));
	        params.add(new BasicNameValuePair("password", pass));	
			
			mAuthTask = new UserLoginTask();
			mAuthTask.execute(params);
		}
		
		setContentView(R.layout.activity_login);
		mEmailView = (EditText) findViewById(R.id.email);
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();					
						
						pass = (storedPass.equals(""))? mPasswordView.getText().toString():storedPass;
						email = (storedMail.equals(""))? mEmailView.getText().toString():storedMail;
						
						pass =  mPasswordView.getText().toString();
						email = mEmailView.getText().toString();
						
						List<NameValuePair> params = new ArrayList<NameValuePair>();
				        params.add(new BasicNameValuePair("email", email));
				        params.add(new BasicNameValuePair("password", pass));						
						
						mAuthTask = new UserLoginTask();
						mAuthTask.execute(params);
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void attemptLogin() {
		if (mAuthTask != null) { return; }

		mEmailView.setError(null);
		mPasswordView.setError(null);

		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		
		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError("Este campo es Obligatorio");
			focusView = mPasswordView;
			cancel = true;
		}

		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError("Este campo es Obligatorio");
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError("Este campo es Obligatorio");
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			mLoginStatusMessageView.setText("Por favor espere");
			esconderTeclado();
			showProgress(true);
			
		}
	}
	
	private void esconderTeclado() {
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),      
		InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public class UserLoginTask extends AsyncTask<List<NameValuePair>, Void, String>{
		@Override
		protected String doInBackground(List<NameValuePair>... params) {
			try {
				for (NameValuePair nvp : params[0]) {
				    String name = nvp.getName();
				    String value = nvp.getValue();
				    Log.w("name", name);
				    Log.w("value", value);
				}
				
				String ret = new JSONParser().getJSONFromUrl("http://bpmcart.com/bpmpayment/php/modelo/loginPost.php", params[0]);
				Log.w("JSON", ret);
				return ret;
			} catch (Exception e) {
				Log.w("Error doInBackground", e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			mAuthTask = null;
			showProgress(false);
            	try{
	                if(!result.equals("false")) {
	                	JSONObject jObject  = new JSONObject(result);
	                	String usuario = jObject.getString("email");
	                			                
		                Intent i = new Intent(getApplicationContext(), FragmentActivityMain.class);
		                i.putExtra("usuario", usuario);
		                
		                SharedPreferences storedPreferences = getSharedPreferences("datos",Context.MODE_PRIVATE);
		                Editor editor = storedPreferences.edit();
		                editor.putString("mail", email);
		                editor.putString("pass", pass);
		                editor.commit();
		                
						startActivity(i);
						showProgress(false);
						
						finish();
	                }
	                else {
	                	Toast.makeText(getBaseContext(), "Credenciales inválidas",Toast.LENGTH_LONG).show();
	                }
	            } catch (Exception e) {
	                Log.d("ReadJSONFeedTask", e.getMessage());
	                Toast.makeText(getBaseContext(), "Imposible conectarse a la red",Toast.LENGTH_LONG).show();
	            }          
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
