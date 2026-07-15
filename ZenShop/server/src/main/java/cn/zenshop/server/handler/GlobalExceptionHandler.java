package cn.zenshop.server.handler;

import cn.zenshop.common.constant.MessageConstant;
import cn.zenshop.common.exception.BaseException;
import cn.zenshop.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获 @Valid 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("参数校验失败");
        log.error("参数校验失败: {}", msg);
        return Result.error(msg);
    }
    /**
     * 捕获自定义业务异常
     */
    @ExceptionHandler(BaseException.class)
    public Result<String> handleBaseException(BaseException e) {
        log.error("BaseException: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 捕获静态资源不存在异常（favicon.ico、Chrome DevTools 请求等），静默处理
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Result<String> handleNoResource(NoResourceFoundException e) {
        log.error("静态资源不存在: {}", e.getResourcePath());
        return Result.error("资源不存在");
    }

    /**
     * 捕获 SQL 异常，不暴露详细报错信息给前端
     */
    @ExceptionHandler(DataAccessException.class)
    public Result<String> handleDataAccessException(DataAccessException e) {
        log.error("SQL异常: ", e);
        return Result.error(MessageConstant.UN_KNOW_ERROR);
    }

    /**
     * 捕获未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("未知异常:", e);
        return Result.error(MessageConstant.UN_KNOW_ERROR);
    }
}
