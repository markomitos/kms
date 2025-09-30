package uns.ftn.kms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.logstash.logback.argument.StructuredArguments;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uns.ftn.kms.annotations.KmsAuditLog;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger auditLogger = LoggerFactory.getLogger("KmsAuditLogger");
    private final ObjectMapper objectMapper;

    public LoggingAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(uns.ftn.kms.annotations.KmsAuditLog)")
    public Object logKmsAction(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        MDC.put("correlationId", UUID.randomUUID().toString());
        Object result = null;
        String status = "SUCCESS";
        String errorMessage = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            status = "FAILURE";
            errorMessage = e.getMessage();
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> logData = new HashMap<>();

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            KmsAuditLog auditAnnotation = method.getAnnotation(KmsAuditLog.class);

            logData.put("log_type", "audit");
            logData.put("event_name", auditAnnotation.action());
            logData.put("status", status);
            logData.put("duration_ms", duration);
            logData.put("actor", "predefined-user-role");

            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            Map<String, String> source = new HashMap<>();
            if (request != null) {
                source.put("ip_address", request.getRemoteAddr());
                source.put("user_agent", request.getHeader("User-Agent"));
            }
            logData.put("source", source);

            Map<String, Object> methodArgs = new HashMap<>();
            String[] parameterNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();

            for (int i = 0; i < parameterNames.length; i++) {
                String paramName = parameterNames[i];
                Object argValue = args[i];

                if (argValue == null) {
                    methodArgs.put(paramName, null);
                    continue;
                }

                Package argPackage = argValue.getClass().getPackage();

                if (argValue instanceof String || argValue instanceof UUID || argValue instanceof Number || argValue instanceof Boolean) {
                    methodArgs.put(paramName, argValue);
                }
                else if (argPackage != null && argPackage.getName().startsWith("uns.ftn.kms.dtos")) {
                    try {
                        methodArgs.put(paramName, objectMapper.convertValue(argValue, Map.class));
                    } catch (Exception e) {
                        methodArgs.put(paramName, "REDACTED_DTO_SERIALIZATION_FAILED: " + argValue.getClass().getSimpleName());
                    }
                } else if (argValue instanceof byte[]) {
                    methodArgs.put(paramName, "REDACTED_BYTE_ARRAY");
                } else {
                    methodArgs.put(paramName, "REDACTED_UNSUPPORTED_TYPE: " + argValue.getClass().getSimpleName());
                }
            }
            logData.put("request_parameters", methodArgs);

            if (errorMessage != null) {
                logData.put("error_details", errorMessage);
            }

            auditLogger.info("KMS Audit Event", StructuredArguments.entries(logData));
            MDC.clear();
        }
    }
}