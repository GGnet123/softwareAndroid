package froot.courierservice.retorfit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class getJson {
    private ArrayList<getOrders> orderList = new ArrayList<>();

    @SerializedName("success")
    @Expose
    private String success;

    @SerializedName("statuses")
    @Expose
    private Object status;

    @SerializedName("orders")
    private ArrayList<getOrders> orders;

    public ArrayList<getOrders> getOrderList(){
        return orderList;
    }
    public String getSuccess() {
        return success;
    }
    public void setSuccess(String success) {
        this.success = success;
    }

    public Object getStatus() {
        return status;
    }
    public void setStatus(Object status) {
        this.status = status;
    }

    public ArrayList<getOrders> getOrders() {
        return orders;
    }
    public void setOrders(ArrayList orders) {
        this.orders = orders;
    }

}

