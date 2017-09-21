package DataAccess.Services;

import DataAccess.Entities.TemperatureSensor;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

public class TemperatureSensorRepository {
    public void CreateReading(Sql2o sql2o, String value){
        Connection conn = sql2o.open();

        conn.createQuery("insert into indoor_temp(value) VALUES (:value)")
                .addParameter("value", Float.valueOf(value))
                .executeUpdate();
    }

    public List<TemperatureSensor> GetReadings(Sql2o sql2o){
        String sql = "SELECT * FROM indoor_temp WHERE id%4 = 0 ORDER BY timestamp DESC LIMIT 24";

        try(Connection conn = sql2o.open()){
            return conn.createQuery(sql).executeAndFetch(TemperatureSensor.class);
        }
    }
}
