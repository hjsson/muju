/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ssonsoft.mujucc;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class ImageAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;

    private List<String> bookNames;
    private List<String> bookNumbers;
    private List<String> imageUrls;

    public ImageAdapter(Context context, List<String> names, List<String> numbers, List<String> imageUrls) {
        super(context, R.layout.grid_row, imageUrls);

        this.context = context;
        this.imageUrls = imageUrls;
        this.bookNames = names;
        this.bookNumbers = numbers;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.grid_row, parent, false);
            holder = new ViewHolder();  //홀더가 없으면 스크롤시 이미지가 섞임
            holder.hImg = (ImageView) convertView.findViewById(R.id.imgview);
            holder.hText = (TextView) convertView.findViewById(R.id.mText);
            holder.hNum = (TextView) convertView.findViewById(R.id.mNum);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.hText.setText(bookNames.get(position));
        holder.hNum.setText(bookNumbers.get(position));

        holder.hImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), bookNames.get(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), BookView.class);
                intent.putExtra("display", bookNumbers.get(position));
                context.startActivity(intent);

            }

        });
        //글라이더 이미지 로딩셋
        Glide
                .with(context)
                .load(imageUrls.get(position))
                .placeholder(R.drawable.x00)
                .error(R.drawable.intro)
                .into((holder.hImg));

        return convertView;
    }

    static class ViewHolder {
        ImageView hImg;
        TextView hText;
        TextView hNum;
    }

}