package froot.courierservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;

    static final String KEY_ORDERS_ID = "OrdersId";
    static final String KEY_ORDERS_NAME = "Name";
    static final String KEY_ORDERS_ADDRESS = "Address";
    static final String KEY_ORDERS_STORE_ID = "StoreId";
    static final String KEY_ORDERS_DATE = "Date";
    static final String KEY_ORDERS_IS_CARD = "IsCard";
    static final String KEY_ORDERS_TAKEN = "Taken";
    static final String KEY_ORDERS_IS_BCC_CARD = "IsBccCard";
    static final String KEY_ORDERS_TOTAL = "Total";
    static final String KEY_ORDERS_STATUS = "Status";
    static final String KEY_ORDERS_STATUS_ID = "StatusId";
    static final String KEY_ORDERS_DELIVERY_TYPE = "DeliveryType";
    static final String KEY_ORDERS_DELIVERY_PRICE = "DeliveryPrice";
    static final String KEY_ORDERS_PHONE = "Phone";
    static final String KEY_ORDERS_ITEMS = "Items";
    static final String KEY_ORDERS_RUNNER = "runner";

    int sum = 0;
    public ListViewAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_item, null);

        TextView id = (TextView)vi.findViewById(R.id.id); // id
        TextView title = (TextView)vi.findViewById(R.id.title); // title
        TextView address = (TextView)vi.findViewById(R.id.address); // address
        TextView store_id = (TextView)vi.findViewById(R.id.store_id);
        TextView date = (TextView)vi.findViewById(R.id.date);
        TextView is_card = (TextView)vi.findViewById(R.id.is_card);
        TextView taken = (TextView)vi.findViewById(R.id.taken);
        TextView is_bcc_card = (TextView)vi.findViewById(R.id.is_bcc_card);
        TextView total = (TextView)vi.findViewById(R.id.total);
        TextView status = (TextView)vi.findViewById(R.id.status);
        TextView status_id = (TextView)vi.findViewById(R.id.status_id);
        TextView delivery_type = (TextView)vi.findViewById(R.id.delivery_type);
        TextView delivery_price = (TextView)vi.findViewById(R.id.delivery_price);
        TextView phone = (TextView)vi.findViewById(R.id.phone);

        HashMap<String, String> item;
        item = data.get(position);

        String textId = "#" + item.get(KEY_ORDERS_ID);
        id.setText(textId);

        title.setText(item.get(KEY_ORDERS_NAME));
        address.setText(item.get(KEY_ORDERS_ADDRESS));
        store_id.setText(item.get(KEY_ORDERS_STORE_ID));

        date.setText(item.get(KEY_ORDERS_DATE));

        is_card.setText(item.get(KEY_ORDERS_IS_CARD));
        taken.setText(item.get(KEY_ORDERS_TAKEN));
        is_bcc_card.setText(item.get(KEY_ORDERS_IS_BCC_CARD));

        total.setText(item.get(KEY_ORDERS_TOTAL));

        if (!item.get(KEY_ORDERS_RUNNER).equals("none")){
            status.setText(item.get(KEY_ORDERS_STATUS) + "  (" + item.get(KEY_ORDERS_RUNNER) + ")");
        }
        else {
            status.setText(item.get(KEY_ORDERS_STATUS));
        }
        status_id.setText(item.get(KEY_ORDERS_STATUS_ID));
        delivery_price.setText(item.get(KEY_ORDERS_DELIVERY_PRICE));
        delivery_type.setText(item.get(KEY_ORDERS_DELIVERY_TYPE));
        phone.setText(item.get(KEY_ORDERS_PHONE));

        return vi;
    }
}