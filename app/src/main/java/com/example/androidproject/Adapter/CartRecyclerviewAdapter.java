package com.example.androidproject.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.Interface.OnDataSetChangedListener;
import com.example.androidproject.Model.AddToCart;
import com.example.androidproject.Model.Products;
import com.example.androidproject.R;
import com.example.androidproject.Singleton.SignInSingleton;
import com.example.androidproject.ViewHolder.CartViewHolder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartRecyclerviewAdapter extends RecyclerView.Adapter<CartViewHolder> {

    Context context;
    List<AddToCart> items;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private OnDataSetChangedListener dataChangedListener;

    /*public interface OnDataSetChangedListener {
        void onDataSetChanged();
    }*/

    public void setOnDataSetChangedListener(OnDataSetChangedListener listener) {
        this.dataChangedListener = listener;
    }

    public CartRecyclerviewAdapter(Context context, List<AddToCart> items) {
        this.context = context;
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(context).inflate(R.layout.cart_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        //holder.cartId.setText(items.get(position).getName());
        String cartId = items.get(position).getCartID();
        String cartProductId = items.get(position).getCartProductID();
        String cartProductQty = items.get(position).getCartProductQty();

        holder.cartProductName.setText(items.get(position).getCartProductName());
        holder.cartProductPrice.setText("Price: " + items.get(position).getCartProductPrice());
        holder.cartProductQty.setText("Qty: " + items.get(position).getCartProductQty());
        //holder.cartImage.setText(items.get(position).getCartProductImage());

        AddToCart cart = items.get(position);
        holder.bind(cart);

        //to make recycleview clickable
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Clicked!", Toast.LENGTH_SHORT).show();
                showAddToCartDialog(context, cartProductId, cartProductQty);

            }
        });

        holder.deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCart(cartId, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void showAddToCartDialog(Context context, String productId, String productQty) {

        databaseReference = firebaseDatabase.getReference("carts");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Inflate the layout XML file for the AlertDialog
        View formView = LayoutInflater.from(context).inflate(R.layout.add_to_cart_popup, null);
        builder.setView(formView);

        EditText quantityEditText = formView.findViewById(R.id.txt_qty);
        Button buttonAdd = formView.findViewById(R.id.btnAdd);
        Button buttonCancel = formView.findViewById(R.id.btnCancel);
        ImageButton buttonMinus = formView.findViewById(R.id.buttonMinus);
        ImageButton buttonPlus = formView.findViewById(R.id.buttonPlus);

        quantityEditText.setText(productQty);

        buttonMinus.setOnClickListener(new CartRecyclerviewAdapter.QuantityButtonClickListener(quantityEditText));
        buttonPlus.setOnClickListener(new CartRecyclerviewAdapter.QuantityButtonClickListener(quantityEditText));

        AlertDialog alertDialog = builder.create();
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //boolean isProductExists = false;

                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String cartProductId = childSnapshot.child("cartproductid").getValue(String.class);
                            String cartProductQty = childSnapshot.child("cartqty").getValue(String.class);
                            String cartUserid = childSnapshot.child("cartuserid").getValue(String.class);
                            String childKeyCart = childSnapshot.getKey();

                            if (cartProductId != null && cartProductId.equals(productId) && cartUserid.equals(SignInSingleton.getInstance().getAuthUserId())) {
                                //isProductExists = true;

                                Map<String, Object> formData = new HashMap<>();
                                formData.put("cartid", childKeyCart);
                                formData.put("cartproductid", productId);
                                formData.put("cartuserid", SignInSingleton.getInstance().getAuthUserId());
                                formData.put("cartqty", quantityEditText.getText().toString());

                                // Update the cart entry in the "carts" table
                                DatabaseReference cartEntryReference = databaseReference.child(childKeyCart);
                                cartEntryReference.setValue(formData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Cart update successful
                                                alertDialog.dismiss();
                                                Toast.makeText(context, "Successfully updated cart.", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Handle update failure
                                                alertDialog.dismiss();
                                                Toast.makeText(context, "Failed to update cart.", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Handle any errors
                    }
                });
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public class QuantityButtonClickListener implements View.OnClickListener {
        private EditText quantityEditText;

        public QuantityButtonClickListener(EditText quantityEditText) {
            this.quantityEditText = quantityEditText;
        }

        @Override
        public void onClick(View v) {
            int quantity = Integer.parseInt(quantityEditText.getText().toString());

            switch (v.getId()) {
                case R.id.buttonMinus:
                    if (quantity > 1) {
                        quantity--;
                    }
                    break;

                case R.id.buttonPlus:
                    quantity++;
                    break;
            }
            quantityEditText.setText(String.valueOf(quantity));
        }
    }

    public void deleteProductFromCart(String cartid, int position) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("carts").child(cartid);
        databaseReference.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Deletion successful
                        Toast.makeText(context, "Successfully removed.", Toast.LENGTH_SHORT).show();
                        removeItem(position);

                        if (dataChangedListener != null) {
                            dataChangedListener.onDataSetChanged(); // Notify the fragment to refresh its data
                        }

                        //alertDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors
                        Toast.makeText(context, "Failed to remove.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void deleteCart(String cartid, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure you want to delete this from your cart?");
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProductFromCart(cartid, position);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < items.size()) {
            items.remove(position);
            notifyDataSetChanged();
        }
    }
}
