package fi.vm.sade.haku.oppija.hakemus.resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wuoti on 4.3.2014.
 */
public class DateParam {
    public static final Logger LOGGER = LoggerFactory.getLogger(DateParam.class);

    private final Date date;

    private final static String DATE_FORMAT_STRING = "yyyyMMddHHmm";

    public DateParam(String dateStr) {
        Date date = null;
        try {
            if (StringUtils.isNotBlank(dateStr)) {
                DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
                date = dateFormat.parse(dateStr);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Parsed date {}", date);
                }
            }
        } catch (ParseException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Couldn't parse date string: " + e.getMessage())
                    .build());
        }
        this.date = date;
    }

    public Date getDate() {
        return date;
    }
}
