package com.example.texttile.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.texttile.data.dao.DaoAuthority;
import com.example.texttile.data.model.UserDataModel;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.PrefUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class MyApplication extends Application {

    Activity activity;
    Context context;
    DaoAuthority dao;
    PrefUtil prefUtil;

    public static MutableLiveData<UserDataModel> USER_DATA = new MutableLiveData<>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public Activity getActivity() {
        return activity;
    }

    public Context getContext() {
        return context;
    }

    public void setActivity(Activity activity, Context context) {
        dao = new DaoAuthority(activity);

        prefUtil = new PrefUtil(activity);

        if (prefUtil.getUser() != null && prefUtil.getUser().getId() != null) {
            dao.getReference().whereEqualTo("id", prefUtil.getUser().getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot snapshot, FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e("sdfdsf", "Error querying Firestore: ", error);
                        return;
                    }
                    for (DocumentSnapshot documentSnapshot : snapshot.getDocuments()) {
                        UserDataModel userModel = documentSnapshot.toObject(UserDataModel.class);
                        USER_DATA.setValue(userModel);
                    }
                }
            });

//            dao.getReference().orderByChild("id").equalTo(prefUtil.getUser().getId()).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@Nonnull DataSnapshot snapshot) {
//                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                        UserDataModel userModel = postSnapshot.getValue(UserDataModel.class);
//                        USER_DATA.setValue(userModel);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@Nonnull DatabaseError error) {
//                }
//            });
        }

        this.activity = activity;
        this.context = context;
    }

    public static UserDataModel getUSERMODEL() {
        if (USER_DATA.getValue() != null){
            return USER_DATA.getValue();
        }else
        {
            MyApplication myApplication = new MyApplication();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(Const.USER_DATA)
                    .whereEqualTo("u_name", new PrefUtil(myApplication.activity).getUser().getU_name())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserDataModel userModel = document.toObject(UserDataModel.class);
                                setUSERMODEL(userModel);
                            }
                        } else {

                            Log.d("dfjsdl", "Error getting documents: ", task.getException());
                        }
                    });


//            new DaoAuthority(myApplication.activity).getReference().orderByChild("u_name").equalTo(new PrefUtil(myApplication.activity).getUser().getU_name()).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@Nonnull DataSnapshot snapshot) {
//                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                        UserDataModel userModel = postSnapshot.getValue(UserDataModel.class);
//                        setUSERMODEL(userModel);
//
//                    }
//                }
//                @Override
//                public void onCancelled(@Nonnull DatabaseError error) {
//                }
//            });
        }
        return USER_DATA.getValue();
    }

    public static void setUSERMODEL(UserDataModel USERMODEL) {
        USER_DATA.setValue(USERMODEL);
    }
}
