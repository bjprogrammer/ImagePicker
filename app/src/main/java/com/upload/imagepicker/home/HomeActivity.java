package com.upload.imagepicker.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.github.loadingview.LoadingView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.upload.imagepicker.R;
import com.upload.imagepicker.databinding.ActivityMainBinding;
import com.upload.imagepicker.detail.DetailActivity;
import com.upload.imagepicker.upload.ImagePickerActivity;
import com.upload.imagepicker.upload.PickerOptionListener;
import com.upload.imagepicker.utils.HelperFunction;
import com.upload.imagepicker.utils.Status;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.upload.imagepicker.utils.Constants.GRID_SIZE;
import static com.upload.imagepicker.utils.Constants.IMAGE_URL;
import static com.upload.imagepicker.utils.Constants.INTENT_ASPECT_RATIO_X;
import static com.upload.imagepicker.utils.Constants.INTENT_ASPECT_RATIO_Y;
import static com.upload.imagepicker.utils.Constants.INTENT_BITMAP_MAX_HEIGHT;
import static com.upload.imagepicker.utils.Constants.INTENT_BITMAP_MAX_WIDTH;
import static com.upload.imagepicker.utils.Constants.INTENT_IMAGE_PICKER_OPTION;
import static com.upload.imagepicker.utils.Constants.INTENT_LOCK_ASPECT_RATIO;
import static com.upload.imagepicker.utils.Constants.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT;
import static com.upload.imagepicker.utils.Constants.REQUEST_GALLERY_IMAGE;
import static com.upload.imagepicker.utils.Constants.REQUEST_IMAGE;
import static com.upload.imagepicker.utils.Constants.REQUEST_IMAGE_CAPTURE;

public class HomeActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private HomeViewModel homeViewModel;
    private ActivityMainBinding binding;
    private RecyclerView postList;
    private HomeAdapter mAdapter;
    private LoadingView progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        homeViewModel= new ViewModelProvider(this).get(HomeViewModel.class);

        HelperFunction.clearCache(this);
        attachViews();
        attachListners();
        configRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();

        getImages();
    }

    private void attachViews() {
        fab = binding.fab;
        postList = binding.imageList;
        progressBar = binding.loadingView;
        swipeRefreshLayout = binding.swipeRefreshLayout;
        emptyView = binding.emptyView;

        Toolbar toolbar = binding.toolbar;
        toolbar.setTitle("Uploaded List");
        setSupportActionBar(toolbar);
    }

    private void attachListners(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(HomeActivity.this)
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    showImagePickerOptions();
                                }

                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getImages();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }

    private void configRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, GRID_SIZE);
        postList.setLayoutManager(gridLayoutManager);
        postList.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new HomeAdapter();
        postList.setAdapter(mAdapter);
    }

    private void getImages() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.start();

        homeViewModel.getImages().observe(this, sampleResults -> {
            progressBar.setVisibility(View.GONE);
            progressBar.stop();

            if (sampleResults.getStatus() == Status.ERROR) {
                Toasty.error(this, sampleResults.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                if (sampleResults.getData().isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);

                    mAdapter.clear();
                    mAdapter.notifyDataSetChanged();

                } else {
                    emptyView.setVisibility(View.GONE);

                    mAdapter.updateList(sampleResults.getData());
                    mAdapter.notifyDataSetChanged();
                    mAdapter.setOnCustomCardClickListener(url -> {
                        Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
                        intent.putExtra(IMAGE_URL, url);
                        startActivity(intent);
                        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                    });
                }
            }
        });
    }


    private void showImagePickerOptions() {
        HelperFunction.showImagePickerOptions(this, new PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(INTENT_IMAGE_PICKER_OPTION, REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(INTENT_IMAGE_PICKER_OPTION,REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                    if (uri != null) {
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.start();


                        homeViewModel.uploadImage(uri).observe(this, sampleResults -> {
                            progressBar.setVisibility(View.GONE);
                            progressBar.stop();

                            if (sampleResults.getStatus() == Status.ERROR) {
                                Toasty.error(this, sampleResults.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toasty.success(this, "Upload successful", Toast.LENGTH_SHORT).show();

                            }
                        });
                    } else {
                        Toasty.error(this, "No file selected", Toast.LENGTH_SHORT).show();
                    }
            }
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}
