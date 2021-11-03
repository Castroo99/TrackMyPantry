package com.progetto.trackingmypantry.ui.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.progetto.trackingmypantry.R;
import com.progetto.trackingmypantry.databinding.FragmentLoginBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;
    private FragmentLoginBinding binding;
    public static final String SHARED_PREFS = "CREDENTIAL";
    public static final String key = "accessToken";
    private ExecutorService threadPool;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        threadPool = Executors.newFixedThreadPool(5);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageView backgroundLoginImg = binding.backgroundLoginImg;
        Glide.with(getContext()).load(R.drawable.wood).into(backgroundLoginImg);

        EditText email = binding.emailText;
        EditText password = binding.editTextPassword;
        Button login = binding.loginButton;

        loginViewModel.getAccessTokenMutableLiveData().observe(getViewLifecycleOwner(), accessToken -> {

            if (accessToken == null) {

            }
            else if (accessToken != null && accessToken.getAccessToken()!="Errore") {

                threadPool.execute(() -> {
                    SharedPreferences sharedPreferences = LoginFragment.this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(key, accessToken.getAccessToken());
                    editor.apply();
                });
                Toast.makeText(LoginFragment.this.getContext(), "Login effettuato", Toast.LENGTH_SHORT).show();

            }
            else if (accessToken.getAccessToken() == "Errore"){
                SharedPreferences sharedPreferences = LoginFragment.this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(key, "Errore");
                editor.apply();
                Toast.makeText(LoginFragment.this.getContext(), "Credenziali errate", Toast.LENGTH_SHORT).show();
            }
        });

        login.setOnClickListener(view -> {

            if (!email.getText().toString().equals("") && !password.getText().toString().equals("")) {

                loginViewModel.login(email.getText().toString(), password.getText().toString());

                email.getText().clear();
                password.getText().clear();

            }
            else {
                Toast.makeText(getContext(), "Compilare tutti i campi", Toast.LENGTH_SHORT).show();
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}