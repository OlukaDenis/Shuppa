package com.shuppa.ui.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shuppa.R;
import com.shuppa.data.adapters.BrandSearchAdapter;
import com.shuppa.data.model.Product;
import com.shuppa.utils.Globals;
import com.shuppa.utils.Vars;
import com.shuppa.viewholders.ProductViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private Vars vars;
    private Product product;

    private LinearLayoutManager layoutManager;
    private RecyclerView searchRecycler;

    private LinearLayoutManager brandLayoutManager;
    private RecyclerView brandSearchRecycler;

    private FirestoreRecyclerAdapter<Product, ProductViewHolder> searchAdapter;

    private List<String> brandSearchList;
    private static final String BRAND_FIELD = "brand";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        vars = new Vars(this);

        brandSearchList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        searchRecycler = findViewById(R.id.product_search_recycler);
        searchRecycler.setLayoutManager(layoutManager);

        brandSearchRecycler = findViewById(R.id.brand_search_recycler);
        brandLayoutManager = new LinearLayoutManager(this);
        brandSearchRecycler.setLayoutManager(brandLayoutManager);

        Toolbar toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_searchview);
        SearchView searchView = (SearchView) searchItem.getActionView();
        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        searchIcon.setImageDrawable(null);
        searchView.setQueryHint("Search");
        searchView.setIconifiedByDefault(false);
        searchItem.expandActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProducts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchProducts(newText);
                return false;
            }
        });

        return true;
    }

    private void searchProducts(String text) {

        if (text.length() > 0) {
            text = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
            searchByBrand(text);
            searchByName(text);
        }
    }

    private void searchByBrand(String brand) {
        brandSearchList.clear();
        Query searchQuery = vars.verityApp.db
                .collectionGroup(Globals.PRODUCTS)
                .orderBy(BRAND_FIELD)
                .whereGreaterThanOrEqualTo(BRAND_FIELD, brand)
                .whereLessThanOrEqualTo(BRAND_FIELD, brand + "\uf8ff");

        searchQuery.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Product prd = document.toObject(Product.class);

                            if (!brandSearchList.contains(prd.getBrand())) {
                                brandSearchList.add(prd.getBrand());
                            }
                        }
                        populateBrandSearchResults();
                        Log.d(TAG, "searchByBrand: "+brandSearchList);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "fetchBrands: ",e );
                    vars.verityApp.crashlytics.recordException(e);
                });
    }

    private void populateBrandSearchResults() {
        BrandSearchAdapter brandSearchAdapter = new BrandSearchAdapter(brandSearchList, this);
        brandSearchRecycler.setAdapter(brandSearchAdapter);
        brandSearchAdapter.notifyDataSetChanged();
    }

    private void searchByName(String name) {
        Query searchQuery = vars.verityApp.db
                .collectionGroup(Globals.PRODUCTS)
                .orderBy("name")
                .startAt(name)
                .endAt(name+"\uf8ff");

        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(searchQuery, snapshot -> {
                    product = snapshot.toObject(Product.class);
                    assert product != null;
                    product.setUuid(snapshot.getId());
                    return product;
                })
                .build();

        searchAdapter = new FirestoreRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {
                holder.bindProduct(model);
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_products, parent, false);
                return new ProductViewHolder(view, vars, SearchActivity.this);
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
                super.onError(e);
                Log.e(TAG, "onError: ", e);
                vars.verityApp.crashlytics.recordException(e);
            }
        };
        searchRecycler.setAdapter(searchAdapter);
        searchAdapter.startListening(); //connects to firebase collection
        searchAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}