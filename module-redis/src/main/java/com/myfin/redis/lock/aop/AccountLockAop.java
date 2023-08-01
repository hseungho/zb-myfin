package com.myfin.redis.lock.aop;

import com.myfin.core.util.MyFinSpringELParser;
import com.myfin.redis.lock.AccountLock;
import com.myfin.redis.lock.TransferLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 계좌 입출금 및 이체에 대한 동시성 이슈를 핸들링하기 위해 <br>
 * RedissonClient를 이용하여 특정 key에 대한 Lock을 제공. <br>
 * <br>
 * reference: <a href="https://helloworld.kurly.com/blog/distributed-redisson-lock/">Reference Link</a>
 */
@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class AccountLockAop {
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(com.myfin.redis.lock.AccountLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AccountLock accountLock = method.getAnnotation(AccountLock.class);

        String key = REDISSON_LOCK_PREFIX + MyFinSpringELParser.getDynamicValue(
                signature.getParameterNames(),
                joinPoint.getArgs(),
                accountLock.key());

        // 1) 락의 이름으로 RLock 인스턴스를 가져온다.
        RLock lock = redissonClient.getLock(key);
        log.info("[MYFIN][AccountLockAop] Account {} get Lock.", keyMasking(key));

        try {
            // 2) 정의된 waitTime까지 획득을 시도한다, 정의된 leaseTime이 지나면 잠금을 해제한다.
            boolean available = lock.tryLock(accountLock.waitTime(), accountLock.leaseTime(), accountLock.timeUnit());
            log.info("[MYFIN][AccountLockAop] Account {} try Lock.", keyMasking(key));

            if (!available) {
                log.info("[MYFIN][AccountLockAop] Account {} failed trying Lock.", keyMasking(key));
                return false;
            }

            // 3) AccountLock 어노테이션이 선언된 메서드를 별도의 트랜잭션으로 실행한다.
            return aopForTransaction.proceed(joinPoint);

        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            try {
                // 4) 종료 시 무조건 락을 해제한다.
                lock.unlock();
                log.info("[MYFIN][AccountLockAop] Account {} unlock.", keyMasking(key));
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already UnLock. serviceName -> {}, key -> {}", method.getName(), keyMasking(key));
            }
        }
    }

    @Around("@annotation(com.myfin.redis.lock.TransferLock)")
    public Object lockForTransfer(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        TransferLock transferLock = method.getAnnotation(TransferLock.class);

        String sendKey = REDISSON_LOCK_PREFIX + MyFinSpringELParser.getDynamicValue(
                signature.getParameterNames(),
                joinPoint.getArgs(),
                transferLock.sendKey());

        String receiveKey = REDISSON_LOCK_PREFIX + MyFinSpringELParser.getDynamicValue(
                signature.getParameterNames(),
                joinPoint.getArgs(),
                transferLock.receiveKey());

        RLock senderLock = redissonClient.getLock(sendKey);
        RLock receiverLock = redissonClient.getLock(receiveKey);
        log.info("[MYFIN][AccountLockAop] Sender Account {} get Lock.", keyMasking(sendKey));
        log.info("[MYFIN][AccountLockAop] Receiver Account {} get Lock.", keyMasking(receiveKey));

        try {
            boolean availableSend = senderLock.tryLock(transferLock.waitTime(), transferLock.leaseTime(), transferLock.timeUnit());
            log.info("[MYFIN][AccountLockAop] Sender Account {} try Lock.", keyMasking(sendKey));
            if (!availableSend) {
                log.info("[MYFIN][AccountLockAop] Sender Account {} failed trying Lock.", keyMasking(sendKey));
                return false;
            }

            boolean availableReceive = receiverLock.tryLock(transferLock.waitTime(), transferLock.leaseTime(), transferLock.timeUnit());
            log.info("[MYFIN][AccountLockAop] Receiver Account {} try Lock.", keyMasking(receiveKey));
            if (!availableReceive) {
                log.info("[MYFIN][AccountLockAop] Receiver Account {} failed trying Lock.", keyMasking(receiveKey));
                return false;
            }

            return aopForTransaction.proceed(joinPoint);

        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            try {
                // 4) 종료 시 무조건 락을 해제한다.
                senderLock.unlock();
                log.info("[MYFIN][AccountLockAop] Sender Account {} unlock.", keyMasking(sendKey));
                receiverLock.unlock();
                log.info("[MYFIN][AccountLockAop] Receiver Account {} unlock.", keyMasking(receiveKey));

            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already UnLock. serviceName -> {}, key -> {}",
                        method.getName(),
                        keyMasking(sendKey) + " OR " + keyMasking(receiveKey));
            }
        }
    }

    private String keyMasking(final String key) {
        int i = 5;
        String mask = "*".repeat(i);
        return new StringBuffer(key).replace(key.length()-i, key.length(), mask).toString();
    }
}
