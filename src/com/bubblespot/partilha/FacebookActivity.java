package com.bubblespot.partilha;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import android.widget.Toast;

import com.facebook.DialogError;
import com.facebook.Facebook;
import com.facebook.Facebook.DialogListener;
import com.facebook.FacebookError;

public class FacebookActivity extends Activity {

	private static final String APP_ID = "263506900373262";
	private static final String[] PERMISSIONS = new String[] { "publish_stream" };

	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";

	private Facebook facebook;
	private String messageToPost;
	private String imageLink;
	private String description;
	private String caption;
	private String href;
	private String name;

	
	public boolean saveCredentials(Facebook facebook) {
		Editor editor = getApplicationContext().getSharedPreferences(KEY,
				Context.MODE_PRIVATE).edit();
		editor.putString(TOKEN, facebook.getAccessToken());
		editor.putLong(EXPIRES, facebook.getAccessExpires());
		return editor.commit();
	}

	public boolean restoreCredentials(Facebook facebook) {
		SharedPreferences sharedPreferences = getApplicationContext()
				.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
		facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
		return facebook.isSessionValid();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle b = this.getIntent().getExtras();

		facebook = new Facebook(APP_ID);
		restoreCredentials(facebook);
		
		facebook.authorize(this, PERMISSIONS,

		new DialogListener() {

			public void onComplete(Bundle values) {
				saveCredentials(facebook);
				if (messageToPost != null) {
					postToWall(messageToPost);
				}
			}

			public void onFacebookError(FacebookError error) {
				showToast("Autentica��o no Facebook falhou!");
				finish();
			}

			public void onError(DialogError error) {
				showToast("Autentica��o no Facebook falhou!");
				finish();
			}

			public void onCancel() {
				showToast("Autentica��o no Facebook cancelada!");
				finish();
			}
		});

		String facebookMessage = b.getString("message");
		messageToPost = facebookMessage;
		imageLink = b.getString("imageLink");
		description = b.getString("description");
		caption = b.getString("caption");
		href = b.getString("link");
		name = b.getString("name");


		
	}

	public void loginAndPostToWall() {
		facebook.authorize(this, PERMISSIONS, new LoginDialogListener());
	}

	public void postToWall(String message) {
		Bundle parameters = new Bundle();
		parameters.putString("picture", imageLink);
		parameters.putString("message", message);
		parameters.putString("name", name);
		parameters.putString("link", href);
		parameters.putString("description", description);

		parameters.putString("caption", caption);
		
		facebook.dialog(this, "stream.publish", parameters,
				new WallPostDialogListener());
		
		
	}

	class LoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			saveCredentials(facebook);
			if (messageToPost != null) {
				
				postToWall(messageToPost);
			}
		}

		public void onFacebookError(FacebookError error) {
			showToast("Autentica��o no Facebook falhou!");
			finish();
		}

		public void onError(DialogError error) {
			showToast("Autentica��o no Facebook falhou!");
			finish();
		}

		public void onCancel() {
			showToast("Autentica��o no Facebook cancelada!");
			finish();
		}
	}

	class WallPostDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			final String postId = values.getString("post_id");
			if (postId != null) {
				showToast("Mensagem publicada no mural do Facebook");
			} else {
				showToast("Publica��o cancelada!");
			}
			finish();
		}

		public void onFacebookError(FacebookError e) {
			showToast("Falha ao publicar no mural!");
			e.printStackTrace();
			finish();
		}

		public void onError(DialogError e) {
			showToast("Falha ao publicar no mural!");
			e.printStackTrace();
			finish();
		}

		public void onCancel() {
			showToast("Publica��o cancelada!");
			finish();
		}
	}

	private void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		facebook.authorizeCallback(requestCode, resultCode, data);
	}
	

}