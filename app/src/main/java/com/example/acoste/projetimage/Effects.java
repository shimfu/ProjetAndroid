package com.example.acoste.projetimage;

/**
 * Created by acoste on 01/02/19.
 */

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Effects {

    static Bitmap initialImg;
    static Bitmap currentImg;

    static List<Bitmap> stateSave;//we will get an arbitrary size of 3
    //String statePath[];//the effects the user apply consecutivly(not implemented)
    static int currentIndex;


    public void setCurrentImg(Bitmap currentImg) {
        this.currentImg = currentImg;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    static int getCurrentIndex() {
        return currentIndex;
    }

    public Effects(Bitmap bMap){
        initialImg = bMap;

        currentImg = initialImg.copy(initialImg.getConfig(), true);;
        stateSave = new ArrayList<Bitmap>();

        currentIndex = 0;
    }

    static Bitmap getInitialImg() {
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

    static int getcurrentIndex() {
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
        return currentImg;
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

    public void reset(){
       this.currentImg = initialImg.copy(initialImg.getConfig(), true);;
    }

    public void grey(){
        setCurrentImg(Simple.grey(getCurrentImg()));
    }

    public void keepColor(int param){
        setCurrentImg(Simple.keepColor(getCurrentImg(), param));
    }

    public void comboEffects (Context context, int param){
        Advanced.blur_moy_RS(getCurrentImg(), context, param);
        Simple.randomHueRS(getCurrentImg(), context,10);
        Advanced.equalization_contrast_ARGB(getCurrentImg());
    }

    public void randomHue(){
        Random rn = new Random();
        int hue = rn.nextInt(361);
        setCurrentImg(Simple.randomHue(getCurrentImg(),hue));
    }

    public void toGreyRS(Context context){
        Simple.toGreyRS(getCurrentImg(), context);
    }

    public void keepColorRS(int param, Context context){
        Simple.keepColorRS(getCurrentImg(), param, context);
    }

    public void randomHueRS(Context context){
        Random random = new Random();
        int r = random.nextInt(361);
        Simple.randomHueRS(getCurrentImg(), context,r);
    }

    public void blur(int param, int mask[][]){
        Advanced.blur(getCurrentImg(), param, mask );
    }

    public void outline(){
        Advanced.outline(getCurrentImg());
    }

    public void linear_contrast_ARGB(){
        Advanced.linear_contrast_ARGB(getCurrentImg());
    }

    public void linear_contrast_HSV(int param){
        Advanced.linear_contrast_HSV(getCurrentImg(), param);
    }

    public void equalization_contrast_HSV(int param){
        Advanced.equalization_contrast_HSV(getCurrentImg(), param);
    }

    public void equalization_contrast_ARGB(){
        Advanced.equalization_contrast_ARGB(getCurrentImg());
    }

    public void linearContrastRS(Context context){
        Advanced.linearContrastRS(getCurrentImg(), context);
    }

    public void equalization_contrast_RS(Context context){
        Advanced.equalization_contrast_RS(getCurrentImg(), context);
    }

    public void blur_moy_RS(Context context, int param){
        Advanced.blur_moy_RS(getCurrentImg(), context, param);
    }

    public void blur_gaussian5x5_RS(Context context){
        Advanced.blur_gaussian5x5_RS(getCurrentImg(), context);
    }

    public void sobel_horizontal_RS(Context context){
        Advanced.sobel_horizontal_RS(getCurrentImg(), context);
    }

    public void sobel_vertical_RS(Context context){
        Advanced.sobel_vertical_RS(getCurrentImg(), context);
    }

    public void laplacian_mask_RS(Context context){
        Advanced.laplacian_mask_RS(getCurrentImg(), context);
    }
}