package com.fireflies.govtfireflies;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fireflies.govtfireflies.ValidationRegex.emailValidationRegex;
import static com.fireflies.govtfireflies.ValidationRegex.nameValidationRegex;

public class RegisterFragment extends Fragment {

	@BindView(R.id.edt_name)
	TextInputEditText edtName;

	@BindView(R.id.edt_email)
	TextInputEditText edtEmail;

	@BindView(R.id.edt_password)
	TextInputEditText edtPassword;

	@BindView(R.id.layout_input_password)
	TextInputLayout layoutPassword;

	@BindView(R.id.progress_bar)
	ProgressBar progressBar;

	private static final String TAG = "RegisterFragment";

	public RegisterFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_register, container, false);
		ButterKnife.bind(this, view);
		((AuthenticationActivity) getActivity()).setToolbarTitle(R.string.string_register);
		ButterKnife.bind(this, view);
		return view;
	}


	@OnClick(R.id.btn_signup_using_google)
	void onClickSignupUsingGoogle() {
	}


	@OnClick(R.id.btn_register)
	void onClickRegister() {
		String name = edtName.getText().toString().trim();
		String email = edtEmail.getText().toString().trim();
		String password = edtPassword.getText().toString().trim();

		boolean isInvalid = false;

		if (TextUtils.isEmpty(name)) {
			edtName.setError("this field cannot be empty");
			isInvalid = true;

		} else if (name.matches(nameValidationRegex)) {
			edtName.setError("this doesn't look like a name");
			isInvalid = true;
		}


		if (TextUtils.isEmpty(email)) {
			edtEmail.setError("this field cannot be empty");
			isInvalid = true;

		} else if (!email.matches(emailValidationRegex)) {
			edtEmail.setError("this is not a valid email address");
			isInvalid = true;
		}


		if (TextUtils.isEmpty(password)) {
			edtPassword.setError("this field is required");
			layoutPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
			isInvalid = true;

		} else if (password.length() < 8) {
			edtPassword.setError("minimum password length is 8");
			layoutPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
			isInvalid = true;
		}


		if (!isInvalid) {
			progressBar.setVisibility(View.VISIBLE);
			FirebaseAuth auth = FirebaseAuth.getInstance();
			auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
				if (task.isSuccessful()) {

					progressBar.setVisibility(View.GONE);
					Log.d(TAG, "createUserWithEmail: success");
					FirebaseUser user = auth.getCurrentUser();
					assert user != null: "Couldn't Get user Info";

					AuthPreferences authPreferences = new AuthPreferences(getActivity());
					authPreferences.setEmail(user.getEmail());
					startActivity(new Intent(getActivity(), HomeActivity.class));
					getActivity().finish();

				} else {

					progressBar.setVisibility(View.GONE);
					Log.w(TAG, "createUserWithEmail: failure", task.getException());
					Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}


	@OnClick(R.id.btn_login_either)
	void onClickLoginEither() {
		getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();
	}

}