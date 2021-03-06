package com.shuppa.ui.bottomviews.category;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.Query;
import com.shuppa.R;
import com.shuppa.data.model.Category;
import com.shuppa.utils.Globals;
import com.shuppa.utils.Vars;
import com.shuppa.viewholders.CategoryViewHolder;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class CategoryFragment extends Fragment {
    private static final String TAG = "CategoryFragment";
    private Vars vars;
    private Category category;
    private FirestorePagingAdapter<Category, CategoryViewHolder> adapter;
    private GridLayoutManager gridLayoutManager;
    private NavController navController;
    private RecyclerView categoryRecycler;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_category, container, false);
        vars = new Vars(requireActivity());
        ButterKnife.bind(this, root);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        categoryRecycler = root.findViewById(R.id.shop_category_recycler);
        gridLayoutManager = new GridLayoutManager(requireActivity(), 3);
        categoryRecycler.setLayoutManager(gridLayoutManager);

        populateCategories();
        return root;
    }

    @OnClick(R.id.shop_by_brands)
    void shopByBrand() {
        navController.navigate(R.id.navigation_brands);
    }

    private void populateCategories() {
        Log.d(TAG, "populateCategories called: ");
        Query catQuery = vars.verityApp.db
                .collection(Globals.CATEGORIES)
                .orderBy("name");

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        FirestorePagingOptions<Category> options = new FirestorePagingOptions.Builder<Category>()
                .setLifecycleOwner(this)
                .setQuery(catQuery, config, snapshot -> {
                    category = snapshot.toObject(Category.class);
                    assert category != null;
                    category.setUuid(snapshot.getId());
                    return category;
                })
                .build();

        adapter = new FirestorePagingAdapter<Category, CategoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull Category model) {
                holder.bindCategory(model);

                holder.itemView.setOnClickListener( v -> {

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Globals.CATEGORY_OBJ, model);
                    navController.navigate(R.id.navigation_products, bundle);
                });
            }

            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_shop_category, parent, false);
                return new CategoryViewHolder(view, getContext());
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
        categoryRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}