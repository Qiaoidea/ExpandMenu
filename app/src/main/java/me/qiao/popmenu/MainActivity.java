package me.qiao.popmenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import me.qiao.pop.PopMenu;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.listview);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 20;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = ViewHolder.generateView(parent);
                }
                ViewHolder holder = (ViewHolder) convertView.getTag();
                holder.titleView.setText("百年大计 Title"+position);
                holder.contentView.setText("一年之计在于春，一日之计在于晨。");

                convertView.setOnClickListener(MainActivity.this);

                return convertView;
            }
        });
    }

    @Override
    public void onClick(View v) {
        Object obj = v.getTag();
        if(obj instanceof ViewHolder){
            View optionView = getLayoutInflater().inflate(R.layout.popmenu, (ViewGroup) v.getParent(), false);
            new PopMenu(this, v, optionView, ((ViewHolder)obj).titleView).show();
        }
    }

    static class ViewHolder {
        ImageView icon;
        TextView titleView, contentView;

        public static View generateView(ViewGroup parent){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
            ViewHolder holder = new ViewHolder();
            holder.icon = (ImageView) view.findViewById(R.id.icon);
            holder.titleView = (TextView) view.findViewById(R.id.title);
            holder.contentView = (TextView) view.findViewById(R.id.content);
            view.setTag(holder);
            return view;
        }
    }
}
