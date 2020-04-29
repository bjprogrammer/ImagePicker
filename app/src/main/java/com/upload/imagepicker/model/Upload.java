package com.upload.imagepicker.model;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.upload.imagepicker.BR;
import com.upload.imagepicker.R;
import com.wang.avi.AVLoadingIndicatorView;

public class Upload extends BaseObservable {
    private String mName;
    private String mImageUrl;

    public Upload(){}

    public Upload(String name, String imageUrl) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mName = name;
        mImageUrl = imageUrl;
    }

    @Bindable
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
        notifyPropertyChanged(BR.imageUrl);
    }

    @BindingAdapter({"bind:url", "bind:spinner"})
    public static void loadImage(ImageView view, String url, AVLoadingIndicatorView spinner)
    {
        spinner.show();
        spinner.setVisibility(View.VISIBLE);
        Glide.with(view.getContext())
                .load(url)
                .thumbnail(0.1f)
                .apply(new RequestOptions().error(R.drawable.no_image))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        spinner.hide();
                        spinner.setVisibility(View.GONE);
                        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        spinner.hide();
                        spinner.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(view);
    }

}