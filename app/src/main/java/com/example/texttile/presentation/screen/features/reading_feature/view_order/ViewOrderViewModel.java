package com.example.texttile.presentation.screen.features.reading_feature.view_order;

import android.app.Activity;
import android.widget.ImageView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.texttile.R;
import com.example.texttile.data.dao.DAOOrder;
import com.example.texttile.data.dao.DaoOrderHistory;
import com.example.texttile.data.model.ItemState;
import com.example.texttile.data.model.OrderHistory;
import com.example.texttile.data.model.OrderMasterModel;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.ViewBinderHelper;
import com.example.texttile.presentation.ui.util.Const;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ViewOrderViewModel extends ViewModel {

    int old_item = -1, new_item = -1;

    Activity activity;
    DAOOrder daoOrder;
//    int item_id;

    MutableLiveData<Integer> itemId = new MutableLiveData<>(R.id.btn_padding);
    MutableLiveData<String> filter = new MutableLiveData<>("");

    MutableLiveData<String> title = new MutableLiveData<>("Pending");

    boolean selectionPermission = false;
    MutableLiveData<ArrayList<OrderMasterModel>> orderMasterModels = new MutableLiveData<>();
    MutableLiveData<ArrayList<ItemState>> itemModels = new MutableLiveData<>();

    MutableLiveData<Integer> selectedItemCount = new MutableLiveData<>();
    MutableLiveData<Boolean> isAllSelected = new MutableLiveData<>(false);
    MutableLiveData<Boolean> selectionMode = new MutableLiveData<>(false);
    MutableLiveData<Boolean> isEmptyData = new MutableLiveData<>(true);

    ViewOrderAdapter viewOrderAdapter;

    public final ViewBinderHelper binderHelper = new ViewBinderHelper();



    public ViewOrderViewModel(Activity activity) {
        this.activity = activity;
        daoOrder = new DAOOrder(activity);

    }

    public void updateItemModel() {
        itemModels.getValue().clear();
        for (int i = 0; i < orderMasterModels.getValue().size(); i++) {
            itemModels.getValue().add(new ItemState(false, false, false, i));
        }
    }

    public void selectionModeUpdate() {
        int count = 0;
        for (int i = 0; i < itemModels.getValue().size(); i++) {
            if (itemModels.getValue().get(i).isSelected()) {
                count++;
            }
        }

        if (count == 0) {
            selectionMode.setValue(false);
            selectedItemCount.setValue(count);
            isAllSelected.setValue(count == itemModels.getValue().size());
            unlockAll();
//            onSelectionCountListeners.onCountChanged(selectionMode, count, count == itemModels.getValue().size());
        } else {
            selectionMode.setValue(true);
            selectedItemCount.setValue(count);
            isAllSelected.setValue(count == itemModels.getValue().size());
            lockAll();
//            onSelectionCountListeners.onCountChanged(selectionMode, count, count == itemModels.getValue().size() ? true : false);
        }
    }

    public void lockAll() {
        for (int i = 0; i < itemModels.getValue().size(); i++) {
            if (itemModels.getValue().get(i).isSwiped()) {
                binderHelper.closeLayout(String.valueOf(i));
            }
            binderHelper.lockSwipe(String.valueOf(i));
        }
    }

    public void unlockAll() {
        for (int i = 0; i < itemModels.getValue().size(); i++) {
            binderHelper.unlockSwipe(String.valueOf(i));
        }
    }

    public void select_all() {
        for (int i = 0; i < itemModels.getValue().size(); i++) {
            itemModels.getValue().get(i).setSelected(true);
        }
        selectionModeUpdate();
    }

    public void unSelect_all() {
        for (int i = 0; i < itemModels.getValue().size(); i++) {
            itemModels.getValue().get(i).setSelected(false);
        }
        selectionModeUpdate();
    }

    public void moveToNextAll() {
        DaoOrderHistory orderHistory = new DaoOrderHistory(activity);
        switch (itemId.getValue()) {

            case R.id.btn_ready_to_dispatch:

                for (int i = 0; i < itemModels.getValue().size(); i++) {
                    String id = orderHistory.getNewId();
                    if (itemModels.getValue().get(i).isSelected()) {
                        OrderMasterModel orderMasterModel = orderMasterModels.getValue().get(itemModels.getValue().get(i).getPosition());
                        OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_READY_TO_DISPATCH, Const.ON_WAREHOUSE, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), orderMasterModel.getQuantity(), new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                        orderMasterModel.getOrderStatusModel().setOnReadyToDispatch(orderMasterModel.getOrderStatusModel().getOnReadyToDispatch() - orderMasterModel.getQuantity());
                        orderMasterModel.getOrderStatusModel().setOnWarehouse(orderMasterModel.getOrderStatusModel().getOnWarehouse() + orderMasterModel.getQuantity());
                        daoOrder.update_qty_without_dialog(orderMasterModel.getId(), orderMasterModel, orderHistoryModel);
                    }

                }
                break;
            case R.id.btn_warehouse:
                for (int i = 0; i < itemModels.getValue().size(); i++) {
                    String id = orderHistory.getNewId();
                    if (itemModels.getValue().get(i).isSelected()) {
                        OrderMasterModel orderMasterModel = orderMasterModels.getValue().get(itemModels.getValue().get(i).getPosition());
                        OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_WAREHOUSE, Const.ON_FINAL_DISPATCH, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), orderMasterModel.getQuantity(), new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                        orderMasterModel.getOrderStatusModel().setOnWarehouse(orderMasterModel.getOrderStatusModel().getOnWarehouse() - orderMasterModel.getQuantity());
                        orderMasterModel.getOrderStatusModel().setOnFinalDispatch(orderMasterModel.getOrderStatusModel().getOnFinalDispatch() + orderMasterModel.getQuantity());
                        daoOrder.update_qty_without_dialog(orderMasterModel.getId(), orderMasterModel, orderHistoryModel);
                    }
                }
                break;
            case R.id.btn_final_dispatch:
                for (int i = 0; i < itemModels.getValue().size(); i++) {
                    String id = orderHistory.getNewId();
                    if (itemModels.getValue().get(i).isSelected()) {
                        OrderMasterModel orderMasterModel = orderMasterModels.getValue().get(itemModels.getValue().get(i).getPosition());
                        OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_FINAL_DISPATCH, Const.ON_DELIVERED, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), orderMasterModel.getQuantity(), new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                        orderMasterModel.getOrderStatusModel().setOnFinalDispatch(orderMasterModel.getOrderStatusModel().getOnFinalDispatch() - orderMasterModel.getQuantity());
                        orderMasterModel.getOrderStatusModel().setOnDelivered(orderMasterModel.getOrderStatusModel().getOnDelivered() + orderMasterModel.getQuantity());
                        daoOrder.update_qty_without_dialog(orderMasterModel.getId(), orderMasterModel, orderHistoryModel);
                    }
                }
                break;
        }
    }

    public void expand_item(int current_item, ImageView btn_down) {
        if (new_item != -1) {
            if (new_item != current_item) {
                old_item = new_item;

                itemModels.getValue().get(old_item).setExpanded(false);
                btn_down.setImageResource(R.drawable.down);
                new_item = current_item;
                itemModels.getValue().get(new_item).setExpanded(true);
                btn_down.setImageResource(R.drawable.up);
                viewOrderAdapter.notifyItemChanged(old_item);
                viewOrderAdapter.notifyItemChanged(new_item);

            } else {
                old_item = new_item;
                if (itemModels.getValue().get(new_item).isExpanded()) {
                    itemModels.getValue().get(new_item).setExpanded(false);
                    btn_down.setImageResource(R.drawable.down);
                } else {
                    itemModels.getValue().get(new_item).setExpanded(true);
                    btn_down.setImageResource(R.drawable.up);
                }
                new_item = current_item;
                viewOrderAdapter.notifyItemChanged(old_item);
                viewOrderAdapter.notifyItemChanged(new_item);
            }

        } else {
            new_item = current_item;
            itemModels.getValue().get(new_item).setExpanded(true);
            btn_down.setImageResource(R.drawable.up);
            viewOrderAdapter.notifyItemChanged(old_item);
            viewOrderAdapter.notifyItemChanged(new_item);
        }
    }

    public void item_click() {

        switch (itemId.getValue()) {
            case R.id.btn_padding:
                selectionPermission = false;
                title.setValue("Pending");
                view_order_list(filter.getValue(), Const.ON_PENDING);
//                orderOption = new FirebaseRecyclerOptions.Builder<OrderMasterModel>().setQuery(new DAOOrder(this).getList().orderByChild("orderStatusModel/" + Const.ON_PENDING).startAt(1), OrderMasterModel.class).build();
                break;
            case R.id.btn_on_machine_padding:
                selectionPermission = false;
                title.setValue("On Machine Padding");
                view_order_list(filter.getValue(), Const.ON_MACHINE_PENDING);
//                orderOption = new FirebaseRecyclerOptions.Builder<OrderMasterModel>().setQuery(new DAOOrder(this).getList().orderByChild("orderStatusModel/" + Const.ON_MACHINE_PENDING).startAt(1), OrderMasterModel.class).build();
                break;
            case R.id.btn_on_machine_completed:
                selectionPermission = false;
                title.setValue("On Machine Completed");
                view_order_list(filter.getValue(), Const.ON_MACHINE_COMPLETED);
//                orderOption = new FirebaseRecyclerOptions.Builder<OrderMasterModel>().setQuery(new DAOOrder(this).getList().orderByChild("orderStatusModel/" + Const.ON_MACHINE_COMPLETED).startAt(1), OrderMasterModel.class).build();
                break;
            case R.id.btn_ready_to_dispatch:
                selectionPermission = true;
                title.setValue("Ready to Dispatch");
                view_order_list(filter.getValue(), Const.ON_READY_TO_DISPATCH);
//                orderOption = new FirebaseRecyclerOptions.Builder<OrderMasterModel>().setQuery(new DAOOrder(this).getList().orderByChild("orderStatusModel/" + Const.ON_READY_TO_DISPATCH).startAt(1), OrderMasterModel.class).build();
                break;
            case R.id.btn_warehouse:
                selectionPermission = true;
                title.setValue("Warehouse");
                view_order_list(filter.getValue(), Const.ON_WAREHOUSE);
//                orderOption = new FirebaseRecyclerOptions.Builder<OrderMasterModel>().setQuery(new DAOOrder(this).getList().orderByChild("orderStatusModel/" + Const.ON_WAREHOUSE).startAt(1), OrderMasterModel.class).build();
                break;
            case R.id.btn_final_dispatch:
                selectionPermission = true;
                title.setValue("Final Dispatch");
                view_order_list(filter.getValue(), Const.ON_FINAL_DISPATCH);
//                orderOption = new FirebaseRecyclerOptions.Builder<OrderMasterModel>().setQuery(new DAOOrder(this).getList().orderByChild("orderStatusModel/" + Const.ON_FINAL_DISPATCH).startAt(1), OrderMasterModel.class).build();
                break;
            case R.id.btn_delivered:
                selectionPermission = false;
                title.setValue("Delivered");
                view_order_list(filter.getValue(), Const.ON_DELIVERED);
//                orderOption = new FirebaseRecyclerOptions.Builder<OrderMasterModel>().setQuery(new DAOOrder(this).getList().orderByChild("orderStatusModel/" + Const.ON_DELIVERED).startAt(1), OrderMasterModel.class).build();
                break;
            case R.id.btn_damage:
                selectionPermission = false;
                title.setValue("Damage");
                view_order_list(filter.getValue(), Const.ON_DAMAGE);
//                orderOption = new FirebaseRecyclerOptions.Builder<OrderMasterModel>().setQuery(new DAOOrder(this).getList().orderByChild("orderStatusModel/" + Const.ON_DAMAGE).startAt(1), OrderMasterModel.class).build();
                break;
        }

    }


    public void view_order_list(String filter, String orderStatusModel) {
        daoOrder.getOrderListByStatus(orderStatusModel, filter);
    }

}
