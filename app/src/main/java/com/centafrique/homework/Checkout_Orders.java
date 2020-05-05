package com.centafrique.homework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Checkout_Orders extends AppCompatActivity {

    DatabaseReference mDatabaseCart, mDatabaseOrders;
    private String mCurrentUserId;
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private FirebaseRecyclerAdapter adapter;

    private List<String> myPrice = new ArrayList<>();

    private TextView tvSubtotal;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout__orders);

        recyclerView = findViewById(R.id.RecyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        preferences = getApplicationContext().getSharedPreferences("Nims", MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        tvSubtotal = findViewById(R.id.tvSubtotal);

        mAuth = FirebaseAuth.getInstance();

        mCurrentUserId = mAuth.getUid();

        mDatabaseCart = FirebaseDatabase.getInstance().getReference().child("cart_items").child(mCurrentUserId);

//        String txtAdminUid = getResources().getString(R.string.admin_uid);
        mDatabaseOrders = FirebaseDatabase.getInstance().getReference().child("order_placed").child(mAuth.getCurrentUser().getUid());


        findViewById(R.id.btnCheckout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(Checkout_Orders.this, "Order placed", Toast.LENGTH_SHORT).show();

                mDatabaseOrders.child("email").setValue(mAuth.getCurrentUser().getEmail());
                mDatabaseOrders.child("uid").setValue(mAuth.getCurrentUser().getUid());

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        fetchData();
        GetTotal();
    }

    private void fetchData() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("cart_items").child(mCurrentUserId);
//                .child("products");

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(query, new SnapshotParser<Products>() {
                            @NonNull
                            @Override
                            public Products parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Products(

                                        Objects.requireNonNull(snapshot.child("name").getValue()).toString(),
                                        Objects.requireNonNull(snapshot.child("price").getValue()).toString(),
                                        Objects.requireNonNull(snapshot.child("main_image").getValue()).toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Products, Checkout_Orders.ViewHolder>(options) {
            @Override
            public Checkout_Orders.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.checkout_order, parent, false);

                return new Checkout_Orders.ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(Checkout_Orders.ViewHolder holder, final int position, Products model) {
                holder.setName(model.getName());
                holder.setPrice(model.getPrice());
                holder.setMain_image(model.getMain_image());

                final String listPostKey = getRef(position).getKey();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getApplicationContext(), OrderPage.class);
                        intent.putExtra("product_id", listPostKey);
                        startActivity(intent);

                    }
                });
            }

        };
        recyclerView.setAdapter(adapter);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView tvName;
        public TextView tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }

        public void setName(String string) {
            tvName.setText(string);
        }


        public void setPrice(String string) {
            tvPrice.setText(string);
        }

        public void setMain_image(final String uri){

            Picasso.with(getApplicationContext()).load(uri)
                    .placeholder(R.drawable.ic_action_image)
                    .error(R.drawable.ic_action_descr).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(getApplicationContext()).load(uri).into(imageView);
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();



    }

    private void GetTotal() {

        mDatabaseCart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try{

                    int sum = 0;

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        String txtPrice = String.valueOf(postSnapshot.child("price").getValue());
                        myPrice.add(txtPrice);

                    }

                    for (int i = 0; i<myPrice.size(); i++){

                        String ttx = myPrice.get(i);

                        int num = Integer.parseInt(ttx);

                        sum = num + sum;


                    }


                    tvSubtotal.setText(String.valueOf(sum));


                }catch (Exception e){
                    Log.e("-*-*- ", e.toString());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }}
