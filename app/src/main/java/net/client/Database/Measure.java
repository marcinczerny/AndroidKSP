package net.client.Database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity(nameInDb = "measurement")
public class Measure{

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "date")
    private Long date;

    @Property(nameInDb = "humidity")
    private Float humidity;

    @Property(nameInDb = "pressure")
    private Float pressure;

    @Property(nameInDb = "temperature")
    private Float temperature;

    @Generated(hash = 388811765)
    public Measure(Long id, Long date, Float humidity, Float pressure, Float temperature) {
        this.id = id;
        this.date = date;
        this.humidity = humidity;
        this.pressure = pressure;
        this.temperature = temperature;
    }

    @Generated(hash = 1840334633)
    public Measure() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Float getHumidity() {
        return humidity;
    }

    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }

    public Float getPressure() {
        return pressure;
    }

    public void setPressure(Float pressure) {
        this.pressure = pressure;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }
}