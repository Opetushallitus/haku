package fi.vm.sade.oppija.ui.provider;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class BaseExceptionMapper {
    public static final Logger LOGGER = LoggerFactory.getLogger(BaseExceptionMapper.class);
    public static final String ERROR_PAGE = "/error/error";
    public static final String MODEL_STACK_TRACE = "stackTrace";
    public static final String MODEL_MESSAGE = "message";
    public static final String ERROR_ID = "error_id";
    public static final String ERROR_TIMESTAMP = "timestamp";

    Map<String, String> createModel(Throwable exception) {
        String timestamp = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
        String uuid = UUID.randomUUID().toString();
        LOGGER.error("Error: " + uuid, exception);
        return ImmutableMap.of(
                MODEL_STACK_TRACE, exception.toString(),
                MODEL_MESSAGE, exception.getMessage(),
                ERROR_ID, uuid,
                ERROR_TIMESTAMP, timestamp);
    }

}
