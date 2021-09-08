
package Utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class DataCleaner {

    public static ArrayList<Integer> medianFilter(ArrayList<Integer> data, int filterSize){
        ArrayList<Integer> filtered = new ArrayList<>();
        ArrayList<Integer> subsection = new ArrayList<>();

        for(int i=1; i<data.size(); i+=2){
            if(i<filterSize/2){
                filtered.add(data.get(i-1));
                filtered.add(data.get(i));
                continue;
            }
            subsection.clear();
            int filterStart =-filterSize/2+(filterSize/2)%2;
//            if(filterStart+i<0)
//                filterStart=0;

            for(int j=filterStart; j<filterSize/2; j+=2){
                int pos = j+i;
//                if(pos<0){
//                    subsection.add(0);
//                    continue;
//                }
                if(pos<data.size())
                    subsection.add(data.get(pos));
                else
                    break;
            }
            Collections.sort(subsection);
            filtered.add(data.get(i-1));
            int pos = subsection.size()/2+1;
            filtered.add(subsection.get(pos));
        }

        return filtered;
    }
}
