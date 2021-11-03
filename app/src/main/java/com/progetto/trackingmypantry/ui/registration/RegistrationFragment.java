package com.progetto.trackingmypantry.ui.registration;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.progetto.trackingmypantry.databinding.FragmentRegistrationBinding;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegistrationFragment extends Fragment {

    private RegistrationViewModel registrationViewModel;
    private FragmentRegistrationBinding binding;
    private ExecutorService threadPool;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        threadPool = Executors.newFixedThreadPool(5);

        registrationViewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);

        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageView backgroundRegistrationImg = binding.backgroundRegistrationImg;
        Glide.with(getContext()).load(R.drawable.wood).into(backgroundRegistrationImg);

        EditText username = binding.usernameText;
        EditText email = binding.emailText;
        EditText password = binding.editTextPassword;
        Button register = binding.loginButton;

        registrationViewModel.getRegisterCodeValue().observe(getViewLifecycleOwner(), integer -> {
            if (integer == 201) {
                Toast.makeText(getContext(), "Registrazione effettuata", Toast.LENGTH_SHORT).show() ;
            }
            else {
                Toast.makeText(getContext(), "Registrazione fallita", Toast.LENGTH_SHORT).show() ;
            }
        });

        register.setOnClickListener(view -> threadPool.execute(() -> {

            Handler threadHandler = new Handler(Looper.getMainLooper());

            if (!email.getText().toString().equals("") && !password.getText().toString().equals("")
                    && !username.getText().toString().equals("")) {

                registrationViewModel.register(username.getText().toString(), email.getText().toString(), password.getText().toString());

                threadHandler.post(() -> {
                    username.getText().clear();
                    email.getText().clear();
                    password.getText().clear();

                });
            }
            else {
                threadHandler.post(() -> Toast.makeText(getContext(), "Compilare tutti i campi", Toast.LENGTH_SHORT).show());
            }
        }));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}