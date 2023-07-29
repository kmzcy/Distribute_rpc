package refertools;

class Result<T> {
    private int code;
    private String message;
    private T data;

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(Builder<T> builder) {
        this.code = builder.code;
        this.message = builder.message;
        this.data = builder.data;
    }
    public static Builder newBuilder(){
        return new Builder();
    }

    public static final class Builder<T> {
        private int code;
        private String message;
        private T data;

        private Builder() {
        }

        public Builder code(int val) {
            code = val;
            return this;
        }

        public Builder message(String val) {
            message = val;
            return this;
        }

        public Builder data(T val) {
            data = val;
            return this;
        }

        public Result build() {
            return new Result(this);
        }
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}

public class builderTest {
    public static void main(String[] args){
        // Result<String> result3 = new Result.Builder<String>().code(404).message("failure").data("11111").build();
        Result<String> result4 = Result.newBuilder().code(404).message("failure").build();
        System.out.println(result4);
    }
}
