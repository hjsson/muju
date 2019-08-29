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
public class TapTwo extends Fragment {

    GridView mGridView;
    private List<String> nameListBan = new ArrayList<String>();
    private List<String> AddrNumListBan = new ArrayList<String>();
    private List<String> imgListBan = new ArrayList<String>();

    public TapTwo() { //After adding this constructor, compile time error is removed

    };

    public TapTwo(List<String> nameArry, List<String> bookNum, List<String> imgAddr) {
        this.nameListBan = nameArry;
        this.AddrNumListBan = bookNum;
        this.imgListBan = imgAddr;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.taptwo, container, false);

        mGridView = (GridView) view.findViewById(R.id.gridTapTwo);
        mGridView.setAdapter(new ImageAdapter(getContext(), nameListBan, AddrNumListBan, imgListBan));
        return view;
    }
}
