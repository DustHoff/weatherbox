package gadget.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Dustin on 01.09.2015.
 */
public class Component {

    private static Properties properties = new Properties();

    static {
        try {
            properties.load(Component.class.getResourceAsStream("/data.properties"));
        } catch (IOException e) {
            LogManager.getRootLogger().error("Problem while Loading Configuration", e);
        }
    }

    protected final Logger LOG;
    private final String file;

    public Component() {
        LOG = LogManager.getLogger(getClass().getSimpleName());
        file = Component.class.getResource("/data.properties").getFile();
        if (properties == null) LOG.error("cold not load properties");
        if (properties.isEmpty()) LOG.error("properties is empty");

    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperties(String key,String value){
        LOG.debug("key = [" + key + "], value = [" + value + "]");
        properties.setProperty(key,value);
        try {
            properties.store(new FileOutputStream(file), "");
        } catch (IOException e) {
            LOG.error("Problem while saving Configuration", e);
        }
    }
}
