package org.rpcframwork.core.codec;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
// 调用编码
public class RpcRequestBody implements Serializable {
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
//
//    RpcRequestBody(construct cons){
//        this.interfaceName = cons.interfaceName;
//        this.methodName = cons.methodName;
//        this.parameters = cons.parameters;
//        this.paramTypes = cons.paramTypes;
//    }
//
//    public static construct builderaConsturctor(){
//        return new construct();
//    }
//    public static class construct{
//
//        private String interfaceName;
//        private String methodName;
//        private Object[] parameters;
//        private Class<?>[] paramTypes;
//
//        public construct setInterfaceName(String interfaceName){
//            this.interfaceName = interfaceName;
//            return this;
//        }
//
//        public construct setMethodName(String methodName){
//            this.methodName = methodName;
//            return this;
//        }
//
//        public construct setParamTypes(Class<?>[] paramTypes){
//            this.paramTypes = paramTypes;
//            return this;
//        }
//
//        public construct setParameters(Object[] parameters){
//            this.parameters = parameters;
//            return this;
//        }
//
//        public RpcRequestBody build(){
//            return new RpcRequestBody(this);
//        }
//    }
}
