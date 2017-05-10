package me.qiao.popmenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import me.qiao.pop.PopMenu;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createListView());
    }

    private ListView createListView(){
        ListView listView = new ListView(this);
        listView.setDivider(null);
        listView.setScrollbarFadingEnabled(false);
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
                holder.titleView.setText("日事日毕 Title"+position);
                holder.contentView.setText("完成一行代码，养成一种习惯。");

                return convertView;
            }
        });
        listView.setOnItemClickListener(this);
        return listView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object obj = view.getTag();
        if(obj instanceof ViewHolder){
            View optionView = getLayoutInflater().inflate(R.layout.popmenu, parent, false);

            PopMenu.Builder
                    .create(this)
                    .setItemView(view)
                    .setMenuView(optionView)
                    .setTextView(((ViewHolder)obj).titleView)
                    .build()
                    .show();
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
