package com.shuangse.ui;

import com.shuangse.meta.CellData;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class UIFactory {
    
    public static ImageView makeImageCell(Drawable imgRes, Context ctx) {
        ImageView imgView = new ImageView(ctx);        
        imgView.setBackgroundColor(Color.BLACK);
        imgView.setImageDrawable(imgRes);        
        return imgView;
    }
    
    public static ImageView makeImageCellWithBackground(Drawable imgRes, Context ctx, int bkground) {
      ImageView imgView = new ImageView(ctx);        
      imgView.setBackgroundColor(Color.rgb(bkground & 0xff0000, bkground & 0x00ff00, bkground & 0x0000ff));
      imgView.setImageDrawable(imgRes);        
      return imgView;
  }
    
    public static TextView makeHeadCell(String titleText, Context ctx) {
        TextView textView = new TextView(ctx);

        textView.setBackgroundColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setPadding(3, 3, 3, 3);
        textView.setTextSize(20);
        textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textView.setText(titleText);
        textView.setTextColor(Color.WHITE);

        return textView;
    }    
    public static TextView makeTextCell(String dispText, int txtColor, 
            Context ctx) {
        TextView textView = new TextView(ctx);

        textView.setBackgroundColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setPadding(3, 3, 3, 3);
        textView.setTextSize(16);
        textView.setText(dispText);    
        textView.setTextColor(txtColor);
        
        return textView;
    }
    
    public static TextView makeTextCellWithBackground(String dispText, int txtColor, Context ctx, int bkcolor) {
    TextView textView = new TextView(ctx);

    textView.setBackgroundColor(Color.rgb(bkcolor & 0xff0000, bkcolor & 0x00ff00, bkcolor & 0x0000ff));
    textView.setGravity(Gravity.CENTER_HORIZONTAL);
    textView.setPadding(3, 3, 3, 3);
    textView.setTextSize(16);
    textView.setText(dispText);    
    textView.setTextColor(txtColor);
    
    return textView;
}    
    
    public static TextView makeClickableTextCell(String dispText, int txtColor,
            CellData clickCell, 
            Context ctx) {
        TextView textView = new TextView(ctx);

        textView.setBackgroundColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setPadding(3, 3, 3, 3);
        textView.setTextSize(16);
        textView.setText(dispText);    
        textView.setTextColor(txtColor);
        textView.setClickable(true);
        textView.setFocusable(true);

        if (clickCell != null) {
            //set the tag so that it can be accessed in the onClick
            textView.setTag(clickCell);
            
            textView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    CellData cellItem = (CellData) v.getTag();
                    Log.i("makeClickableTextCell", cellItem.toString());
                }
            });
        }

        return textView;
    }    
    
    public static ImageView makeBlankCell(Context ctx) {
        
        final ImageView imgView = new ImageView(ctx);
        imgView.setBackgroundColor(Color.BLACK);
        
        return imgView;        
    }
    
    @SuppressWarnings("deprecation")
    public static View makeButton(Drawable btnImg, OnClickListener listener, Context ctx) {
        LinearLayout container = new LinearLayout(ctx);
        container.setBackgroundColor(Color.BLACK);
        
        final ImageButton btn = new ImageButton(ctx);
        btn.setFocusable(true);
        btn.setClickable(true);
        btn.setOnClickListener(listener);
        btn.setBackgroundDrawable(btnImg);
        btn.setPadding(0, 0, 0, 0);
        
        container.addView(btn);
        return container;
    }
    
    public static ImageView makeClickableBlankCell(String dispText, 
            CellData clickCell, OnClickListener listener,
            Context ctx) {
        
        final ImageView imgView = new ImageView(ctx);
        if (clickCell != null) {
          imgView.setTag(clickCell);
          imgView.setOnClickListener(listener);
          if(clickCell.isClicked() == 1) {
            imgView.setImageDrawable(clickCell.getDispImg());
          } else if(clickCell.isClicked() == 2) {
            imgView.setImageDrawable(clickCell.getDoubleclickDispImg());
          } else {
            imgView.setImageDrawable(null);
          }
      }
        imgView.setClickable(true);
        imgView.setFocusable(true);
        imgView.setBackgroundColor(Color.BLACK);
        
        
        return imgView;        
    }
    
    
    public static TextView makeSeperator(Context ctx) {
        //<View android:layout_width="2px" android:background="#0000ff" /> 
        final TextView blankView = new TextView(ctx);        
        blankView.setFocusable(true);
        blankView.setBackgroundColor(Color.YELLOW);
        @SuppressWarnings("deprecation")
        TableRow.LayoutParams params = new TableRow.LayoutParams(2,TableRow.LayoutParams.FILL_PARENT);
        blankView.setLayoutParams(params); // causes layout update
        return blankView;
    }
    
}
