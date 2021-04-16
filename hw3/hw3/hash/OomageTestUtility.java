package hw3.hash;

import java.util.List;

public class OomageTestUtility {
    public static boolean haveNiceHashCodeSpread(List<Oomage> oomages, int M) {

        int[] buckets=new int [M];
        for (Oomage o:oomages){
            int bucketNum = (o.hashCode() & 0x7FFFFFFF) % M;
            buckets[bucketNum]++;
            if (buckets[bucketNum]>oomages.size()/2.5){
                return false;
            }
        }
        for (int i=0;i<buckets.length;i++){
            if (buckets[i]<oomages.size()/50.0)
                return false;
        }
        return true;
    }
}
