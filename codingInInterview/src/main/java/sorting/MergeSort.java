package sorting;

import java.util.Arrays;

public class MergeSort {


    static void main() {
        int[] arr = {38,27,43,10,-1};
        mergeSort(arr, 0, arr.length-1);
        System.out.println(Arrays.toString(arr));
    }

    static void mergeSort(int[] arr, int low, int high) {
        if(low>=high) return;

        int mid = low+(high-low)/2;
        mergeSort(arr, low,mid);
        mergeSort(arr, mid+1, high);

        //merge both above sorted half's
        merge(arr, low, mid, mid+1, high);
    }

    static void merge(int[] arr, int leftStart, int leftEnd, int rightStart, int rightEnd) {
        int n1 = leftEnd-leftStart+1;
        int n2 = rightEnd-rightStart+1;
        int[] leftArr = new int[n1];
        int[] rightArr = new int[n2];

        for(int i=leftStart; i<=leftEnd; i++) {
            leftArr[i-leftStart] = arr[i];
        }

        for(int i=rightStart; i<=rightEnd; i++) {
            rightArr[i-rightStart] = arr[i];
        }

        //now merging
        int k1 = 0;
        int k2 = 0;
        int index = leftStart;
        while(k1<n1 && k2<n2) {
            if(leftArr[k1]<=rightArr[k2]) {
                arr[index++] = leftArr[k1++];
            } else {
                arr[index++] = rightArr[k2++];
            }
        }

        while(k1<n1) {
            arr[index++] = leftArr[k1++];
        }
        while(k2<n2) {
            arr[index++] = rightArr[k2++];
        }
    }



}
