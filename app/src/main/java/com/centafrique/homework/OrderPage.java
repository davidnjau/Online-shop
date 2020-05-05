package com.centafrique.homework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class OrderPage extends AppCompatActivity {

    private String product_id, mCurrentUserId;
    private DatabaseReference mDatabase;
    private Bundle extras;

    private ImageView image_main;

    private TextView tvDesc,tvName,txtQuantity;

    private DatabaseReference mDatabaseCart, mDatabaseSave;
    private FirebaseAuth mAuth;
    private Button btnCart;

    private Boolean CartDataIsIn = false;

    private String txtName, txDesc, txtPrice, txtMainImage;
    int QuantityCounter=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page);

        extras = getIntent().getExtras();

        txtQuantity = findViewById(R.id.txtQuantity);

        image_main = findViewById(R.id.image_main);

        tvDesc = findViewById(R.id.tvDesc);
        tvName = findViewById(R.id.tvName);

        mDatabaseCart = FirebaseDatabase.getInstance().getReference().child("cart_items");
        mDatabaseSave = FirebaseDatabase.getInstance().getReference().child("collections");

        mAuth = FirebaseAuth.getInstance();

        mCurrentUserId = mAuth.getUid();

        findViewById(R.id.btnCheckout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference newPost = mDatabaseCart.child(mCurrentUserId).child(product_id);
                newPost.child("main_image").setValue(txtMainImage);
                newPost.child("name").setValue(txtName);

                String txtTotalPrice = txtQuantity.getText().toString();

                newPost.child("price").setValue(txtTotalPrice);


                startActivity(new Intent(getApplicationContext(), Checkout_Orders.class));

            }
        });

        btnCart =findViewById(R.id.btnCart);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCurrentUserId != null){

                    if (CartDataIsIn){

                        mDatabaseCart.child(mCurrentUserId).child(product_id).removeValue();
                        btnCart.setText("Add to cart");

                        Toast.makeText(OrderPage.this, "Product removed from cart..", Toast.LENGTH_SHORT).show();

                    }else {

                        DatabaseReference newPost = mDatabaseCart.child(mCurrentUserId).child(product_id);
                        newPost.child("main_image").setValue(txtMainImage);
                        newPost.child("name").setValue(txtName);

                        String txtTotalPrice = txtQuantity.getText().toString();

                        newPost.child("price").setValue(txtTotalPrice);


                        btnCart.setText("Remove from cart");

                        Toast.makeText(OrderPage.this, "Product added to cart..", Toast.LENGTH_SHORT).show();

                    }

                }


            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


        if (extras != null) {

            product_id = extras.getString("product_id");
            mDatabase = FirebaseDatabase.getInstance().getReference().child("products").child(product_id);

            GetData();
            GetPastData();

        }else {

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }



    }

    private void GetPastData() {

        mDatabaseCart.child(mCurrentUserId).orderByKey().equalTo(product_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    //Key exists

                    CartDataIsIn = true;

                    btnCart.setText("Remove from cart");


                } else {

                    CartDataIsIn = false;

                    //Key does not exist
                    btnCart.setText("Add to cart");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });




    }

    private void GetData() {

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                txtName = String.valueOf(dataSnapshot.child("name").getValue());
                txDesc = String.valueOf(dataSnapshot.child("desc").getValue());
                txtPrice = String.valueOf(dataSnapshot.child("price").getValue());

                txtMainImage = String.valueOf(dataSnapshot.child("main_image").getValue());

                Picasso.with(getApplicationContext()).load(txtMainImage).into(image_main);

                tvDesc.setText(txDesc);
                txtQuantity.setText(txtPrice);
                tvName.setText(txtName);

                GetOrderedItems();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void GetOrderedItems() {

        mDatabaseCart.child(mCurrentUserId).child(product_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String txtTotalPrice = String.valueOf(dataSnapshot.child("price").getValue());

                if (!txtTotalPrice.equals("null")) {

                    QuantityCounter = 1;
                    int txtQnty = Integer.parseInt(txtTotalPrice) / Integer.parseInt(txtPrice);
                    txtQuantity.setText(txtTotalPrice);
                }else {
                    QuantityCounter = 1;

                    txtQuantity.setText(txtPrice);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
