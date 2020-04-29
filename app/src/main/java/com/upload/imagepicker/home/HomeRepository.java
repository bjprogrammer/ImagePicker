package com.upload.imagepicker.home;

import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.upload.imagepicker.model.Upload;

import com.upload.imagepicker.utils.Resource;
import com.upload.imagepicker.utils.Status;

import java.util.ArrayList;
import java.util.List;

class HomeRepository {
    private MutableLiveData<Resource<List<Upload>>> fetchliveData;
    private MutableLiveData<Resource<Upload>> uploadliveData;
    private DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

    // Private Constructor
    HomeRepository() { }

    MutableLiveData<Resource<List<Upload>>> fetchImages() {
        fetchliveData = new MutableLiveData<>();

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Upload> mUploads = new ArrayList<>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
                }

                fetchliveData.postValue(new Resource<>(Status.SUCCESS, mUploads, null));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                fetchliveData.setValue(new Resource<>(Status.ERROR, null,databaseError.getMessage()));
            }
        });
        return fetchliveData;
    }

    MutableLiveData<Resource<Upload>> uploadImage(Uri uri) {
        uploadliveData = new MutableLiveData<>();
        Long time= System.currentTimeMillis();
        StorageReference fileReference = mStorageRef.child(time.toString());

        fileReference.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
        {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
            {
                if (!task.isSuccessful())
                {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>()
        {
            @Override
            public void onComplete(@NonNull Task<Uri> task)
            {
                if (task.isSuccessful())
                {
                    Uri downloadUri = task.getResult();
                    Upload upload = new Upload(time.toString(), downloadUri.toString());
                    mDatabaseRef.push().setValue(upload);

                    uploadliveData.postValue(new Resource<>(Status.SUCCESS,upload, null));
                } else
                {
                    uploadliveData.setValue(new Resource<>(Status.ERROR, null,task.getException().getMessage()));
                }
            }
        });

        return uploadliveData;
    }
}
