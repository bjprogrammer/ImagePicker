package com.upload.imagepicker.home;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.upload.imagepicker.model.Upload;
import com.upload.imagepicker.utils.Resource;

import java.util.List;


public class HomeViewModel extends ViewModel {
    private MutableLiveData<Resource<List<Upload>>> imageList;
    private MutableLiveData<Resource<Upload>> upload;
    private HomeRepository homeRepository;

    public HomeViewModel() {
        super();
        homeRepository = new HomeRepository();

        imageList = homeRepository.fetchImages();
    }

    LiveData<Resource<List<Upload>>> getImages() {
        return imageList;
    }

    LiveData<Resource<Upload>> uploadImage(Uri uri) {
        upload = homeRepository.uploadImage(uri);
        return upload;
    }
}