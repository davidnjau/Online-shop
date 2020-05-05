package com.centafrique.homework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class AllOrders extends AppCompatActivity {

    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private String buyer_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders);

        recyclerView = findViewById(R.id.RecyclerView);
        linearLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);


    }


    @Override
    protected void onStart() {
        super.onStart();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            buyer_id = extras.getString("product_id");

            fetchData(buyer_id);

        }else {

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        adapter.startListening();
    }

    private void fetchData(String txtBuyerId) {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("cart_items").child(txtBuyerId);
        //                .child("cart_items").child();


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

        adapter = new FirebaseRecyclerAdapter<Products, AllOrders.ViewHolder>(options) {
            @Override
            public AllOrders.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.products_items, parent, false);

                return new AllOrders.ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(AllOrders.ViewHolder holder, final int position, Products model) {
                holder.setName(model.getName());
                holder.setPrice(model.getPrice());
                holder.setMain_image(model.getMain_image());

                final String listPostKey = getRef(position).getKey();


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

            Picasso.with(getApplicationContext()).load(uri).into(imageView, new Callback() {
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
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
