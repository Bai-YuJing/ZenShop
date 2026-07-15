package cn.zenshop.common.exception;

public class TypeNullException extends BaseException {

    public TypeNullException(String message) {
        super(message);
    }

    public TypeNullException() {
        super("参数不能为空");
    }
}
