package Utilities;

public class IntList {
    int[] a;
    int listLength = 8;
    int pos = 0;

    public IntList(){
        a = new int[listLength];
    }

    public boolean add(int i){
        if(i<0||i>=listLength)
            return false;
        a[pos]=i;
        pos++;
        if(pos==listLength-1){
            resize();
        }

        return true;
    }

    public int get(int pos){
        return a[pos];
    }

    public void resize(){
        int[] t = new int[listLength*2];
        System.arraycopy(a,0,t,0,listLength);
        a=t;
        listLength*=2;
    }
}
