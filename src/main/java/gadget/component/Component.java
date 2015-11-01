package gadget.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Dustin on 01.09.2015.
 */
public class Component {

    protected final Logger LOG;
    private final String file;
    private Properties properties = new Properties();

    public Component() {
        LOG = LogManager.getLogger(getClass().getSimpleName());
        file = Component.class.getResource("/data.properties").getFile();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            LOG.error("Problem while Loading Configuration", e);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperties(String key,String value){
        properties.setProperty(key,value);
        try {
            properties.store(new FileOutputStream(file), "");
        } catch (IOException e) {
            LOG.error("Problem while saving Configuration", e);
        }
    }
}
