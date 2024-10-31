package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weather.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Model.WeatherData24h;

public class CustomAdapter24h extends BaseAdapter {
    Context context;
    ArrayList<WeatherData24h> arrayList;

    public CustomAdapter24h(Context context, ArrayList<WeatherData24h> arrayList) {
        this.context=context;
        this.arrayList = arrayList;
    }

    public CustomAdapter24h(Context context) {
        this.context = context;
    }



    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.item_listview_weather24h,null);

        WeatherData24h weather = arrayList.get(i);

        TextView txtDay = (TextView) view.findViewById(R.id.textviewThu);
        TextView txtTrangThai = (TextView) view.findViewById(R.id.textviewTrangThai);
        TextView txtMaxTemp = (TextView) view.findViewById(R.id.textviewMaxTemp);
        TextView txtMinTemp = (TextView) view.findViewById(R.id.textviewMinTemp);
        ImageView imgStatus = (ImageView) view.findViewById(R.id.imageviewTrangThai);

        txtDay.setText(weather.Day);
        txtTrangThai.setText(weather.Status);
        txtMaxTemp.setText(weather.MaxTemp);
        txtMinTemp.setText(weather.MinTemp+"°C");
        Picasso.with(context).load("https://developer.accuweather.com/sites/default/files/" + String.format("%02d", Integer.parseInt(weather.Image)) + "-s.png").into(imgStatus);
        return view;
    }
}

