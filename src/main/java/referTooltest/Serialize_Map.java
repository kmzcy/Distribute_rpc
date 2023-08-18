package referTooltest;

import java.util.HashMap;
import org.rpcframwork.core.serialize.kyro.KryoSerializer;
// 对于map类的序列化操作，在远程模型之中，大部分的模型数据都需要通过map进行传输。

public class Serialize_Map {
    public static void main(String [] args) {
        HashMap<Integer, String> hmap = new HashMap<Integer, String>();
        //Adding elements to HashMap
        hmap.put(11, "AB");
        hmap.put(2, "CD");
        hmap.put(33, "EF");
        hmap.put(9, "GH");
        hmap.put(3, "IJ");

        KryoSerializer kryoSerializer = new KryoSerializer();
        byte[] bytes = kryoSerializer.serialize(hmap);


        HashMap<Integer, String> map_get = kryoSerializer.deserialize(bytes, HashMap.class);


        for(Integer key : map_get.keySet()){
            System.out.println("key: " + key + " value: " + map_get.get(key));
        }

    }
}
