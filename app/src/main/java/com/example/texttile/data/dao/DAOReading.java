package com.example.texttile.data.dao;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;

import com.example.texttile.R;
import com.example.texttile.data.model.DailyReadingModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class DAOReading {

    MutableLiveData<ArrayList<DailyReadingModel>> dailyReadingLiveData = new MutableLiveData<>();
    ArrayList<DailyReadingModel> dailyReadingList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference dailyReadingRef = db.collection(Const.DAILY_READING);
    Activity activity;

    public DAOReading(Activity activity) {
        this.activity = activity;
    }

    public Task<Void> insert(String id, DailyReadingModel dailyReadingModel) {
        return dailyReadingRef.document(id).set(dailyReadingModel).addOnFailureListener(e -> {
            DialogUtil.showErrorDialog(activity);
        });
    }

    public Task<Void> update(String id, Map<String, Object> updates) {
        return dailyReadingRef.document(id).update(updates).addOnFailureListener(e -> {
            DialogUtil.showErrorDialog(activity);
        });
    }

    public Task<Void> delete(String id) {
        return dailyReadingRef.document(id).delete().addOnFailureListener(e -> {
            DialogUtil.showErrorDialog(activity);
        });
    }

    public void removeSelectedItem(ArrayList<Integer> selectedItems) {
        int batchSize = 500;
        int batchCount = 0;
        WriteBatch batch = db.batch();
        DialogUtil.showProgressDialog(activity);
        for (int i = selectedItems.size() - 1; i >= 0; i--) {
            DocumentReference documentReference = dailyReadingRef.document(dailyReadingList.get(selectedItems.get(i)).getId());
            batch.delete(documentReference);
            if (++batchCount == batchSize) {
                batch.commit();
                batch = FirebaseFirestore.getInstance().batch();
                batchCount = 0;
            }
        }
        if (batchCount > 0) {
            batch.commit()
                    .addOnSuccessListener(aVoid -> {
                        DialogUtil.hideProgressDialog();
                        DialogUtil.showDeleteDialog(activity);
                    })
                    .addOnFailureListener(e -> {
                        DialogUtil.hideProgressDialog();
                        new CustomSnackUtil().showSnack(activity, "Something went wrong", R.drawable.error_msg_icon);
                    });
        }else {
            DialogUtil.hideProgressDialog();
            DialogUtil.showDeleteDialog(activity);
        }
    }

    public CollectionReference getReference() {
        return dailyReadingRef;
    }

    public String getId() {
        return dailyReadingRef.document().getId();
    }

    public MutableLiveData<ArrayList<DailyReadingModel>> getDailyReadingListLiveData() {
        if (dailyReadingLiveData == null) {
            dailyReadingLiveData = new MutableLiveData<>();
            getDailyReadingList("");
        }
        return dailyReadingLiveData;
    }

    public void getDailyReadingList(String filter) {
        dailyReadingRef.addSnapshotListener((value, error) -> {
            dailyReadingList.clear();
            if (value != null) {
                for (QueryDocumentSnapshot doc : value) {
                    DailyReadingModel dailyReadingModel = doc.toObject(DailyReadingModel.class);
                    if (dailyReadingModel.getOrder_no().toLowerCase().contains(filter.toLowerCase())) {
                        dailyReadingList.add(dailyReadingModel);
                    }
                }
                dailyReadingLiveData.setValue(sortModelListByDate(dailyReadingList));
            } else {
                dailyReadingLiveData.setValue(sortModelListByDate(dailyReadingList));
            }
        });
    }

    public ArrayList<DailyReadingModel> sortModelListByDate(ArrayList<DailyReadingModel> modelList) {
        Collections.sort(modelList, (readingModel1, readingModel2) -> {
            try {
                return ((new SimpleDateFormat("dd/MM/yy")).parse(readingModel2.getDate()).getDay() - (new SimpleDateFormat("dd/MM/yy")).parse(readingModel1.getDate()).getDay());
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        });
        return modelList;
    }

}
