package com.example.acoste.projetimage;

/**
 * Created by acoste on 01/02/19.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Effects {

    Bitmap initialImg;
    Bitmap stateSave[];
    int currentIndex;

    public Bitmap getInitialImg() {
        return initialImg;
    }

    public void setInitialImg(Bitmap initialImg) {
        this.initialImg = initialImg;
    }

    public Bitmap[] getStateSave() {
        return stateSave;
    }

    public void setStateSave(Bitmap[] stateSave) {
        this.stateSave = stateSave;
    }

    public int getcurrentIndex() {
        return currentIndex;
    }

    public void setcurrentIndex(int currentIndex) {
        if(currentIndex >= 0 && currentIndex < stateSave.length){
            this.currentIndex = currentIndex;
        }
    }

    public Bitmap getCurrentImg(){
        return stateSave[currentIndex];
    }

    public Bitmap undo(){
        setcurrentIndex(this.currentIndex - 1);
        return getCurrentImg();
    }

    public Bitmap redo(){
        setcurrentIndex(this.currentIndex + 1);
        return this.stateSave[currentIndex];
    }




}
