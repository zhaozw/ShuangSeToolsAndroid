package com.shuangse.ui;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class SelectBlueDialog extends Dialog {
  Context context;
  private ImageButton[] blueButton = new ImageButton[16];
  private View selectBlueView;
  private Button returnButton;
  private ArrayList<Integer> selectedBlueNumbers = new ArrayList<Integer>();
  private SelectBlueDialogCallback parentActivity;

  public SelectBlueDialog(Context context, SelectBlueDialogCallback parentActivity) {
    super(context);
    this.context = context;
    this.parentActivity = parentActivity;
  }

  public SelectBlueDialog(Context context, int theme, SelectBlueDialogCallback parentActivity) {
    super(context, theme);
    this.context = context;
    this.parentActivity = parentActivity;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.selectbluedialog);
    selectBlueView = this.findViewById(R.id.selbluecontainer);
    this.findAndSetButtonViewAction();
    
    returnButton = (Button)selectBlueView.findViewById(R.id.returnbtn);
    this.selectedBlueNumbers.clear();
    returnButton.setOnClickListener(new View.OnClickListener() {
      
      @Override
      public void onClick(View v) {
        parentActivity.sendSelectedBlueData(selectedBlueNumbers);
        SelectBlueDialog.this.dismiss();
      }
    });
  }
  
  private void findAndSetButtonViewAction() {
    blueButton[0] = (ImageButton) selectBlueView.findViewById(R.id.blankblue1);
    blueButton[0].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            blueButton[0].setImageResource(R.drawable.blankblue1);
            btnState.ifClicked = false;
            selectedBlueNumbers.remove(Integer.valueOf(1));
        } else {
            btnState.ifClicked = true;
            blueButton[0].setImageResource(R.drawable.blue1);
            selectedBlueNumbers.add(1);
        }
      }
    });
    blueButton[1] = (ImageButton) selectBlueView.findViewById(R.id.blankblue2);
    blueButton[1].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            blueButton[1].setImageResource(R.drawable.blankblue2);
            btnState.ifClicked = false;
            selectedBlueNumbers.remove(Integer.valueOf(2));
        } else {
            btnState.ifClicked = true;
            blueButton[1].setImageResource(R.drawable.blue2);
            selectedBlueNumbers.add(2);
        }
      }
    });
    blueButton[2] = (ImageButton) selectBlueView.findViewById(R.id.blankblue3);
    blueButton[2].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            blueButton[2].setImageResource(R.drawable.blankblue3);
            btnState.ifClicked = false;
            selectedBlueNumbers.remove(Integer.valueOf(3));
        } else {
            btnState.ifClicked = true;
            blueButton[2].setImageResource(R.drawable.blue3);
            selectedBlueNumbers.add(3);
        }
      }
    });
    blueButton[3] = (ImageButton) selectBlueView.findViewById(R.id.blankblue4);
    blueButton[3].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            blueButton[3].setImageResource(R.drawable.blankblue4);
            btnState.ifClicked = false;
            selectedBlueNumbers.remove(Integer.valueOf(4));
        } else {
            btnState.ifClicked = true;
            blueButton[3].setImageResource(R.drawable.blue4);
            selectedBlueNumbers.add(4);
        }
      }
    });
    blueButton[4] = (ImageButton) selectBlueView.findViewById(R.id.blankblue5);
    blueButton[4].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            blueButton[4].setImageResource(R.drawable.blankblue5);
            btnState.ifClicked = false;
            selectedBlueNumbers.remove(Integer.valueOf(5));
        } else {
            btnState.ifClicked = true;
            blueButton[4].setImageResource(R.drawable.blue5);
            selectedBlueNumbers.add(5);
        }
      }
    });
    blueButton[5] = (ImageButton) selectBlueView.findViewById(R.id.blankblue6);
    blueButton[5].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            blueButton[5].setImageResource(R.drawable.blankblue6);
            btnState.ifClicked = false;
            selectedBlueNumbers.remove(Integer.valueOf(6));
        } else {
            btnState.ifClicked = true;
            blueButton[5].setImageResource(R.drawable.blue6);
            selectedBlueNumbers.add(6);
        }
      }
    });
    blueButton[6] = (ImageButton) selectBlueView.findViewById(R.id.blankblue7);
    blueButton[6].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            blueButton[6].setImageResource(R.drawable.blankblue7);
            btnState.ifClicked = false;
            selectedBlueNumbers.remove(Integer.valueOf(7));
        } else {
            btnState.ifClicked = true;
            blueButton[6].setImageResource(R.drawable.blue7);
            selectedBlueNumbers.add(7);
        }
      }
    });
    blueButton[7] = (ImageButton) selectBlueView.findViewById(R.id.blankblue8);
    blueButton[7].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            blueButton[7].setImageResource(R.drawable.blankblue8);
            btnState.ifClicked = false;
            selectedBlueNumbers.remove(Integer.valueOf(8));
        } else {
            btnState.ifClicked = true;
            blueButton[7].setImageResource(R.drawable.blue8);
            selectedBlueNumbers.add(8);
        }
      }
    });
    blueButton[8] = (ImageButton) selectBlueView.findViewById(R.id.blankblue9);
    blueButton[8].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            blueButton[8].setImageResource(R.drawable.blankblue9);
            btnState.ifClicked = false;
            selectedBlueNumbers.remove(Integer.valueOf(9));
        } else {
            btnState.ifClicked = true;
            blueButton[8].setImageResource(R.drawable.blue9);
            selectedBlueNumbers.add(9);
        }
      }
    });
    blueButton[9] = (ImageButton) selectBlueView.findViewById(R.id.blankblue10);
    blueButton[9].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            blueButton[9].setImageResource(R.drawable.blankblue10);
            btnState.ifClicked = false;
            selectedBlueNumbers.remove(Integer.valueOf(10));
        } else {
            btnState.ifClicked = true;
            blueButton[9].setImageResource(R.drawable.blue10);
            selectedBlueNumbers.add(10);
        }
      }
    });
    blueButton[10] = (ImageButton) selectBlueView.findViewById(R.id.blankblue11);
    blueButton[10].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            blueButton[10].setImageResource(R.drawable.blankblue11);
            btnState.ifClicked = false;
            selectedBlueNumbers.remove(Integer.valueOf(11));
        } else {
            btnState.ifClicked = true;
            blueButton[10].setImageResource(R.drawable.blue11);
            selectedBlueNumbers.add(11);
        }
      }
    });
    blueButton[11] = (ImageButton) selectBlueView.findViewById(R.id.blankblue12);
    blueButton[11].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            blueButton[11].setImageResource(R.drawable.blankblue12);
            btnState.ifClicked = false;
            selectedBlueNumbers.remove(Integer.valueOf(12));
        } else {
            btnState.ifClicked = true;
            blueButton[11].setImageResource(R.drawable.blue12);
            selectedBlueNumbers.add(12);
        }
      }
    });
    blueButton[12] = (ImageButton) selectBlueView.findViewById(R.id.blankblue13);
    blueButton[12].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            blueButton[12].setImageResource(R.drawable.blankblue13);
            btnState.ifClicked = false;
            selectedBlueNumbers.remove(Integer.valueOf(13));
        } else {
            btnState.ifClicked = true;
            blueButton[12].setImageResource(R.drawable.blue13);
            selectedBlueNumbers.add(13);
        }
      }
    });
    blueButton[13] = (ImageButton) selectBlueView.findViewById(R.id.blankblue14);
    blueButton[13].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            blueButton[13].setImageResource(R.drawable.blankblue14);
            btnState.ifClicked = false;
            selectedBlueNumbers.remove(Integer.valueOf(14));
        } else {
            btnState.ifClicked = true;
            blueButton[13].setImageResource(R.drawable.blue14);
            selectedBlueNumbers.add(14);
        }
      }
    });
    blueButton[14] = (ImageButton) selectBlueView.findViewById(R.id.blankblue15);
    blueButton[14].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            blueButton[14].setImageResource(R.drawable.blankblue15);
            btnState.ifClicked = false;
            selectedBlueNumbers.remove(Integer.valueOf(15));
        } else {
            btnState.ifClicked = true;
            blueButton[14].setImageResource(R.drawable.blue15);
            selectedBlueNumbers.add(15);
        }
      }
    });
    blueButton[15] = (ImageButton) selectBlueView.findViewById(R.id.blankblue16);
    blueButton[15].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            blueButton[15].setImageResource(R.drawable.blankblue16);
            btnState.ifClicked = false;
            selectedBlueNumbers.remove(Integer.valueOf(16));
        } else {
            btnState.ifClicked = true;
            blueButton[15].setImageResource(R.drawable.blue16);
            selectedBlueNumbers.add(16);
        }
      }
    });
  }

  static class ButtonState {
      public ButtonState() {
       ifClicked = false;
      }
      public boolean ifClicked;
  };
  
}
