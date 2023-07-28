package refertools;

import java.io.*;
import java.util.HashMap;

public class Serialize_Map {
    public static void main(String [] args) {
        HashMap<Integer, String> hmap = new HashMap<Integer, String>();
        //Adding elements to HashMap
        hmap.put(11, "AB");
        hmap.put(2, "CD");
        hmap.put(33, "EF");
        hmap.put(9, "GH");
        hmap.put(3, "IJ");
        try {
            FileOutputStream fos = new FileOutputStream("hashmap.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(hmap);
            oos.close();
            fos.close();

            System.out.printf("Serialized HashMap data is saved in hashmap.ser");
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
        HashMap<Integer, String> map_get = null;

        try{
            FileInputStream fis = new FileInputStream("hashmap.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            map_get = (HashMap<Integer, String>)ois.readObject();

        }catch(ClassNotFoundException | IOException e){
            System.out.println("Class not found");
            e.printStackTrace();
            return;
        }
        for(Integer key : map_get.keySet()){
            System.out.println("key: " + key + " value: " + map_get.get(key));
        }

    }
}
