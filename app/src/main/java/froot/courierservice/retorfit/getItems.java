package froot.courierservice.retorfit;

import java.io.Serializable;

public class getItems implements Serializable {

    private String title;
    private String price;
    private String cnt;
    private String grams;
    private String barcode;

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getCnt() {
        return cnt;
    }

    public String getGrams() {
        return grams;
    }

    public String getBarcode() {
        return barcode;
    }
}
