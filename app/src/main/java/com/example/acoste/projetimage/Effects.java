package com.example.acoste.projetimage;

/**
 * Created by acoste on 01/02/19.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

public class Effects {

    Bitmap initialImg;
    List<Bitmap> stateSave;//we will get an abitrary size of 3
    String statePath[];//the effects the user apply consecutivly(not implemented)
    int currentIndex;

    public Effects(Bitmap bMap){
        initialImg = bMap;
        stateSave = new ArrayList<Bitmap>();
    }


    public Bitmap getInitialImg() {
        return initialImg;
    }

    public void setInitialImg(Bitmap initialImg) {
        this.initialImg = initialImg;
    }

    public List<Bitmap> getStateSave() {
        return stateSave;
    }

    public void setStateSave(List<Bitmap> stateSave) {
        this.stateSave = stateSave;
    }

    public int getcurrentIndex() {
        return currentIndex;
    }

    public boolean setcurrentIndex(int currentIndex) {
        if(currentIndex >= 0 && currentIndex < 3){//IL FAUT UNE CONSTANTE SIZEMAX (bonus, calculer sizemax)
            this.currentIndex = currentIndex;
            return true;
        }
        return false;
    }

    public Bitmap getCurrentImg(){
        return stateSave.get(currentIndex);
    }

    public Bitmap undo(){
        setcurrentIndex(this.currentIndex - 1);
        return getCurrentImg();

    }

    public Bitmap redo(){
        setcurrentIndex(this.currentIndex + 1);
        return this.stateSave.get(currentIndex);
    }

    public void save(){
        if(!setcurrentIndex(this.currentIndex + 1)){
            stateSave.remove(0);
            setcurrentIndex(this.currentIndex - 1);
        }
        stateSave.add(stateSave.get(currentIndex));
        setcurrentIndex(this.currentIndex + 1);



    }




}
