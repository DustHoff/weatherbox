package gadget.component.owm.data;

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dustin on 31.10.2015.
 */
public class CityParser {

    public List<City> parse(InputStream in) throws IOException {
        ArrayList<City> cities = new ArrayList<City>();
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        reader.setLenient(true);
        while (reader.hasNext()) {
            try {
                cities.add(readCity(reader));
            } catch (IllegalStateException e) {
                break;
            }
        }
        reader.close();
        return cities;
    }

    private City readCity(JsonReader reader) throws IOException {
        City city = new City();
        reader.beginObject();
        while (reader.hasNext()) {
            String field = reader.nextName();
            if (field.equalsIgnoreCase("_id")) city.set_id(reader.nextLong());
            if (field.equalsIgnoreCase("name")) city.setName(reader.nextString());
            if (field.equalsIgnoreCase("country")) city.setCountry(reader.nextString());
            if (field.equalsIgnoreCase("coord")) city.setCoord(readCoordinate(reader));
        }
        reader.endObject();
        return city;
    }

    private Coordinate readCoordinate(JsonReader reader) throws IOException {
        Coordinate coordinate = new Coordinate();
        reader.beginObject();
        while (reader.hasNext()) {
            String field = reader.nextName();
            if (field.equalsIgnoreCase("lat")) coordinate.setLat(reader.nextDouble());
            if (field.equalsIgnoreCase("lon")) coordinate.setLon(reader.nextDouble());
        }
        reader.endObject();
        return coordinate;
    }
}
