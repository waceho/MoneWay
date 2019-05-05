package com.moneway.test.ui.home;

import android.os.Bundle;
import android.view.View;

import com.moneway.test.R;
import com.moneway.test.base.BaseFragment;
import com.moneway.test.data.model.Repositorie;
import com.moneway.test.databinding.FragmentHomeListBinding;
import com.moneway.test.ui.detail.DetailsFragment;
import com.moneway.test.ui.detail.DetailsViewModel;
import com.moneway.test.ui.main.MainActivity;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

public class HomeFragment extends BaseFragment implements RepositorieSelectListener {

    @Inject
    ViewModelProvider.Factory factoryViewModel;

    private HomeViewModel viewModel;
    private FragmentHomeListBinding binding;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_home_list;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // init binding
        binding = bind(view);
        // init viewModel
        viewModel = ViewModelProviders.of(this, factoryViewModel).get(HomeViewModel.class);
        // config recyclerView
        configRecyclerView();
        // set observer
        observableViewModel();

        MainActivity activity = (MainActivity)getActivity();
        if (activity != null) {
            activity.canDisplayHomeUp();
        }
    }

    /**
     * config repositories recyclerView
     */
    private void configRecyclerView() {
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getBaseActivity(), DividerItemDecoration.VERTICAL));
        binding.recyclerView.setAdapter(new RepositorieListAdapter(viewModel, this, this));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     *
     * @param view this fragment view
     * @return FragmentHomeList Binding
     */
    private FragmentHomeListBinding bind(View view) {
        return FragmentHomeListBinding.bind(view);
    }

    @Override
    public void onRepoSelected(Repositorie repositorie) {
        showDetails(repositorie);
    }

    /**
     * show selected repositorie details
     **/
    private void showDetails(Repositorie repositorie) {
        DetailsViewModel detailsViewModel = ViewModelProviders.of(getBaseActivity(), factoryViewModel).get(DetailsViewModel.class);
        detailsViewModel.setRepositorie(repositorie);
        getBaseActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainScreenContainer, new DetailsFragment())
                .addToBackStack(null).commit();
    }

    /**
     * set observable viewModel
     **/
    private void observableViewModel() {
        // set repositories observer
        setRepositoriesObserver();
        // set Error observer
        setErrorObserver();
        // set Loading observer
        setLoadingObserver();
    }

    private void setRepositoriesObserver() {
        viewModel.getRepos().observe(this, repos -> {
            if (repos != null) binding.recyclerView.setVisibility(View.VISIBLE);
        });
    }

    private void setErrorObserver() {
        viewModel.getError().observe(this, isError -> {
            if (isError != null)
                if (isError) {
                    binding.tvError.setVisibility(View.VISIBLE);
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.tvError.setText("An Error Occurred While Loading Data!");
                } else {
                    binding.tvError.setVisibility(View.GONE);
                    binding.tvError.setText(null);
                }
        });
    }

    private void setLoadingObserver() {
        viewModel.getLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                binding.loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if (isLoading) {
                    binding.tvError.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.GONE);
                }
            }
        });
    }
}
