package ru.antisessa.DTO;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import ru.antisessa.models.Car;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public enum RefuelDTO {
    ;

    private interface id {
        @NotNull
        int getId();
    }

    private interface volume {
        @NotNull(message = "Значение заправленного объема не может быть пустым")
        @DecimalMax(value = "99.99", message = "Значение заправленного объема должно быть меньше 100 литров")
        @DecimalMin(value = "1.00", message = "Значение заправленного объема должно быть больше 1 литра")
        double getVolume();
    }

    private interface cost{
        @NotNull
        double getCost();
    }

    private interface odometerRecord {
        @Min(value = 0, message = "Показание одометра должно быть положительным")
        int getOdometerRecord();
    }

    private interface previousOdometerRecord {
        @Min(value = 0, message = "Показание одометра должно быть положительным")
        int getPreviousOdometerRecord();
    }

    private interface dateTime {
        LocalDateTime getDateTime();
    }

    private interface calculatedConsumption {
        @NotNull(message = "Значение среднего расхода топлива не может быть пустым")
        @DecimalMax(value = "99.99", message = "Значение должно быть меньше 100 литров")
        @DecimalMin(value = "1.00", message = "Значение должно быть больше 1 литра")
        double getCalculatedConsumption();
    }

    private interface previousConsumption {
        @NotNull(message = "Значение среднего расхода топлива не может быть пустым")
        @DecimalMax(value = "99.99", message = "Значение должно быть меньше 100 литров")
        @DecimalMin(value = "1.00", message = "Значение должно быть больше 1 литра")
        double getPreviousConsumption();
    }

    private interface fullTankRefuel{
        @NotNull(message = "Укажите, заправка была до полного бака или нет")
        boolean isFullTankRefuel();
    }

    private interface car{
        Car getCar();
    }

    private interface carName{
        @NotNull
        String getCarName();
    }

    public enum Request {
        ;

        @Getter @Setter
        public static class UpdateRefuel implements
                volume, cost, odometerRecord, fullTankRefuel, carName{
            double volume;
            double cost;
            int odometerRecord;
            boolean fullTankRefuel;
            String carName;
        }

        @Getter @Setter
        public static class CreateRefuel extends UpdateRefuel{
        }
    }

    public enum Response {
        ;

        public static class T{}
        @Getter @Setter
        public static class DeleteLastRefuel extends T implements carName{
            String carName;
        }

        @Getter @Setter
        public static class GetRefuel extends DeleteLastRefuel implements
                id, volume, cost, dateTime, calculatedConsumption{

            int id;

            @JsonDeserialize(using = LocalDateTimeDeserializer.class)
            @JsonSerialize(using = LocalDateTimeSerializer.class)
            LocalDateTime dateTime;

            double volume;
            double cost;
            double calculatedConsumption;

            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder("\nrefuel{\n");
                sb.append("id=").append(id).append(",\n");
                sb.append("dateTime=").append(dateTime).append(",\n");
                sb.append("volume=").append(volume).append(",\n");
                sb.append("cost=").append(cost).append(",\n");
                sb.append("calculatedConsumption=").append(calculatedConsumption);
                sb.append('}');
                return sb.toString();
            }
        }

        @Getter @Setter
        public static class GetRefuelFullInfo extends GetRefuel implements
                previousOdometerRecord, odometerRecord{

            int previousOdometerRecord;
            int odometerRecord;
        }
    }
}
