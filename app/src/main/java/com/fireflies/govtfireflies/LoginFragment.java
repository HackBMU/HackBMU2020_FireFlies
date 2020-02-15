package com.fireflies.govtfireflies;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fireflies.govtfireflies.ValidationRegex.emailValidationRegex;

public class LoginFragment extends Fragment {

	@BindView(R.id.edt_email)
	TextInputEditText edtEmail;

	@BindView(R.id.edt_password)
	TextInputEditText edtPassword;

	@BindView(R.id.progress_bar)
	ProgressBar progressBar;

	private static final String TAG = "LoginFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_login, container, false);
		((AuthenticationActivity) getActivity()).setToolbarTitle(R.string.string_login);
		ButterKnife.bind(this, view);
		return view;
	}


	@OnClick(R.id.btn_login_using_google)
	void onClickLoginUsingGoogle() {
		Intent intent = new Intent(getActivity(), HomeActivity.class);
		startActivity(intent);
	}


	@OnClick(R.id.btn_login)
	void onClickLogin() {
		String email = edtEmail.getText().toString().trim();
		String password = edtPassword.getText().toString().trim();

		boolean isInvalid = false;

		if (TextUtils.isEmpty(email)) {
			edtEmail.setError("this field cannot be empty");
			isInvalid = true;

		} else if (!email.matches(emailValidationRegex)) {
			edtEmail.setError("this is not a valid email address");
			isInvalid = true;
		}

		if (TextUtils.isEmpty(password)) {
			edtPassword.setError("this field is required");
			isInvalid = true;

		} else if (password.length() < 8) {
			edtPassword.setError("minimum password length is 8");
			isInvalid = true;
		}

		if (!isInvalid) {
			progressBar.setVisibility(View.VISIBLE);
			FirebaseAuth auth = FirebaseAuth.getInstance();
			auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
				if (task.isSuccessful()) {
					progressBar.setVisibility(View.GONE);
					FirebaseUser user = auth.getCurrentUser();
					Log.d(TAG, "onClickLogin: " + user.getEmail());

					AuthPreferences authPreferences = new AuthPreferences(getActivity());
					authPreferences.setEmail(user.getEmail());
					authPreferences.setUserId(user.getUid());
					startActivity(new Intent(getActivity(), HomeActivity.class));
					getActivity().finish();

				} else {
					progressBar.setVisibility(View.GONE);
					Log.d(TAG, "Login Failed: " + task.getException());
					Toast.makeText(getActivity(), "Login Failed" + task.getException(), Toast.LENGTH_SHORT).show();
				}
			});
		}
	}


	@OnClick(R.id.btn_signup_either)
	void onClickLoginEither() {
		getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RegisterFragment()).commit();
	}

}
