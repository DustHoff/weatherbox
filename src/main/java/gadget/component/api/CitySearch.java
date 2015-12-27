package gadget.component.api;

import gadget.component.job.owm.OWM;
import gadget.component.job.owm.data.City;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dustin on 23.12.2015.
 */
public class CitySearch extends ApiComponent<String> {
    @Override
    public Object handleRequest(String request, String requestMethod) throws Exception {
        if (request == null) request = "";
        if (OWM.getInstance() == null) return Collections.emptyList();
        List<City> citylist = OWM.getInstance().getCities();
        List<City> result = new ArrayList<City>();
        if (request.length() > 4) {
            for (City city : citylist) {
                if (city.getName().matches(request + ".*")) result.add(city);
                if (result.size() > 10) break;
            }
        } else result = citylist.subList(0, 10);
        return result;
    }
}
