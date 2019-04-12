#pragma  version (1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.android.rssample)

#include "rs_debug.rsh"

uint32_t intensity;//maximum distance around the enhanced pixel (Uniform norm)

int32_t width;//width of the Bitmap image
int32_t height;//height of the Bitmap image
uchar4 *data;// 1D array wich contain all the values of each pixel of the Bitmap



void map_img(rs_allocation bmp, rs_allocation data) {//this function fill dada with the values of the Bitmap
    for(int i = 0 ; i<width ; i++){//for each collumn
        for(int j = 0 ; j<height ; j++){//for each line
            uchar4 copy = rsGetElementAt_uchar4(bmp, i, j);//we get the Bitmap "bmp" value (Bitmap "bmp" has 2 Dimension)
            rsSetElementAt_uchar4(data, copy, j*width + i);//we copy it in data (data has 1 Dimension)
        }
    }
}

static float4 median(float4 pixel_list[], int list_size){
    if(list_size%2 == 0){
        return pixel_list[(list_size/2)];
    }else{
        float4 moy = {(pixel_list[(list_size/2)].r + pixel_list[(list_size/2) + 1].r)/2,
                        (pixel_list[(list_size/2)].g + pixel_list[(list_size/2) + 1].g)/2,
                        (pixel_list[(list_size/2)].b + pixel_list[(list_size/2) + 1].b)/2,
                        (pixel_list[(list_size/2)].a + pixel_list[(list_size/2) + 1].a)/2};
        return moy;
    }

}

static float grey(float4 color){
    return 0.30*color.r + 0.59*color.g + 0.11*color.b;
}

static void merge(float4 list[],int i1,int j1,int i2,int j2)
{
	float4 temp[(2*intensity + 1)*(2*intensity + 1)];//array used for merging
	int i=i1;	//beginning of the first list
	int j=i2;	//beginning of the second list
	int k=0;

	while(i<=j1 && j<=j2){	//while elements in both lists

		if(grey(list[i])<grey(list[j])){
			temp[k]=list[i];
			k = k+1;
			i = i+1;
		}else{
			temp[k]=list[j];
			k = k+1;
			j = j+1;
		}
	}

	while(i<=j1){	//copy remaining elements of the first list
		temp[k]=list[i];
		k = k+1;
		i = i+1;
    }
	while(j<=j2){	//copy remaining elements of the second list
		temp[k]=list[j];
		k = k+1;
		j = j+1;
	}
	//Transfer elements from temp[] back to list[]

	for(i=i1,j=0;i<=j2;i++,j++){
		list[i]=temp[j];
	}
}

static void mergesort(float4 list[],int i,int j)
{
	int mid;

	if(i<j)
	{
		mid=(i+j)/2;
		mergesort(list,i,mid);		//left recursion
		mergesort(list,mid+1,j);	//right recursion
		merge(list,i,mid,mid+1,j);	//merging of two sorted sub-arrays
	}
}

uchar4 RS_KERNEL compute_data(uchar4 in, uint32_t x, uint32_t y) {//For each pixel at once

    float4 pixelin = rsUnpackColor8888(in);

    float4 pixel_list[(2*intensity + 1)*(2*intensity + 1)];

    float4 pixeldata;//this memory will store de data's values

    int list_size = 0;
    for(int i = x-intensity ; i < x+intensity+1 ; i++){//for each collumn of the mask
        for(int j = y-intensity ; j < y+intensity+1; j++){//for each line of the mask

            if(i >= 0 && j >= 0 && i < width && j < height){//checking if we are in the limits of the image
                pixeldata = rsUnpackColor8888(data[j*width + i]);//we get the values of 1 pixel in the mask
                pixel_list[list_size] = pixeldata;
                list_size = list_size + 1;
            }
        }
    }
    list_size = list_size-1;
    mergesort(pixel_list,0,list_size);
    float4 newPixel = median(pixel_list,list_size);

    return rsPackColorTo8888(newPixel.r,newPixel.g,newPixel.b,pixelin.a);
}