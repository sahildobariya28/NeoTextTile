package com.example.texttile.presentation.ui.util;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.ListShowDialogBinding;
import com.example.texttile.databinding.SearchItemBinding;
import com.example.texttile.presentation.ui.interfaces.AddOnSearchedItemSelectListener;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class ListShowDialog {

    Activity activity;
    ArrayList<String> modelList = new ArrayList<>();
    ArrayList<String> selectedModelList = new ArrayList<>();
    ArrayList<Boolean> checkedList = new ArrayList<>();
    Dialog dialog;
    AddOnSearchedItemSelectListener addOnSearchedItemSelectListener;
    SearchListAdapter searchListAdapter;
    int REQUEST_CODE;
    ListShowDialogBinding bindingDialog;
    boolean isMultiSelection = false;
    LinearLayoutManager linearLayoutManager;


    public ListShowDialog(Activity activity, ArrayList<String> modelList, AddOnSearchedItemSelectListener addOnSearchedItemSelectListener, int request_code) {
        this.activity = activity;
        this.modelList = modelList;
        this.addOnSearchedItemSelectListener = addOnSearchedItemSelectListener;
        this.REQUEST_CODE = request_code;

        setDialog();
    }

    public ListShowDialog(Activity activity, ArrayList<String> modelList, AddOnSearchedItemSelectListener addOnSearchedItemSelectListener, int request_code, boolean isMultiSelection) {
        this.activity = activity;
        this.modelList = modelList;
        this.addOnSearchedItemSelectListener = addOnSearchedItemSelectListener;
        this.REQUEST_CODE = request_code;
        this.isMultiSelection = isMultiSelection;

        setDialog();
    }


    public Dialog setDialog() {


        if (activity != null) {
            dialog = new Dialog(activity);
            bindingDialog = ListShowDialogBinding.inflate(activity.getLayoutInflater());
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
            dialog.setContentView(bindingDialog.getRoot());
            dialog.setCancelable(true);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            bindingDialog.editSearch.requestFocus();
            setAdapter(modelList, false);
            CardView btnOkay = dialog.findViewById(R.id.btn_okay);



            if (isMultiSelection) {
                btnOkay.setVisibility(View.VISIBLE);
            }

            btnOkay.setOnClickListener(view -> {
                for (int i = 0; i < checkedList.size(); i++) {
                    if (checkedList.get(i)) {
                        selectedModelList.add(modelList.get(i));
                    }
                }

                addOnSearchedItemSelectListener.onMultiSelected(selectedModelList, REQUEST_CODE);
                dialog.dismiss();
            });

            KeyboardUtils.addKeyboardToggleListener(activity, new KeyboardUtils.SoftKeyboardToggleListener() {
                @Override
                public void onToggleSoftKeyboard(boolean isVisible) {
                    if (isVisible) {
                        bindingDialog.guideline3.setGuidelinePercent(0.10f);
                        bindingDialog.guideline4.setGuidelinePercent(0.90f);
                    } else {
                        bindingDialog.guideline3.setGuidelinePercent(0.25f);
                        bindingDialog.guideline4.setGuidelinePercent(0.75f);
                    }
                }
            });

            bindingDialog.editSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int pos, int i1, int i2) {

                    if (charSequence.toString().isEmpty()) {
                        setAdapter(modelList, false);
                    } else {
                        ArrayList<String> filteredModelList = new ArrayList<>();
                        for (int i = 0; i < modelList.size(); i++) {
                            if (modelList.get(i).contains(charSequence.toString())) {
                                filteredModelList.add(modelList.get(i));
                            }
                        }
                        checkedList.clear();
                        for (int i = 0; i < filteredModelList.size(); i++) {
                            checkedList.add(false);
                        }
                        if (filteredModelList.size() == 0) {
                            filteredModelList.add(charSequence.toString());
                            setAdapter(filteredModelList, true);
                        } else {
                            setAdapter(filteredModelList, false);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            bindingDialog.btnClose.setOnClickListener(view -> {
                bindingDialog.editSearch.setText("");
            });
            bindingDialog.recyclerView.setOnClickListener(view -> {

            });
            bindingDialog.searchBar.setOnClickListener(view -> {

            });
            bindingDialog.viewMain.setOnClickListener(view -> {
                dialog.dismiss();
            });
            bindingDialog.cardMain.setOnClickListener(view -> {

            });

            return dialog;
        }
        return null;
    }

    private void updateItemCountText(ListShowDialogBinding bindingDialog) {
        bindingDialog.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition() + 1;
                int lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition() + 1;

                if (firstVisibleItem > -1 && lastVisibleItem > -1 && totalItemCount > -1){
                    bindingDialog.textItemSize.setText(firstVisibleItem + " - " + lastVisibleItem + " of " + totalItemCount);
                }else {
                    bindingDialog.textItemSize.setText(0 + " - " + 0 + " of " + totalItemCount);
                }
            }
        });

//        LinearLayoutManager layoutManager = (LinearLayoutManager) bindingDialog.recyclerView.getLayoutManager();
//        int firstVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition();
//        int lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();
//        int totalItemCount = layoutManager.getItemCount();
//
//        // Update the item count text.
//        String itemCountText = String.format("%d - %d of %d", firstVisibleItem, lastVisibleItem, totalItemCount);
//        bindingDialog.textItemSize.setText(itemCountText);
    }

    public void show_dialog() {
        if (dialog != null) {
            if (!dialog.isShowing()) {

                dialog.show();
            }
        } else {
            setDialog();
        }

    }

    public void dismiss_dialog() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        } else {
            setDialog();
        }
    }

    public void update_list(ArrayList<String> modelList) {
        if (modelList.size() > 0) {
            if (bindingDialog.editSearch.getText().toString().isEmpty()) {
                this.modelList = modelList;
                searchListAdapter.updateList(modelList);
                searchListAdapter.notifyDataSetChanged();
            } else {
                ArrayList<String> filteredModelList = new ArrayList<>();
                for (int i = 0; i < modelList.size(); i++) {
                    if (modelList.get(i).contains(bindingDialog.editSearch.getText().toString())) {
                        filteredModelList.add(modelList.get(i));
                    }
                }

                checkedList.clear();
                for (int i = 0; i < filteredModelList.size(); i++) {
                    checkedList.add(false);
                }
                if (filteredModelList.size() == 0) {
                    filteredModelList.add(bindingDialog.editSearch.getText().toString());
                    setAdapter(filteredModelList, true);
                } else {
                    setAdapter(filteredModelList, false);
                }
            }
        } else {

        }
    }

    public void setAdapter(ArrayList<String> modelList, boolean is_add) {

        if (searchListAdapter == null) {
            searchListAdapter = new SearchListAdapter(modelList, addOnSearchedItemSelectListener);
            linearLayoutManager = new LinearLayoutManager(activity);
            bindingDialog.recyclerView.setLayoutManager(linearLayoutManager);
            bindingDialog.recyclerView.setAdapter(searchListAdapter);
            updateItemCountText(bindingDialog);
        } else {
            searchListAdapter.setModelList(modelList, is_add);
        }

    }


    public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {

        ArrayList<String> modelList;
        AddOnSearchedItemSelectListener addOnSearchedItemSelectListener;
        boolean is_add;

        public SearchListAdapter(ArrayList<String> modelList, AddOnSearchedItemSelectListener addOnSearchedItemSelectListener) {
            this.modelList = modelList;
            checkedList.clear();
            for (int i = 0; i < modelList.size(); i++) {
                checkedList.add(false);
            }
            this.addOnSearchedItemSelectListener = addOnSearchedItemSelectListener;
        }

        public void setModelList(ArrayList<String> list, boolean is_add) {
            modelList = list;
            checkedList.clear();
            for (int i = 0; i < modelList.size(); i++) {
                checkedList.add(false);
            }
            notifyDataSetChanged();
            this.is_add = is_add;
        }

        public void updateList(ArrayList<String> modelList) {
            this.modelList = modelList;
            checkedList.clear();
            for (int i = 0; i < modelList.size(); i++) {
                checkedList.add(false);
            }
        }

        @Nonnull
        @Override
        public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
            SearchItemBinding binding = SearchItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@Nonnull ViewHolder holder, int position) {
            if (isMultiSelection) {
                if (checkedList.get(position)) {
                    holder.binding.viewMain.setBackgroundColor(activity.getResources().getColor(R.color.blue_light));
                } else {
                    holder.binding.viewMain.setBackgroundColor(activity.getResources().getColor(R.color.white));
                }
            }

            if (is_add) {
                holder.binding.btnInfo.setVisibility(View.VISIBLE);
            } else {
                holder.binding.btnInfo.setVisibility(View.GONE);
            }
            holder.binding.textTitle.setText(modelList.get(position));

            holder.itemView.setOnClickListener(view -> {
                if (isMultiSelection) {
                    if (checkedList.get(position)) {
                        checkedList.set(position, false);
                        holder.binding.viewMain.setBackgroundColor(activity.getResources().getColor(R.color.white));
                    } else {
                        checkedList.set(position, true);
                        holder.binding.viewMain.setBackgroundColor(activity.getResources().getColor(R.color.blue_light));
                    }
                } else {
                    if (is_add) {
                        new CustomSnackUtil().showSnack(dialog, "Please create a new item First", R.drawable.icon_warning);
                    } else {
                        dialog.dismiss();
                        addOnSearchedItemSelectListener.onSelected(position, modelList.get(position), REQUEST_CODE);
                    }
                }
            });

            holder.binding.btnInfo.setOnClickListener(view -> {
                addOnSearchedItemSelectListener.onAddButtonClicked(position, modelList.get(position), REQUEST_CODE);
            });
        }

        @Override
        public int getItemCount() {
            return modelList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            SearchItemBinding binding;

            public ViewHolder(@Nonnull SearchItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;

            }
        }
    }
}
