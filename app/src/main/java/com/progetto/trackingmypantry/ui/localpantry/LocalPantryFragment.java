package com.progetto.trackingmypantry.ui.localpantry;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.progetto.trackingmypantry.R;
import com.progetto.trackingmypantry.databinding.LocalPantryFragmentBinding;
import com.progetto.trackingmypantry.room.LocalProducts;
import com.progetto.trackingmypantry.ui.CameraAct;
import com.progetto.trackingmypantry.ui.home.HomeViewModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalPantryFragment extends Fragment implements LocalPantryAdapter.OnDeleteListener {

    private LocalPantryViewModel localPantryViewModel;
    private HomeViewModel homeViewModel;
    private LocalPantryFragmentBinding binding;
    private RecyclerView recyclerView;
    private String imageEncoded;
    private DatePickerDialog datePicker;
    private Calendar calendar;
    private ExecutorService threadPool;
    ActivityResultLauncher<Intent> imageResultLauncher;
    private String[] PERMISSIONSCAMERA;
    private String[] PERMISSIONSSTORAGE;
    public static final String ALARM = "ID";
    public static final String key = "alarmId";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        threadPool = Executors.newFixedThreadPool(8);

        localPantryViewModel = new ViewModelProvider(this).get(LocalPantryViewModel.class);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        binding = LocalPantryFragmentBinding.inflate(inflater, container, false);
        recyclerView = binding.localCardsListView;
        View root = binding.getRoot();
        FloatingActionButton addButton = binding.addLocalProdBtn;
        FloatingActionButton searchButton = binding.searchLocalProdBtn;

        ImageView backgroundLocalImg = binding.backgroundLocalImg;
        Glide.with(getContext()).load(R.drawable.wood).into(backgroundLocalImg);

        PERMISSIONSCAMERA = new String[] {
                Manifest.permission.CAMERA
        };
        PERMISSIONSSTORAGE = new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        imageEncoded = null;
        setHasOptionsMenu(true);

        imageResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            if(result.getResultCode() == Activity.RESULT_OK) {
                Uri selectedImage = result.getData().getData();
                Log.println(Log.INFO, "Get Products fragment", selectedImage.getEncodedPath());
                try {
                    Bitmap bitmap;
                    ImageDecoder.Source source = null;
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
            createNotificationChannel();
        });
        t.setName("Notification Channel");
        t.start();

        addButton.setOnClickListener(view -> showLocalProdDialog());

        searchButton.setOnClickListener(view -> showNameDialog());

        loadRecyclerViewItems();

        return root;
    }

    //Permessi RUNTIME
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.getItem(0);
        item.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                filterDialog();
                return  true;
            default :
                return super.onOptionsItemSelected(item);
        }

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ExpirationReminderChannel";
            String description = "Channel for Expiration Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("expirationAlarm", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void loadRecyclerViewItems() {
        threadPool.execute(() -> {

            Handler threadHandler = new Handler(Looper.getMainLooper());

            List<LocalProducts> localProductsList = localPantryViewModel.getAllProducts();

            threadHandler.post(() -> {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);
                LocalPantryAdapter localPantryAdapter = new LocalPantryAdapter(getContext(), localProductsList, LocalPantryFragment.this);
                recyclerView.setAdapter(localPantryAdapter);
            });

        });
    }

    void filterDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_filter_products);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button filter = dialog.findViewById(R.id.filterDialogLocalProducts);
        Spinner type = dialog.findViewById(R.id.localFilterDialogType);
        String[] items = new String[]{"Cibo", "Bevanda"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        type.setAdapter(adapter);

        filter.setOnClickListener(v -> {
            threadPool.execute(() -> {

                Handler threadHandler = new Handler(Looper.getMainLooper());

                List<LocalProducts> localProductsList = localPantryViewModel.getFilteredProducts(type.getSelectedItem().toString());
                threadHandler.post(() -> {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    LocalPantryAdapter localPantryAdapter = new LocalPantryAdapter(getContext(), localProductsList, LocalPantryFragment.this);
                    recyclerView.setAdapter(localPantryAdapter);
                });

            });
            dialog.dismiss();
        });

        dialog.show();
    }

    void showNameDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.search_local_prod_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText prodNameView = dialog.findViewById(R.id.nameText);
        Button search = dialog.findViewById(R.id.searchLocalProducts);

        search.setOnClickListener(view -> {
            threadPool.execute(() -> {

                Handler threadHandler = new Handler(Looper.getMainLooper());

                List<LocalProducts> localProductsList = localPantryViewModel.searchProduct(prodNameView.getText().toString());

                threadHandler.post(() -> {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    LocalPantryAdapter localPantryAdapter = new LocalPantryAdapter(getContext(), localProductsList, LocalPantryFragment.this);
                    recyclerView.setAdapter(localPantryAdapter);
                });
            });
            dialog.dismiss();
        });

        dialog.show();
    }

    void showLocalProdDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_local_products);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText nameView = dialog.findViewById(R.id.localDialogNameProd);
        EditText descriptionView = dialog.findViewById(R.id.localDialogDescriptionProd);
        EditText expirationDate = dialog.findViewById(R.id.dialogExpirationDate);
        EditText barcodeView = dialog.findViewById(R.id.localBarcodeProd);
        ImageButton localCamera = dialog.findViewById(R.id.localCameraBtn);
        expirationDate.setInputType(InputType.TYPE_NULL);
        imageEncoded = null;

        homeViewModel.setStringScanned("");
        homeViewModel.getStringScanned().observe(getViewLifecycleOwner(), s -> {
            barcodeView.setText(s);

        });

        localCamera.setOnClickListener(view -> {
            if (!hasPermissions(getActivity(),PERMISSIONSCAMERA)){
                ActivityCompat.requestPermissions(getActivity(),PERMISSIONSCAMERA,1);
            }
            else {
                scanCode();
            }
        });


        expirationDate.setOnClickListener(view -> {

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
        Button upload = dialog.findViewById(R.id.uploadLocalImg);
        Spinner type = dialog.findViewById(R.id.localDialogType);
        String[] items = new String[]{"Cibo", "Bevanda"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        type.setAdapter(adapter);

        upload.setOnClickListener(view -> threadPool.execute(() -> {
                    if (!hasPermissions(getActivity(),PERMISSIONSSTORAGE)){
                        ActivityCompat.requestPermissions(getActivity(),PERMISSIONSSTORAGE,1);
                    }
                    else {
                        selectImg();
                    }
                }
        ));

        add.setOnClickListener(v -> {
            if (!barcodeView.getText().toString().equals("") && !descriptionView.getText().toString().equals("") && !nameView.getText().toString().equals("")
                && !expirationDate.getText().toString().equals("")) {

                threadPool.execute(() -> {
                    Handler threadHandler = new Handler(Looper.getMainLooper());
                    Intent intent = new Intent(getContext(), ReminderBroadcast.class);
                    int id = getActivity().getSharedPreferences(ALARM, Context.MODE_PRIVATE).getInt(key, 0);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    SharedPreferences sharedPreferences = LocalPantryFragment.this.getActivity().getSharedPreferences(ALARM, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(key, id+1);
                    editor.apply();


                    Log.w("coap", String.valueOf(id));
                    Log.w("ciao", String.valueOf(id-1));
                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

                    if (calendar != null) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }

                    LocalProducts localProducts = new LocalProducts();
                    localProducts.barcode = barcodeView.getText().toString();
                    localProducts.description = descriptionView.getText().toString();
                    localProducts.prodName = nameView.getText().toString();
                    localProducts.expirationDate = expirationDate.getText().toString();
                    localProducts.type = type.getSelectedItem().toString();
                    localProducts.img = imageEncoded;
                    localProducts.alarmId = id;

                    localPantryViewModel.insertProduct(localProducts);

                    threadHandler.post(() -> loadRecyclerViewItems());
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
        Intent getImg = new   Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imageResultLauncher.launch(getImg);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDeleteClick(int position, List<LocalProducts> localProductsList) {
        threadPool.execute(() -> {
            Handler threadHandler = new Handler(Looper.getMainLooper());
            LocalProducts localProducts = localProductsList.get(position);

            Intent intent = new Intent(getContext(), ReminderBroadcast.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), localProducts.alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);

            localPantryViewModel.deleteProduct(localProducts);
            threadHandler.post(() -> {

                Toast.makeText(getContext(), "Oggetto eliminato", Toast.LENGTH_SHORT).show();

                loadRecyclerViewItems();
            });
        });

    }
}