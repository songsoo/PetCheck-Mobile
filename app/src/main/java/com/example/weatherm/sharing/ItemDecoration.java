package com.example.weatherm.sharing;

import android.content.Context;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ItemDecoration extends RecyclerView.ItemDecoration{
    private int size10;
    private int size5;

    public ItemDecoration(Context context) {

        size10 = dpToPx(context, 10);
        size5 = dpToPx(context, 5);
    }

    // dp -> pixel 단위로 변경
    private int dpToPx(Context context, int dp) {

        return (int) TypedValue.applyDimension
                (TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
    @Override
    public void getItemOffsets
            (@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);
        int itemCount = state.getItemCount();
//
//        //상하 설정
//        if(position == 0 || position == 1) {
//            // 첫번 째 줄 아이템
//            outRect.top = size10;
//            outRect.bottom = size10;
//        } else {
            outRect.top = size10;
            outRect.bottom = size10;
            outRect.right=size10;
            outRect.left=size10;
//        }

        // spanIndex = 0 -> 왼쪽
        // spanIndex = 1 -> 오른쪽
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        int spanIndex = lp.getSpanIndex();

        if(spanIndex == 0) {
            //왼쪽 아이템
            outRect.left = size10;
            outRect.right = size5;

        } else if(spanIndex == 1) {
            //오른쪽 아이템
            outRect.left = size5;
            outRect.right = size10;
        }

    }
}


