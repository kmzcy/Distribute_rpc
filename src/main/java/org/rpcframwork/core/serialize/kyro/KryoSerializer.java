package org.rpcframwork.core.serialize.kyro;

import org.rpcframwork.core.serialize.Serializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.rpcframwork.core.rpc_protocol.RpcRequest;
import org.rpcframwork.core.rpc_protocol.RpcResponse;

public class KryoSerializer implements Serializer {
    /**
     * 每个thread都拥有一个自己的kryo，保证其线程安全性。
     */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try(ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayInputStream);)
        {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("serialize exception");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream))
        {
            Kryo kryo = kryoThreadLocal.get();
            // byte->Object:从byte数组中反序列化出对对象
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return clazz.cast(o);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("deserialize exception");
        }
    }

}
