package kr.co.ezenac.cjy.teamproject.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ezenac.cjy.teamproject.HomeActivity;
import kr.co.ezenac.cjy.teamproject.R;
import kr.co.ezenac.cjy.teamproject.db.DBManager;
import kr.co.ezenac.cjy.teamproject.model.Collect;
import kr.co.ezenac.cjy.teamproject.model.Img;
import kr.co.ezenac.cjy.teamproject.model.Main;
import kr.co.ezenac.cjy.teamproject.model.Room;
import kr.co.ezenac.cjy.teamproject.retrofit.RetrofitService;
import kr.co.ezenac.cjy.teamproject.singletone.LoginInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2018-02-08.
 */

public class Home_adapter extends BaseAdapter {
    ArrayList<Main> Items = new ArrayList<>();
    private Context mContext;
    HashMap<Integer, Collect> col = new HashMap<>();
    DBManager dbManager;

    public Home_adapter(ArrayList<Main> items, Context mContext, HashMap<Integer, Collect> col) {
        this.Items = items;
        this.mContext = mContext;
        this.col = col;
    }

    public Home_adapter(ArrayList<Main> items, Context mContext) {
        this.Items = items;
        this.mContext = mContext;

    }

    @Override
    public int getCount() {
        return Items.size();
    }


    @Override
    public Object getItem(int position) {
        return Items.get(position); //변경여부..
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();

        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.home_adapter, parent, false);
            holder.img_roomImg = convertView.findViewById(R.id.img_roomImg);
            holder.text_roomName = convertView.findViewById(R.id.text_roomName);
            holder.img_homeAdapter = convertView.findViewById(R.id.img_homeAdapter);
            holder.text_homeAdapter = convertView.findViewById(R.id.text_homeAdapter);
            holder.img_star = convertView.findViewById(R.id.img_star);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final Main item = (Main) getItem(position);
        //Log.d("ddd", item.toString());
        Log.d("ddd","position : " + position);

        Glide.with(mContext).load(item.getPath()).centerCrop().into(holder.img_homeAdapter);
        holder.text_homeAdapter.setText(item.getContent());
        holder.text_roomName.setText(item.getName());
        Glide.with(mContext).load(item.getRoom_img()).centerCrop().into(holder.img_roomImg);

        dbManager = new DBManager(mContext,"Collect.db", null,1);

        final Integer user_id = LoginInfo.getInstance().getMember().getId();
        final String img_path = item.getPath();
        final String img_content = item.getContent();
        final Integer main_id = item.getId();

        if (col.get(main_id) == null){
            holder.img_star.setBackgroundResource(R.drawable.star);
            Log.d("col1", "i: " + holder.img_star);
        } else {
            holder.img_star.setBackgroundResource(R.drawable.star_mark);
        }

        final Holder finalHolder = holder;
        holder.img_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(col.get(main_id) == null) {
                    dbManager.insertData(1, user_id, main_id, img_path, img_content);
                    finalHolder.img_star.setBackgroundResource(R.drawable.star_mark);
                    Log.d("collection",main_id + "//"+user_id +" // " +  img_path +" // " + img_content);
                    Collect iCollect = new Collect();
                    iCollect = dbManager.collectInfo(user_id,main_id);
                    col.put(main_id, iCollect);
                    notifyDataSetChanged();
                } else {
                    dbManager.deleteData(main_id, user_id);
                    finalHolder.img_star.setBackgroundResource(R.drawable.star);
                    col.remove(main_id);
                    Log.d("collection", String.valueOf(main_id));
                    notifyDataSetChanged();
                }
            }
        });
        return convertView;
    }

    private class Holder{
        ImageView img_roomImg;
        TextView text_roomName;
        ImageView img_homeAdapter;
        TextView text_homeAdapter;
        ImageView img_star;
    }

}
