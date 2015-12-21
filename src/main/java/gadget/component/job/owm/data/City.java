package gadget.component.job.owm.data;

/**
 * Created by Dustin on 03.10.2015.
 */
public class City {
    private long _id;
    private String name;
    private String country;
    private Coordinate coord;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o.toString().equalsIgnoreCase(this.name)) return true;
        if (o == null || getClass() != o.getClass()) return false;

        City city = (City) o;

        return name.equals(city.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
