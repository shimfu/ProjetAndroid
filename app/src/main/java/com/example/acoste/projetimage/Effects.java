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

    private Bitmap initialImg = null;
    private Bitmap currentImg = null;

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

        currentImg = initialImg.copy(initialImg.getConfig(), true);
        stateSave = new ArrayList<Bitmap>();

        currentIndex = 0;
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
        Simple.keepColorRS(getCurrentImg(), context, param);
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

    public void linearContrastRS(Context context, int new_min, int new_max){
        Advanced.linearContrastRS(getCurrentImg(), context, new_min,new_max);
    }

    public void equalization_contrast_RS(Context context, float intensity){
        Advanced.equalization_contrast_RS(getCurrentImg(), context, intensity);
    }

    public void blur_moy_RS(Context context, int param){
        Advanced.blur_moy_RS(getCurrentImg(), context, param);
    }

    public void blur_gaussian_RS(Context context, int intensity){
        Advanced.blur_gaussian_RS(getCurrentImg(),context,intensity);
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

    public void bilateral_filter_RS( Context context, int intensity){
        Advanced.bilateralfilter_RS(getCurrentImg(), context, intensity);
    }

    public void drawOutline_RS(Context context, float edge_intensity, int outline_size ){
        Advanced.drawOutline_RS(getCurrentImg(), context, edge_intensity, outline_size);
    }

    public void sobelGradient_RS(Context context){
        Advanced.sobelGradient_RS(getCurrentImg(), context);
    }

    public void medianfilter_RS(Context context, int intensity){
        Advanced.medianfilter_RS(getCurrentImg(), context, intensity);
    }

    public void minfilter_RS(Context context, int intensity ){
        Advanced.minfilter_RS(getCurrentImg(), context, intensity);
    }

    public void colorPartition_RS (Context context, int color_precision, int shade_precision, int color_shift){
        Simple.colorPartition_RS(getCurrentImg(), context, color_precision, shade_precision, color_shift);
    }

    public void luminosity_RS(Context context, float intensity){//intensity between 0.0...1.0, substract luminosity if intensity < 0.5, add if > 0.5, change nothing at 0.5
        Simple.luminosityRS(getCurrentImg(), context, intensity);
    }

    public void pixelise_RS(Context context, int pixel_size){
        Advanced.pixelise_RS(getCurrentImg(), context, pixel_size);
    }

    public void negative_RS(Context context){
        Simple.negativeRS(getCurrentImg(), context);
    }

    public void pencil_RS(Context context, int blur_intensity){
        Advanced.pencil_RS(getCurrentImg(), context, blur_intensity);
    }

    public void cartoon_RS(Context context, int first_medianfilter_size,int minfilter_size, int final_medianfilter_size,
                           float edge_precision, int outline_size, int color_precision, int shade_precision, int color_shift){

        Advanced.cartoon_RS(getCurrentImg(),context,first_medianfilter_size,minfilter_size,
                final_medianfilter_size,edge_precision,outline_size,color_precision,shade_precision,color_shift);
    }
    
    public void sobelGradientColored_RS(Context context){
        Advanced.sobelGradientColored_RS(getCurrentImg(), context);
    }

}
