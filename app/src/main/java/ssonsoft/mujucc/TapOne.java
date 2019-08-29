package ssonsoft.mujucc;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class TapOne extends Fragment {

    GridView mGridView;                   //그리드뷰

    private List<String> nameListHiangto = new ArrayList<String>();
    private List<String> addrNumListHiangto = new ArrayList<String>();
    private List<String> imgListHiangto = new ArrayList<String>();

    public TapOne() { //After adding this constructor, compile time error is removed

    }

    public TapOne(List<String> nameArry, List<String> bookNum, List<String> imgAddr) {
        this.nameListHiangto = nameArry;
        this.addrNumListHiangto = bookNum;
        this.imgListHiangto = imgAddr;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tapone, container, false);
        /*for(int i=0;i<nameListHiangto.size();i++){
            Log.d("111111", nameListHiangto.get(i));
            Log.d("111111", addrNumListHiangto.get(i));
            Log.d("111111", imgListHiangto.get(i));
        };*/

        mGridView = (GridView) view.findViewById(R.id.gridTapOne);
        mGridView.setAdapter(new ImageAdapter(getContext(), nameListHiangto, addrNumListHiangto, imgListHiangto));
        return view;
    }
}
