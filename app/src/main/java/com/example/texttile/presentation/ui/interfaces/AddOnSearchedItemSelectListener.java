package com.example.texttile.presentation.ui.interfaces;

import java.util.ArrayList;

public interface AddOnSearchedItemSelectListener {
    void onSelected(int position, String name, int request_code);
    void onAddButtonClicked(int position, String name, int request_code);
    void onMultiSelected(ArrayList<String> shadeList, int request_code);
}