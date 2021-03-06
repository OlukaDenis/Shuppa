package com.shuppa.ui.brands;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.Query;
import com.shuppa.MainActivity;
import com.shuppa.R;
import com.shuppa.data.model.Product;
import com.shuppa.data.model.Variable;
import com.shuppa.ui.bottomviews.shop.ShopViewModel;
import com.shuppa.utils.Globals;
import com.shuppa.utils.Vars;
import com.shuppa.viewholders.ProductViewHolder;
import com.shuppa.viewholders.VariableViewHolder;

import java.util.HashMap;
import java.util.Map;

public class BrandProductsFragment extends Fragment {

    private static final String TAG = "BrandProductsFragment";
    private Vars vars;
    private Product product;
    private RecyclerView productRecycler;
    private FirestorePagingAdapter<Product, ProductViewHolder> adapter;
    private FirestorePagingAdapter<Variable, VariableViewHolder> variableAdapter;
    private LinearLayoutManager layoutManager;
    private ProgressDialog loading;

    private NavController navController;
    private BadgeDrawable badgeDrawable;
    private BottomNavigationView bottomNav;

    private ShopViewModel shopViewModel;

    private PagedList.Config config;
    private Variable variable;

    private int modifiedAmount;
    private Map<String, Object> cartPath;
    private String brandName;

    public BrandProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_brand_products, container, false);

        vars = new Vars(requireActivity());

        layoutManager = new LinearLayoutManager(requireActivity());
        productRecycler = root.findViewById(R.id.products_recycler);
        productRecycler.setLayoutManager(layoutManager);

        Bundle bundle = getArguments();
        assert bundle != null;
        brandName = bundle.getString(Globals.SELECTED_BRAND_OBJ);
        if (brandName != null) {
            getActionBar().setTitle(brandName);
        }

        config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        cartPath = new HashMap<>();
        cartPath.put("name", "Cart");

        populateProducts();

        return root;
    }

    private ActionBar getActionBar() {
        return ((MainActivity) requireActivity()).getSupportActionBar();
    }

    private void populateProducts() {

        Query catQuery = vars.verityApp.db
                .collectionGroup(Globals.PRODUCTS)
                .whereEqualTo("brand", brandName);

        FirestorePagingOptions<Product> options = new FirestorePagingOptions.Builder<Product>()
                .setLifecycleOwner(this)
                .setQuery(catQuery, config, snapshot -> {
                    product = snapshot.toObject(Product.class);
                    assert product != null;
                    product.setUuid(snapshot.getId());
                    return product;
                })
                .build();

        adapter = new FirestorePagingAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {
                holder.bindProduct(model);
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_products, parent, false);
                return new ProductViewHolder(view, vars, requireActivity());
            }

            @Override
            protected void onError(@NonNull Exception e) {
                super.onError(e);
                Toast.makeText(requireActivity(), "Error", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:

                        break;

                    case LOADING_MORE:
//                        mShimmerViewContainer.setVisibility(View.VISIBLE);
                        break;

                    case LOADED:
//                        mShimmerViewContainer.setVisibility(View.GONE);
                        notifyDataSetChanged();
                        break;

                    case ERROR:
                        Toast.makeText(requireActivity(), "Error", Toast.LENGTH_SHORT).show();

//                        mShimmerViewContainer.setVisibility(View.GONE);
                        break;

                    case FINISHED:
//                        mShimmerViewContainer.setVisibility(View.GONE);
                        break;
                }
            }
        };
        productRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}