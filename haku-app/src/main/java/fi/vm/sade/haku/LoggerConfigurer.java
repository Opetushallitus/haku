package fi.vm.sade.haku;

import java.io.InputStream;
import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LoggerConfigurer {
    public LoggerConfigurer(String filename) {
        final URL resource = getClass().getClassLoader().getResource(filename);
        PropertyConfigurator.configure(resource);
        Logger.getLogger(LoggerConfigurer.class).info("Using log4j configuration file " + resource);
    }
}
