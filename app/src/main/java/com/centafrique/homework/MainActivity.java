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
import android.widget.TableLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private TableLayout tableAdminPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tableAdminPanel = findViewById(R.id.tableAdminPanel);

        recyclerView = findViewById(R.id.RecyclerView);
        linearLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        fetchData();

        findViewById(R.id.linearAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), UploadProduct.class));

            }
        });

        findViewById(R.id.btnOrders).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), UserOrders.class));

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if (auth == null){

            Intent intent = new Intent(getApplicationContext(), Registration.class);
            startActivity(intent);
            finish();

        }else {

            if (auth.getEmail().equals(getResources().getString(R.string.admin_email)) ){
                tableAdminPanel.setVisibility(View.VISIBLE);

            }else {
                tableAdminPanel.setVisibility(View.GONE);

            }

            adapter.startListening();
        }
    }

    private void fetchData() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("products");

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

        adapter = new FirebaseRecyclerAdapter<Products, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.products_items, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, Products model) {
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
