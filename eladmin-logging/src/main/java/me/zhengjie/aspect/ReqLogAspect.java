package me.zhengjie.aspect;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.RequestLog;
import me.zhengjie.service.RequestLogService;
import me.zhengjie.utils.ThrowableUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 请求日志AOP
 */
@Component
@Aspect
@Slf4j
public class ReqLogAspect {

    private final RequestLogService requestLogService;

    public ReqLogAspect(RequestLogService requestLogService) {
        this.requestLogService = requestLogService;
    }

    ThreadLocal<Long> currentTime = new ThreadLocal<>();

    /**
     * 配置切入点
     */
    @Pointcut("@annotation(me.zhengjie.annotation.ReqLog)")
    public void logPointcut() {
        // 该方法无方法体,主要为了让同类中其他方法使用此切入点
    }

    /**
     * 配置环绕通知,使用在方法logPointcut()上注册的切入点
     *
     * @param joinPoint join point for advice
     */
    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        currentTime.set(System.currentTimeMillis());
        result = joinPoint.proceed();
        RequestLog log = new RequestLog(System.currentTimeMillis() - currentTime.get());
        currentTime.remove();
        requestLogService.save(joinPoint, log);
        return result;
    }

    /**
     * 配置异常通知
     *
     * @param joinPoint join point for advice
     * @param e exception
     */
    @AfterThrowing(pointcut = "logPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        RequestLog log = new RequestLog(System.currentTimeMillis() - currentTime.get());
        currentTime.remove();
        log.setResMsg(ThrowableUtil.getStackTrace(e));
        try {
            requestLogService.save((ProceedingJoinPoint)joinPoint, log);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
