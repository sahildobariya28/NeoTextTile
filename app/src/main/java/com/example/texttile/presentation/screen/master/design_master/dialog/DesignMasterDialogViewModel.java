package com.example.texttile.presentation.screen.master.design_master.dialog;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.texttile.R;
import com.example.texttile.data.model.DesignMasterModel;

import java.util.HashMap;

public class DesignMasterDialogViewModel extends ViewModel {
    public String tracker;
    Activity activity;
    public MutableLiveData<DesignMasterModel> designMasterLiveData = new MutableLiveData<>(new DesignMasterModel());
    String start_Len = "", end_Len = "";
    String[] category = {"PIECE", "METER"};



    public DesignMasterDialogViewModel(String tracker, Activity activity) {
        this.tracker = tracker;
        this.activity = activity;
    }

    public ArrayAdapter getTypeEntries(){

        ArrayAdapter aa = new ArrayAdapter(activity, R.layout.white_simple_spinner_item, category);
        aa.setDropDownViewResource(R.layout.white_simple_spinner_dropdown_item);
        return  aa;
    }
    public void onDesignNoTextChanged(CharSequence s, int start, int before, int count) {
        designMasterLiveData.getValue().setDesign_no(s.toString());
    }

    public void onDateTextChanged(CharSequence s, int start, int before, int count) {
        designMasterLiveData.getValue().setDate(s.toString());
    }

    public void onReedTextChanged(CharSequence s, int start, int before, int count) {
        designMasterLiveData.getValue().setReed(Integer.valueOf(s.toString()));
    }

    public void onBasePickTextChanged(CharSequence s, int start, int before, int count) {
        designMasterLiveData.getValue().setBase_pick(Integer.valueOf(s.toString()));
    }

    public void onBaseCardTextChanged(CharSequence s, int start, int before, int count) {
        designMasterLiveData.getValue().setBase_card(Integer.valueOf(s.toString()));
    }

    public void onTotalCardTextChanged(CharSequence s, int start, int before, int count) {
        designMasterLiveData.getValue().setTotal_card(Integer.valueOf(s.toString()));
    }

    public void onAvgPickTextChanged(CharSequence s, int start, int before, int count) {
        designMasterLiveData.getValue().setAvg_pick(Integer.valueOf(s.toString()));
    }

    public void onTotalLengthTextChanged(CharSequence s, int start, int before, int count) {
        designMasterLiveData.getValue().setTotal_len(Float.valueOf(s.toString().replace("/mtr", "")));
    }

    public void onSampleCardLengthStartTextChanged(CharSequence s, int start, int before, int count) {
        makeSampleCardLength(s.toString(), true);
    }

    public void onSampleCardLengthEndTextChanged(CharSequence s, int start, int before, int count) {
        makeSampleCardLength(s.toString(), false);
    }

    public void onDesignTypeSelectedListener(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("djfdjskfjq2342", "onQtyTextChanged: " + i);

        if (i == 0) {
            designMasterLiveData.getValue().setType("PIECE");
        } else if (i == 1) {
            designMasterLiveData.getValue().setType("METER");
        }
    }

    public void makeSampleCardLength(String length, boolean isStarting) {
        if (isStarting) {
            start_Len = length;
        } else {
            end_Len = length;
        }
        designMasterLiveData.getValue().setSample_card_len(start_Len + "-" + end_Len);
    }

    public DesignMasterModel getDesignMasterModel() {
        return designMasterLiveData.getValue();
    }

    public HashMap<String, Object> getDesignMasterHashMap() {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("design_no", designMasterLiveData.getValue().getDesign_no());
        hashMap.put("date", designMasterLiveData.getValue().getDate());
        hashMap.put("reed", designMasterLiveData.getValue().getReed());
        hashMap.put("base_pick", designMasterLiveData.getValue().getBase_pick());
        hashMap.put("base_card", designMasterLiveData.getValue().getBase_card());
        hashMap.put("total_card", designMasterLiveData.getValue().getTotal_card());
        hashMap.put("avg_pick", designMasterLiveData.getValue().getAvg_pick());
        hashMap.put("type", designMasterLiveData.getValue().getType());
        hashMap.put("f_list", designMasterLiveData.getValue().getF_list());
        hashMap.put("total_len", designMasterLiveData.getValue().getTotal_len());
        hashMap.put("sample_card_len", designMasterLiveData.getValue().getSample_card_len());
        hashMap.put("jalaWithFileModelList", designMasterLiveData.getValue().getJalaWithFileModelList());

        return hashMap;
    }

}
