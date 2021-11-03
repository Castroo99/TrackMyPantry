package com.progetto.trackingmypantry.ui.home;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.progetto.trackingmypantry.R;
import com.progetto.trackingmypantry.databinding.FragmentHomeBinding;
import com.progetto.trackingmypantry.room.Database;
import com.progetto.trackingmypantry.room.LocalProducts;
import com.progetto.trackingmypantry.ui.CameraAct;
import com.progetto.trackingmypantry.ui.localpantry.ReminderBroadcast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment implements HomeAdapter.OnRatingListener, HomeAdapter.OnAddListener {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private RecyclerView cardsList;
    private String imageEncoded;
    private String accessToken;
    private String sessionToken;
    private ExecutorService threadPool;
    private DatePickerDialog datePicker;
    private Calendar calendar;
    public static final String SHARED_PREFS = "CREDENTIAL";
    public static final String key = "sessionToken";
    ActivityResultLauncher<Intent> imageResultLauncher;
    private String[] PERMISSIONSCAMERA;
    private String[] PERMISSIONSSTORAGE;
    public static final String ALARM = "ID";
    public static final String keyAlarm = "alarmId";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        threadPool = Executors.newFixedThreadPool(8);

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        cardsList = binding.cardsListView;
        imageEncoded = null;

        PERMISSIONSCAMERA = new String[] {
                Manifest.permission.CAMERA,
        };
        PERMISSIONSSTORAGE = new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        ImageView backgroundImg = binding.backgroundImg;
        Glide.with(getContext()).load(R.drawable.wood).into(backgroundImg);

        FloatingActionButton searchProdBtn = binding.searchProdBtn;
        FloatingActionButton postProdBtn = binding.postProdBtn;

        imageResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            if(result.getResultCode() == Activity.RESULT_OK) {
                Uri selectedImage = result.getData().getData();
                Log.println(Log.INFO, "Get Products fragment", selectedImage.getEncodedPath());
                try {
                    Bitmap bitmap;
                    ImageDecoder.Source source;
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        source = ImageDecoder.createSource(getActivity().getContentResolver(), selectedImage);
                        bitmap = ImageDecoder.decodeBitmap(source);
                    }
                    else {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    }
                    int h = 150;
                    int w = 150;
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, h, w, true);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    scaled.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] b = baos.toByteArray();
                    String prova = Base64.encodeToString(b, Base64.DEFAULT);
                    imageEncoded = "data:image/png;base64,"+prova;
                    Log.println(Log.INFO, "Get Products fragment", imageEncoded);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t = new Thread(() -> {
            accessToken = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString("accessToken", "");
        });
        t.setName("Get Access Token");
        t.start();

        searchProdBtn.setOnClickListener(view -> {
            if (accessToken != "" && accessToken != "Errore") {
                showBarcodeDialog();
            }
            else {
                Toast.makeText(getContext(), "Fare il login", Toast.LENGTH_SHORT).show();
            }
        });

        postProdBtn.setOnClickListener(view -> {

            sessionToken = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString("sessionToken", "");

            if (sessionToken != "") {
                showProdDialog();
            }
            else {
                Toast.makeText(getContext(), "Ricercare prima il prodotto", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    private boolean hasPermissions(Context context, String... PERMISSIONS) {
        if (context != null && PERMISSIONS != null) {
            for (String permission: PERMISSIONS){
                if (ActivityCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    void showBarcodeDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_barcode);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText barcodeView = dialog.findViewById(R.id.barcodeText);
        Button search = dialog.findViewById(R.id.searchProducts);
        ImageButton cameraBtn = dialog.findViewById(R.id.cameraBtn);

        homeViewModel.setStringScanned("");
        homeViewModel.getStringScanned().observe(getViewLifecycleOwner(), s -> {
            barcodeView.setText(s);

        });

        cameraBtn.setOnClickListener(view -> {
            if (!hasPermissions(getActivity(),PERMISSIONSCAMERA)) {
                ActivityCompat.requestPermissions(getActivity(),PERMISSIONSCAMERA,1);
            }
            else {
                scanCode();
            }
        });

        search.setOnClickListener(v -> {
            String barcode = barcodeView.getText().toString();

            homeViewModel.loadProducts(accessToken, barcode).observe(getViewLifecycleOwner(), productsToken -> {

                if (productsToken!=null) {
                    //Salvo il sessionToken
                    threadPool.execute(() -> {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(key, productsToken.getToken());
                        editor.apply();
                    });

                    HomeAdapter homeAdapter = new HomeAdapter(getActivity(), productsToken.getProducts(), HomeFragment.this, HomeFragment.this);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    cardsList.setLayoutManager(linearLayoutManager);
                    cardsList.setAdapter(homeAdapter);

                }
            });
            dialog.dismiss();
        });

        dialog.show();
    }

    //Function to display the Post Products Dialog
    void showProdDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_products);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText nameView = dialog.findViewById(R.id.nameProd);
        EditText descriptionView = dialog.findViewById(R.id.descriptionProd);
        EditText barcodeView = dialog.findViewById(R.id.barcodeTextProd);
        ImageButton cameraBtn = dialog.findViewById(R.id.cameraBtn);
        Button add = dialog.findViewById(R.id.addProducts);
        Button upload = dialog.findViewById(R.id.uploadImg);

        imageEncoded = null;
        homeViewModel.setStringScanned("");
        homeViewModel.getStringScanned().observe(getViewLifecycleOwner(), s -> {
            barcodeView.setText(s);

        });

        cameraBtn.setOnClickListener(view -> {
            if (!hasPermissions(getActivity(),PERMISSIONSCAMERA)) {
                ActivityCompat.requestPermissions(getActivity(),PERMISSIONSCAMERA,1);
            }
            else {
                scanCode();
            }

        });

        upload.setOnClickListener(view -> threadPool.execute(() -> {
            if (!hasPermissions(getActivity(),PERMISSIONSSTORAGE)){
                ActivityCompat.requestPermissions(getActivity(),PERMISSIONSSTORAGE,1);
            }
            else {
                selectImg();
            }
        }));

        add.setOnClickListener(v -> {

            if (!barcodeView.getText().toString().equals("") && !nameView.getText().toString().equals("") &&
                !descriptionView.getText().toString().equals("")) {

                threadPool.execute(() -> {
                    String barcode = barcodeView.getText().toString();
                    String name = nameView.getText().toString();
                    String description = descriptionView.getText().toString();
                    sessionToken = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(key, "");
                    Log.w("ciao", sessionToken);
                    homeViewModel.addProducts(accessToken, sessionToken, name, description, imageEncoded, barcode);
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                    sharedPreferences.edit().remove("sessionToken").apply();
                });

                dialog.dismiss();
            }
            else {
                Toast.makeText(getContext(), "Compilare tutti i campi", Toast.LENGTH_SHORT).show();
            }

        });

        dialog.show();
    }

    private void scanCode() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
        intentIntegrator.setCaptureActivity(CameraAct.class);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setPrompt("Scanning Barcode");
        intentIntegrator.initiateScan();
    }

    private void selectImg() {
        Intent getImg = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imageResultLauncher.launch(getImg);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRatingClick(int position, RatingBar ratingBar, float v) {

        ConstraintLayout constraintLayout = (ConstraintLayout) ratingBar.getParent();
        TextView productId = (TextView) constraintLayout.getChildAt(4);
        sessionToken = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString("sessionToken", "");
        homeViewModel.postVote(accessToken, sessionToken, (int) v, (String) productId.getText());
        homeViewModel.getCodeRequest().observe(getViewLifecycleOwner(), code -> {
            Log.println(Log.INFO, "Code", String.valueOf(code));
            if (code == 500) {
                Log.w("Voto", "Oggetto già votato");
            }
        });
    }

    @Override
    public void onAddClick(View view) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_add_server_to_local);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText expirationDate = dialog.findViewById(R.id.dialogExpirationDate);
        expirationDate.setInputType(InputType.TYPE_NULL);

        expirationDate.setOnClickListener(v -> {

            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            datePicker = new DatePickerDialog(getContext(), (view1, year1, monthOfYear, dayOfMonth) -> {

                calendar = new GregorianCalendar(year1, monthOfYear, dayOfMonth);
                Log.println(Log.WARN, "DATA", calendar.getTime().toString());
                expirationDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
            }, year, month, day);
            datePicker.show();
        });

        Button add = dialog.findViewById(R.id.addDialogLocalProducts);
        Spinner type = dialog.findViewById(R.id.localDialogType);
        String[] items = new String[]{"Cibo", "Bevanda"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        type.setAdapter(adapter);

        add.setOnClickListener(v -> {
            if (!expirationDate.getText().toString().equals("")) {
                Log.w("ciao", expirationDate.getText().toString());
                threadPool.execute(() -> {
                    Intent intent = new Intent(getContext(), ReminderBroadcast.class);
                    int id = getActivity().getSharedPreferences(ALARM, Context.MODE_PRIVATE).getInt(keyAlarm, 0);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), id, intent, 0);

                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    if (calendar != null) {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }

                    SharedPreferences sharedPreferences = HomeFragment.this.getActivity().getSharedPreferences(ALARM, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(keyAlarm, id+1);
                    editor.apply();

                    //Prendo gli elementi della carta da mettere nella dispensa locale
                    ConstraintLayout constraintLayout = (ConstraintLayout) view.getParent();
                    TextView nameBarcodeView = (TextView) constraintLayout.getChildAt(1);
                    TextView descriptionView = (TextView) constraintLayout.getChildAt(2);
                    ImageView imageView = (ImageView) constraintLayout.getChildAt(0);

                    String image64 = null;
                    if (!(imageView.getDrawable() instanceof VectorDrawable)) {
                        //ImageView to Base64 String
                        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();
                        int h = 150;
                        int w = 150;
                        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, h, w, true);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        scaled.compress(Bitmap.CompressFormat.PNG,100, baos);
                        byte[] b = baos.toByteArray();
                        String image = Base64.encodeToString(b, Base64.DEFAULT);
                        image64 = "data:image/png;base64," + image;
                    }

                    String[] nameBarcode = nameBarcodeView.getText().toString().split(" / ");

                    Database db = Database.getDbInstance(getContext());

                    LocalProducts localProducts = new LocalProducts();
                    if (nameBarcode.length == 0) {
                        localProducts.prodName = "";
                        localProducts.barcode = "";
                    }
                    else if (nameBarcode.length == 1){
                        localProducts.prodName = nameBarcode[0];
                        localProducts.barcode = "";
                    }
                    else {
                        localProducts.prodName = nameBarcode[0];
                        localProducts.barcode = nameBarcode[1];
                    }
                    localProducts.description = descriptionView.getText().toString();
                    localProducts.expirationDate = expirationDate.getText().toString();
                    localProducts.type = type.getSelectedItem().toString();
                    localProducts.img = image64;
                    localProducts.alarmId = id;
                    db.localProductsDao().insertProduct(localProducts);
                });


                dialog.dismiss();
            }
            else {
                Toast.makeText(getContext(), "Compilare tutti i campi", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}