package com.shuangse.ui;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class SelectRedDialog extends Dialog {
  Context context;
  private ImageButton[] redButton = new ImageButton[33];
  private View selectRedView;
  private Button returnButton;
  private ArrayList<Integer> selectedRedNumbers = new ArrayList<Integer>();
  private SelectRedDialogCallback parentActivity;

  public SelectRedDialog(Context context, SelectRedDialogCallback parentActivity) {
    super(context);
    this.context = context;
    this.parentActivity = parentActivity;
  }

  public SelectRedDialog(Context context, int theme, SelectRedDialogCallback parentActivity) {
    super(context, theme);
    this.context = context;
    this.parentActivity = parentActivity;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.selectreddialog);
    selectRedView = this.findViewById(R.id.selredcontainer);
    this.findAndSetButtonViewAction();
    
    returnButton = (Button)selectRedView.findViewById(R.id.returnbtn);
    this.selectedRedNumbers.clear();
    returnButton.setOnClickListener(new View.OnClickListener() {
      
      @Override
      public void onClick(View v) {
        parentActivity.sendSelectedRedData(selectedRedNumbers);
        SelectRedDialog.this.dismiss();
      }
    });
  }
  
  private void findAndSetButtonViewAction() {
    redButton[0] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn1);
    redButton[0].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[0].setImageResource(R.drawable.blankr1);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(1));
        } else {
            btnState.ifClicked = true;
            redButton[0].setImageResource(R.drawable.red1);
            selectedRedNumbers.add(1);
        }
      }
    });
    redButton[1] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn2);
    redButton[1].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[1].setImageResource(R.drawable.blankr2);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(2));
        } else {
            btnState.ifClicked = true;
            redButton[1].setImageResource(R.drawable.red2);
            selectedRedNumbers.add(2);
        }
      }
    });
    redButton[2] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn3);
    redButton[2].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[2].setImageResource(R.drawable.blankr3);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(3));
        } else {
            btnState.ifClicked = true;
            redButton[2].setImageResource(R.drawable.red3);
            selectedRedNumbers.add(3);
        }
      }
    });
    redButton[3] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn4);
    redButton[3].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[3].setImageResource(R.drawable.blankr4);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(4));
        } else {
            btnState.ifClicked = true;
            redButton[3].setImageResource(R.drawable.red4);
            selectedRedNumbers.add(4);
        }
      }
    });
    redButton[4] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn5);
    redButton[4].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[4].setImageResource(R.drawable.blankr5);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(5));
        } else {
            btnState.ifClicked = true;
            redButton[4].setImageResource(R.drawable.red5);
            selectedRedNumbers.add(5);
        }
      }
    });
    redButton[5] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn6);
    redButton[5].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[5].setImageResource(R.drawable.blankr6);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(6));
        } else {
            btnState.ifClicked = true;
            redButton[5].setImageResource(R.drawable.red6);
            selectedRedNumbers.add(6);
        }
      }
    });
    redButton[6] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn7);
    redButton[6].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[6].setImageResource(R.drawable.blankr7);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(7));
        } else {
            btnState.ifClicked = true;
            redButton[6].setImageResource(R.drawable.red7);
            selectedRedNumbers.add(7);
        }
      }
    });
    redButton[7] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn8);
    redButton[7].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[7].setImageResource(R.drawable.blankr8);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(8));
        } else {
            btnState.ifClicked = true;
            redButton[7].setImageResource(R.drawable.red8);
            selectedRedNumbers.add(8);
        }
      }
    });
    redButton[8] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn9);
    redButton[8].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[8].setImageResource(R.drawable.blankr9);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(9));
        } else {
            btnState.ifClicked = true;
            redButton[8].setImageResource(R.drawable.red9);
            selectedRedNumbers.add(9);
        }
      }
    });
    redButton[9] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn10);
    redButton[9].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[9].setImageResource(R.drawable.blankr10);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(10));
        } else {
            btnState.ifClicked = true;
            redButton[9].setImageResource(R.drawable.red10);
            selectedRedNumbers.add(10);
        }
      }
    });
    redButton[10] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn11);
    redButton[10].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[10].setImageResource(R.drawable.blankr11);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(11));
        } else {
            btnState.ifClicked = true;
            redButton[10].setImageResource(R.drawable.red11);
            selectedRedNumbers.add(11);
        }
      }
    });
    redButton[11] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn12);
    redButton[11].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[11].setImageResource(R.drawable.blankr12);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(12));
        } else {
            btnState.ifClicked = true;
            redButton[11].setImageResource(R.drawable.red12);
            selectedRedNumbers.add(12);
        }
      }
    });
    redButton[12] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn13);
    redButton[12].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[12].setImageResource(R.drawable.blankr13);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(13));
        } else {
            btnState.ifClicked = true;
            redButton[12].setImageResource(R.drawable.red13);
            selectedRedNumbers.add(13);
        }
      }
    });
    redButton[13] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn14);
    redButton[13].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[13].setImageResource(R.drawable.blankr14);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(14));
        } else {
            btnState.ifClicked = true;
            redButton[13].setImageResource(R.drawable.red14);
            selectedRedNumbers.add(14);
        }
      }
    });
    redButton[14] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn15);
    redButton[14].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[14].setImageResource(R.drawable.blankr15);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(15));
        } else {
            btnState.ifClicked = true;
            redButton[14].setImageResource(R.drawable.red15);
            selectedRedNumbers.add(15);
        }
      }
    });
    redButton[15] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn16);
    redButton[15].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[15].setImageResource(R.drawable.blankr16);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(16));
        } else {
            btnState.ifClicked = true;
            redButton[15].setImageResource(R.drawable.red16);
            selectedRedNumbers.add(16);
        }
      }
    });
    redButton[16] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn17);
    redButton[16].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[16].setImageResource(R.drawable.blankr17);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(17));
        } else {
            btnState.ifClicked = true;
            redButton[16].setImageResource(R.drawable.red17);
            selectedRedNumbers.add(17);
        }
      }
    });
    redButton[17] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn18);
    redButton[17].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[17].setImageResource(R.drawable.blankr18);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(18));
        } else {
            btnState.ifClicked = true;
            redButton[17].setImageResource(R.drawable.red18);
            selectedRedNumbers.add(18);
        }
      }
    });
    redButton[18] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn19);
    redButton[18].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[18].setImageResource(R.drawable.blankr19);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(19));
        } else {
            btnState.ifClicked = true;
            redButton[18].setImageResource(R.drawable.red19);
            selectedRedNumbers.add(19);
        }
      }
    });
    redButton[19] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn20);
    redButton[19].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[19].setImageResource(R.drawable.blankr20);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(20));
        } else {
            btnState.ifClicked = true;
            redButton[19].setImageResource(R.drawable.red20);
            selectedRedNumbers.add(20);
        }
      }
    });
    redButton[20] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn21);
    redButton[20].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[20].setImageResource(R.drawable.blankr21);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(21));
        } else {
            btnState.ifClicked = true;
            redButton[20].setImageResource(R.drawable.red21);
            selectedRedNumbers.add(21);
        }
      }
    });
    redButton[21] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn22);
    redButton[21].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[21].setImageResource(R.drawable.blankr22);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(22));
        } else {
            btnState.ifClicked = true;
            redButton[21].setImageResource(R.drawable.red22);
            selectedRedNumbers.add(22);
        }
      }
    });
    redButton[22] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn23);
    redButton[22].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[22].setImageResource(R.drawable.blankr23);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(23));
        } else {
            btnState.ifClicked = true;
            redButton[22].setImageResource(R.drawable.red23);
            selectedRedNumbers.add(23);
        }
      }
    });
    redButton[23] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn24);
    redButton[23].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[23].setImageResource(R.drawable.blankr24);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(24));
        } else {
            btnState.ifClicked = true;
            redButton[23].setImageResource(R.drawable.red24);
            selectedRedNumbers.add(24);
        }
      }
    });
    redButton[24] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn25);
    redButton[24].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[24].setImageResource(R.drawable.blankr25);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(25));
        } else {
            btnState.ifClicked = true;
            redButton[24].setImageResource(R.drawable.red25);
            selectedRedNumbers.add(25);
        }
      }
    });
    redButton[25] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn26);
    redButton[25].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[25].setImageResource(R.drawable.blankr26);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(26));
        } else {
            btnState.ifClicked = true;
            redButton[25].setImageResource(R.drawable.red26);
            selectedRedNumbers.add(26);
        }
      }
    });
    redButton[26] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn27);
    redButton[26].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[26].setImageResource(R.drawable.blankr27);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(27));
        } else {
            btnState.ifClicked = true;
            redButton[26].setImageResource(R.drawable.red27);
            selectedRedNumbers.add(27);
        }
      }
    });
    redButton[27] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn28);
    redButton[27].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[27].setImageResource(R.drawable.blankr28);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(28));
        } else {
            btnState.ifClicked = true;
            redButton[27].setImageResource(R.drawable.red28);
            selectedRedNumbers.add(28);
        }
      }
    });
    redButton[28] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn29);
    redButton[28].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[28].setImageResource(R.drawable.blankr29);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(29));
        } else {
            btnState.ifClicked = true;
            redButton[28].setImageResource(R.drawable.red29);
            selectedRedNumbers.add(29);
        }
      }
    });
    redButton[29] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn30);
    redButton[29].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[29].setImageResource(R.drawable.blankr30);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(30));
        } else {
            btnState.ifClicked = true;
            redButton[29].setImageResource(R.drawable.red30);
            selectedRedNumbers.add(30);
        }
      }
    });
    redButton[30] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn31);
    redButton[30].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[30].setImageResource(R.drawable.blankr31);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(31));
        } else {
            btnState.ifClicked = true;
            redButton[30].setImageResource(R.drawable.red31);
            selectedRedNumbers.add(31);
        }
      }
    });
    redButton[31] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn32);
    redButton[31].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[31].setImageResource(R.drawable.blankr32);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(32));
        } else {
            btnState.ifClicked = true;
            redButton[31].setImageResource(R.drawable.red32);
            selectedRedNumbers.add(32);
        }
      }
    });
    redButton[32] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn33);
    redButton[32].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[32].setImageResource(R.drawable.blankr33);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(33));
        } else {
            btnState.ifClicked = true;
            redButton[32].setImageResource(R.drawable.red33);
            selectedRedNumbers.add(33);
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
