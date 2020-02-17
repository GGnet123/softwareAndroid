package froot.courierservice.retorfit;

import java.util.ArrayList;

public class getOrders {
    private String id;
    private String client_name;
    private String client_address;
    private String store_id;
    private String date;
    private String is_card;
    private String taken;
    private String is_bcc_card;
    private String total;
    private String status;
    private String status_id;
    private String delivery_type;
    private String delivery_price;
    private String client_phone;
    private String runner;
    private ArrayList<getItems> items;

    public String getStore_id() {
        return store_id;
    }
    public String getDate() {
        return date;
    }
    public String getIs_card() {
        return is_card;
    }
    public String getTaken() {
        return taken;
    }
    public String getIs_bcc_card() {
        return is_bcc_card;
    }
    public String getTotal() {
        return total;
    }
    public String getStatus() {
        return status;
    }
    public String getStatus_id() {
        return status_id;
    }
    public String getDelivery_type() {
        return delivery_type;
    }
    public String getDelivery_price() {
        return delivery_price;
    }
    public String getClient_phone() {
        return client_phone;
    }
    public ArrayList<getItems> getItems() {
        return items;
    }
    public String getId(){
        return id;
    }
    public String getRunner(){
        return runner;
    }
    public  String getName(){
        return client_name;
    }
    public String getAddress(){ return client_address;}
}
