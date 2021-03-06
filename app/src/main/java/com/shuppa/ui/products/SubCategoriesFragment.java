package com.shuppa.ui.products;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.Query;
import com.shuppa.MainActivity;
import com.shuppa.R;
import com.shuppa.data.model.Product;
import com.shuppa.data.model.SubCategory;
import com.shuppa.utils.Globals;
import com.shuppa.utils.Vars;
import com.shuppa.viewholders.ProductViewHolder;

public class SubCategoriesFragment extends Fragment {
    private static final String TAG = "SubCategoriesFragment";
    private PagedList.Config config;

    private Vars vars;
    private RecyclerView productRecycler;
    private FirestorePagingAdapter<Product, ProductViewHolder> adapter;
    private LinearLayoutManager productsLayoutManager;

    private SubCategory subCategory;
    private Product product;
    private String categoryID;
    private String categoryName;

    public SubCategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_sub_categories, container, false);

        vars = new Vars(requireContext());

        Bundle bundle = getArguments();
        assert bundle != null;
        subCategory = (SubCategory) bundle.getSerializable(Globals.SUB_CATEGORY_OBJ);

        categoryID = Globals.CATEGORY_ID;
        categoryName = Globals.CATEGORY_NAME;
        getActionBar().setTitle(subCategory.getName());

        productsLayoutManager = new LinearLayoutManager(requireActivity());
        productRecycler = root.findViewById(R.id.products_recycler);
        productRecycler.setLayoutManager(productsLayoutManager);

        config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        populateProducts();

        return root;
    }

    private ActionBar getActionBar() {
        return ((MainActivity) requireActivity()).getSupportActionBar();
    }

    private void populateProducts() {
        Log.d(TAG, "populateProducts called");

        Query categoryQuery = vars.verityApp.db
                .collection(Globals.CATEGORIES)
                .document(categoryID)
                .collection(Globals.SUB_CATEGORIES)
                .document(subCategory.getUuid())
                .collection(Globals.PRODUCTS);

        FirestorePagingOptions<Product> options = new FirestorePagingOptions.Builder<Product>()
                .setLifecycleOwner(this)
                .setQuery(categoryQuery, config, snapshot -> {
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