package com.demo.wecharfloatingdemo;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private FloatingMoveButton floatingMoveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        this.floatingMoveButton = findViewById(R.id.floating_move_btn);


        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) floatingMoveButton.getLayoutParams();
        params.leftMargin = getResources().getDisplayMetrics().widthPixels - params.width;
        floatingMoveButton.setSelfClickListener(new FloatingMoveButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPop();
            }
        });
    }

    private void showPop() {
        View view = View.inflate(this, R.layout.layout_floating_pop, null);
        view.setClickable(true);
        StateListDrawable stateListDrawable = new StateListDrawable();

        GradientDrawable pressDrawable = new GradientDrawable();
        pressDrawable.setColor(Color.parseColor("#C1C1C1"));
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressDrawable);

        GradientDrawable normalDrawable = new GradientDrawable();
        normalDrawable.setColor(Color.parseColor("#D9F8F8F8"));
        stateListDrawable.addState(new int[]{}, normalDrawable);

        view.setBackground(stateListDrawable);
        PopupWindow popupWindow = new PopupWindow(view,
                dp2px(206), dp2px(53), true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                floatingMoveButton.setVisibility(View.VISIBLE);
            }
        });
        int radius = dp2px(26);
        if (floatingMoveButton.isAnchorLeft()) {
            pressDrawable.setCornerRadii(new float[]{0, 0, radius, radius, radius, radius, 0, 0});
            normalDrawable.setCornerRadii(new float[]{0, 0, radius, radius, radius, radius, 0, 0});
            popupWindow.setAnimationStyle(R.style.LeftFloatingPopAnimator);
        } else {
            pressDrawable.setCornerRadii(new float[]{radius, radius, 0, 0, 0, 0, radius, radius});
            normalDrawable.setCornerRadii(new float[]{radius, radius, 0, 0, 0, 0, radius, radius});
            popupWindow.setAnimationStyle(R.style.RightFloatingPopAnimator);
        }
        popupWindow.showAsDropDown(floatingMoveButton);
        floatingMoveButton.setVisibility(View.GONE);
    }

    public int dp2px(float dp) {
        return Math.round(dp * this.getResources().getDisplayMetrics().density + 0.5f);
    }
}
