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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UserOrders extends AppCompatActivity {

    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_orders);

        recyclerView = findViewById(R.id.RecyclerView);
        linearLayoutManager = new LinearLayoutManager(UserOrders.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        fetchData();
    }

    @Override
    protected void onStart() {
        super.onStart();

        adapter.startListening();
    }

    private void fetchData() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("order_placed");

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, new SnapshotParser<User>() {
                            @NonNull
                            @Override
                            public User parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new User(

                                        Objects.requireNonNull(snapshot.child("email").getValue()).toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<User, UserOrders.ViewHolder>(options) {
            @Override
            public UserOrders.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.buyers, parent, false);

                return new UserOrders.ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(UserOrders.ViewHolder holder, final int position, User model) {
                holder.setEmail(model.getEmail());

                final String listPostKey = getRef(position).getKey();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getApplicationContext(), AllOrders.class);
                        intent.putExtra("product_id", listPostKey);
                        startActivity(intent);

                    }
                });

            }

        };
        recyclerView.setAdapter(adapter);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);

        }

        public void setEmail(String string) {
            tvName.setText(string);
        }



    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
