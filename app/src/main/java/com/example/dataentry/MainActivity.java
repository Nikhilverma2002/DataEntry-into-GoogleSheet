package com.example.dataentry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dataentry.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import www.sanju.motiontoast.MotionToast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    ActivityMainBinding binding;

    String who,description,email,rent_sale,status,rat_s,feature_s,owner_name,number,name,address,area,min_price,max_price,sq_feet,year;

    int ps=0,rs=0,rated=0,featured=0;
    private Uri filePath;
   // ProgressDialog progressDialog;
    Dialog dialog, dialog1;
    private int upload_count = 0;

    String amenities;
    ArrayList urlStrings;
    ArrayList link;
    FirebaseStorage storage;
    StorageReference storageReference;

    ArrayAdapter cit,type,sub_type,sel_bhk,face;
    String sub[];
    String[] city_name = {"Bilaspur","Raipur"};
    String[] property = {"Residential","Commercial","Farms"};
    String[] sub_property_resident = {"Flat/Apartment","House","Villa","Builder Fleer","Plots","Office Space","Shop","Commercial Land","WareHouse/Godown","Industrial Land","Farm House Project","Agriculture Lands"};
    String[] sub_property_commercial = {"Office Space","Shop","Commercial Land","WareHouse/Godown","Industrial Land"};
    String[] sub_property_farms = {"Farm House Project","Agriculture Lands"};
    String[] selectBhk = {"1BHK","2BHK","3BHK","Open Space","Palace","Villa"};
    String[] facing = {"Chhattisgarh"};

    int PICK_IMAGE_MULTIPLE = 1;
    private final int PICK_IMAGE_REQUEST = 22;
    ArrayList<Uri> mArrayUri;
    int position = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mArrayUri = new ArrayList<>();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Window window = MainActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.white));


        binding.next.setOnClickListener(v->{
            if (position < mArrayUri.size() - 1) {
                // increase the position by 1
                position++;
                binding.imageThumb.setImageURI(mArrayUri.get(position));
            } else {
                Toast.makeText(MainActivity.this, "Last Image", Toast.LENGTH_SHORT).show();
            }
        });

        binding.previous.setOnClickListener(v->{
            if (position > 0) {
                // decrease the position by 1
                position--;
                binding.imageThumb.setImageURI(mArrayUri.get(position));
            }
        });

        binding.imageThumb.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new ImageView(getApplicationContext());
            }
        });

        binding.constraint.setOnClickListener(v->{
            propertyImage();
        });

        binding.constraint2.setOnClickListener(v->{
            thumbnailImage();
        });

        /*binding.textView86.setOnClickListener(v->{
            propertyImage();
        });*/


        binding.city.setOnItemSelectedListener(MainActivity.this);
        binding.propertyType.setOnItemSelectedListener(MainActivity.this);
        binding.subpropertytype.setOnItemSelectedListener(MainActivity.this);
        binding.selectBhk.setOnItemSelectedListener(MainActivity.this);
        binding.facing.setOnItemSelectedListener(MainActivity.this);


        binding.owner.setOnClickListener(v->{
            binding.owner.setBackgroundResource(R.drawable.bg_choose);
            binding.broker.setBackgroundResource(R.drawable.bg_card_choose);
            binding.builder.setBackgroundResource(R.drawable.bg_card_choose);
            binding.arpan.setBackgroundResource(R.drawable.bg_card_choose);
            who = "Owner";
        });

        binding.broker.setOnClickListener(v->{
            binding.owner.setBackgroundResource(R.drawable.bg_card_choose);
            binding.broker.setBackgroundResource(R.drawable.bg_choose);
            binding.builder.setBackgroundResource(R.drawable.bg_card_choose);
            binding.arpan.setBackgroundResource(R.drawable.bg_card_choose);
            who = "Broker";
        });

        binding.builder.setOnClickListener(v->{
            binding.owner.setBackgroundResource(R.drawable.bg_card_choose);
            binding.broker.setBackgroundResource(R.drawable.bg_card_choose);
            binding.builder.setBackgroundResource(R.drawable.bg_choose);
            binding.arpan.setBackgroundResource(R.drawable.bg_card_choose);
            who = "Builder";
        });

        binding.arpan.setOnClickListener(v->{
            binding.owner.setBackgroundResource(R.drawable.bg_card_choose);
            binding.broker.setBackgroundResource(R.drawable.bg_card_choose);
            binding.builder.setBackgroundResource(R.drawable.bg_card_choose);
            binding.arpan.setBackgroundResource(R.drawable.bg_choose);
            who = "Housing Cart Broker";
        });

        binding.amenities.setOnClickListener(v->{
            dialog = new Dialog(MainActivity.this);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.amenities_select);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
            dialog.show();

            CheckBox pool = dialog.findViewById(R.id.pool);
            CheckBox parking = dialog.findViewById(R.id.parking);
            CheckBox terrace = dialog.findViewById(R.id.terrace);
            CheckBox air = dialog.findViewById(R.id.air);
            CheckBox internet = dialog.findViewById(R.id.internet);
            CheckBox balcony = dialog.findViewById(R.id.balcony);
            CheckBox cable = dialog.findViewById(R.id.cable);
            CheckBox green_zone = dialog.findViewById(R.id.green_zone);
            CheckBox church = dialog.findViewById(R.id.church);
            CheckBox dish = dialog.findViewById(R.id.dish);
            CheckBox coffee = dialog.findViewById(R.id.pot);
            CheckBox estate = dialog.findViewById(R.id.estate);

            pool.setOnClickListener(v1->{
                amenities += ",Swimming Pool";
            });

            parking.setOnClickListener(v2->{
                amenities += ",Car Parking";
            });


            terrace.setOnClickListener(v2->{
                amenities += ",Terrace";
            });

            air.setOnClickListener(v2->{
                amenities += ",Air Conditioning";
            });
            internet.setOnClickListener(v2->{
                amenities += ",Internet";
            });

            balcony.setOnClickListener(v2->{
                amenities += ",Balcony";
            });

            cable.setOnClickListener(v2->{
                amenities += ",Cable TV";
            });

            green_zone.setOnClickListener(v2->{
                amenities += ",Near Green Zone";
            });

            church.setOnClickListener(v2->{
                amenities += ",Near Church";
            });

            coffee.setOnClickListener(v2->{
                amenities += ",Coffee Pot";
            });

            estate.setOnClickListener(v2->{
                amenities += ",Near Estate";
            });

            dish.setOnClickListener(v2->{
                amenities += ",Dishwasher";
            });

        });

        cit = new ArrayAdapter(
                MainActivity.this,
                android.R.layout.simple_spinner_item,
                city_name);
        cit.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        binding.city.setAdapter(cit);


        type = new ArrayAdapter(
                MainActivity.this,
                android.R.layout.simple_spinner_item,
                property);
        type.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        binding.propertyType.setAdapter(type);


        if (binding.propertyType.getSelectedItem().toString().equals("Residential")){
            sub = sub_property_resident;
        }
        if (binding.propertyType.getSelectedItem().toString().equals("Commercial")){
            sub = sub_property_commercial;
        }
        if (binding.propertyType.getSelectedItem().toString().equals("Farms")){
            sub = sub_property_farms;
        }

        sub_type = new ArrayAdapter(

                MainActivity.this,
                android.R.layout.simple_spinner_item,
                sub);
        sub_type.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        binding.subpropertytype.setAdapter(sub_type);

        sel_bhk = new ArrayAdapter(
                MainActivity.this,
                android.R.layout.simple_spinner_item,
                selectBhk);
        sel_bhk.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        binding.selectBhk.setAdapter(sel_bhk);

        face = new ArrayAdapter(
                MainActivity.this,
                android.R.layout.simple_spinner_item,
                facing);
        face.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        binding.facing.setAdapter(face);


        binding.rent.setOnClickListener(v->{
            rent_sale = "For Rent";
            binding.rent.setChecked(true);
            binding.sale.setChecked(false);

            rs = 1;
        });

        binding.sale.setOnClickListener(v->{
            rent_sale = "For Sale";
            binding.rent.setChecked(false);
            binding.sale.setChecked(true);
            rs=2;
        });

        binding.ready.setOnClickListener(v->{
            status = "Ready to move";
            binding.ready.setChecked(true);
            binding.under.setChecked(false);
            ps =1;
        });

        binding.under.setOnClickListener(v->{
            status = "Under construction";
            binding.ready.setChecked(false);
            binding.under.setChecked(true);
            ps=2;
        });

        binding.rateYes.setOnClickListener(v->{
            rat_s = "Yes";
            binding.rateYes.setChecked(true);
            binding.rateNo.setChecked(false);
            rated=1;
        });

        binding.rateNo.setOnClickListener(v->{
            rat_s = "No";
            binding.rateNo.setChecked(true);
            binding.rateYes.setChecked(false);
            rated=2;
        });

        binding.featureYes.setOnClickListener(v->{
            feature_s = "Yes";
            binding.featureYes.setChecked(true);
            binding.featureNo.setChecked(false);
            featured = 1;
        });

        binding.featureNo.setOnClickListener(v->{
            feature_s = "No";
            binding.featureYes.setChecked(false);
            binding.featureNo.setChecked(true);
            featured = 2;
        });

        binding.submit.setOnClickListener(v->{

            if (rated!=0){
                if (featured!=0){
                    if (ps!=0){
                        if (rs!=0){
                            sendData();
                        }else {
                            Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void thumbnailImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_REQUEST);
        binding.textView5.setVisibility(View.GONE);
        binding.imageView2.setVisibility(View.GONE);
        binding.textView6.setVisibility(View.GONE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When an Image is picked
        if ((requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) || (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
        && null != data  && data.getData() != null)){
            // Get the Image from data
            if (requestCode == PICK_IMAGE_MULTIPLE) {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    int cout = 0;
                    cout = data.getClipData().getItemCount();
                    for (int i = 0; i < cout; i++) {
                        // adding imageuri in array
                        Uri imageurl = data.getClipData().getItemAt(i).getUri();
                        mArrayUri.add(imageurl);
                    }
                    // setting 1st selected image into image switcher
                    binding.imageThumb.setImageURI(mArrayUri.get(0));
                    position = 0;
                } else {
                    Uri imageurl = (Uri) data.getData();
                    mArrayUri.add(imageurl);
                    binding.imageThumb.setImageURI(mArrayUri.get(0));
                    position = 0;
                }
            }
            if (requestCode == PICK_IMAGE_REQUEST){
                filePath = data.getData();
                try {

                    // Setting image on image view using Bitmap
                    Bitmap bitmap = MediaStore
                            .Images
                            .Media
                            .getBitmap(
                                    getContentResolver(),
                                    filePath);
                    binding.imageMain.setImageBitmap(bitmap);
                }

                catch (IOException e) {
                    // Log the exception
                    e.printStackTrace();
                }
            }
        } else {
            // show this if no image is selected
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    public void sendData() {
        if (!binding.nameproperty.getText().toString().trim().equals("")) {
            if (!binding.address.getText().toString().trim().equals("")) {
                if (!binding.price.getText().toString().trim().equals("")) {
                    if (!binding.sqfeet.getText().toString().trim().equals("")) {
                        if (!binding.location.getText().toString().trim().equals("")) {
                            String url = binding.email.getText().toString().trim();
                            //String regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
                            //Matching the given phone number with regular expression
                           // boolean result = url.matches(regex);

                            if (check_Email(url)) {
                                addStudentData();
                                uploadImage();
                                uploadThumbnail();
                                dialog = new Dialog(MainActivity.this);
                                dialog.setCancelable(false);
                                dialog.setContentView(R.layout.loading);
                                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                dialog.show();
                            } else {
                                MotionToast.Companion.darkColorToast(MainActivity.this,
                                        "Error",
                                        "Please add a valid Link",
                                        MotionToast.TOAST_ERROR,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(MainActivity.this, R.font.lexend));
                            }
                        } else {
                            binding.location.setError("Empty");
                            Snackbar.make(binding.layout, "Please add a valid link", Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.parseColor("#171746"))
                                    .setTextColor(Color.parseColor("#FF7F5C"))
                                    .setBackgroundTint(Color.parseColor("#171746"))
                                    .show();
                        }
                    } else {
                        binding.sqfeet.setError("Empty");
                        Snackbar.make(binding.layout, "Please Add Property's Sq. Feet", Snackbar.LENGTH_LONG)
                                .setActionTextColor(Color.parseColor("#171746"))
                                .setTextColor(Color.parseColor("#FF7F5C"))
                                .setBackgroundTint(Color.parseColor("#171746"))
                                .show();
                    }
                } else {
                    binding.price.setError("Empty");
                    Snackbar.make(binding.layout, "Please Add Property Price", Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.parseColor("#171746"))
                            .setTextColor(Color.parseColor("#FF7F5C"))
                            .setBackgroundTint(Color.parseColor("#171746"))
                            .show();
                }
            } else {
                binding.address.setError("Empty");
                Snackbar.make(binding.layout, "Please Add Property Address", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#171746"))
                        .setTextColor(Color.parseColor("#FF7F5C"))
                        .setBackgroundTint(Color.parseColor("#171746"))
                        .show();
            }
        } else {
            binding.nameproperty.setError("Empty");
            Snackbar.make(binding.layout, "Please Add Property Name", Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.parseColor("#171746"))
                    .setTextColor(Color.parseColor("#FF7F5C"))
                    .setBackgroundTint(Color.parseColor("#171746"))
                    .show();
        }
    }

    public void uploadImage() {
        urlStrings = new ArrayList<>();
//        progressDialog.show();
        //alert.setText("If Loading Takes to long press button again");
        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("PropertyImages");

        for (upload_count = 0; upload_count < mArrayUri.size(); upload_count++) {

            Uri IndividualImage = mArrayUri.get(upload_count);
            final StorageReference ImageName = ImageFolder.child( owner_name + "_" + min_price + "/" + owner_name + "_" + upload_count  + IndividualImage.getLastPathSegment());

            ImageName.putFile(IndividualImage).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ImageName.getDownloadUrl().addOnSuccessListener(
                                    new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            urlStrings.add(String.valueOf(uri));


                                            if (urlStrings.size() == mArrayUri.size()) {
                                                storeLink(urlStrings);
                                            }

                                        }
                                    }
                            );
                        }
                    }
            );
        }

    }
    private void uploadThumbnail()
    {
        link = new ArrayList<>();
        if (filePath != null) {

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "Thumbnail/" + owner_name + "_" + min_price + "/" + owner_name  + "_Thumbnail"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    ref.getDownloadUrl().addOnSuccessListener(
                                            new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    link.add(String.valueOf(uri));
                                                    storeLink(link);

                                                }
                                            }
                                    );

                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded

                            Toast.makeText(MainActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
        }
    }

    private void storeLink(ArrayList<String> urlStrings) {

        HashMap<String, String> hashMap = new HashMap<>();

        for (int i = 0; i <urlStrings.size() ; i++) {
            hashMap.put("ImgLink"+i, urlStrings.get(i));

        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("DATA").child(owner_name.replace(" ","") + "_" + min_price);

        databaseReference.push().setValue(hashMap)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                ).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        //progressDialog.dismiss();
        mArrayUri.clear();
    }
        public static boolean check_Email(String str) {
            /*try {
                new URL(str).toURI();
                return true;
            }catch (Exception e) {
                return false;
            }*/
            return android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches();
        }

    public void addStudentData(){


        name = binding.nameproperty.getText().toString().trim();
        address = binding.address.getText().toString().trim();
        area = binding.location.getText().toString().trim();
        min_price = binding.price.getText().toString().trim();
        max_price = binding.maxPrice.getText().toString().trim();
        owner_name = binding.ownerName.getText().toString().trim();
        number = binding.number.getText().toString().trim();
        sq_feet = binding.sqfeet.getText().toString().trim();
        year = binding.yrBuilt.getText().toString().trim();
        description = binding.description.getText().toString().trim();
        email = binding.email.getText().toString().trim();

        String city_spin = binding.city.getSelectedItem().toString();
        String type_spin = binding.propertyType.getSelectedItem().toString();
        String subType_spin = binding.subpropertytype.getSelectedItem().toString();
        String bhk_spin = binding.selectBhk.getSelectedItem().toString();
        String state = binding.facing.getSelectedItem().toString();

        StringRequest request = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbyWJM-A1WlH-KCifNpFYnuDntBLrjmrGuJ-9ROsYH_AhMNhfzIkGJ8RkDCXFW7jhcYXkQ/exec", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

         /*       Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);*/
                dialog.hide();
                dialog1 = new Dialog(MainActivity.this);
                dialog1.setCancelable(false);
                dialog1.setContentView(R.layout.done);
                dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog1.show();

                binding.ownerName.setText("");
                binding.nameproperty.setText("");
                binding.number.setText("");
                binding.address.setText("");
                binding.price.setText("");
                binding.maxPrice.setText("");
                binding.sqfeet.setText("");
                binding.yrBuilt.setText("");
                binding.description.setText("");

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog1.dismiss();
                    }

                },1500);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                params.put("action","addProperty");

                params.put("vWho",who);
                params.put("vOwnerName",owner_name);
                params.put("vPhone",number);
                params.put("vEmail",email);
                params.put("vTypeSpin",type_spin);
                params.put("vSubTypeSpin",subType_spin);
                params.put("vName",name);
                params.put("vDescription",description);
                params.put("vState",state);
                params.put("vCitySpin",city_spin);
                params.put("vArea",area);
                params.put("vAddress",address);
                params.put("vRent",rent_sale);
                params.put("vStatus",status);
                params.put("vRated",rat_s);
                params.put("vFeature",feature_s);
                params.put("vMinprice",min_price);
                params.put("vMaxprice",max_price);
                params.put("vSqFeet",sq_feet);
                params.put("vYear",year);
                params.put("vBhkSpin",bhk_spin);
                params.put("vAmenities",amenities);


                /*params.put("vLocation",location);




                params.put("vBedroom",bedroom);




                params.put("vFacingSpin",facing_spin);



                params.put("vLength",length);
                params.put("vBreadth",breadth);
                params.put("vLiving",living);*/


                return params;
            }
        };

        int socket = 5000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socket,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add((request));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void propertyImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
        binding.thumb.setVisibility(View.GONE);
        binding.textView23.setVisibility(View.GONE);
        binding.textView86.setVisibility(View.GONE);
    }

}