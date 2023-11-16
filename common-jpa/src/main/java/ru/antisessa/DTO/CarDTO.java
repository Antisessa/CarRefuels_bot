package ru.antisessa.DTO;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/*
Используется паттерн DTO interface,
В главном Enum представлены интерфейсы, названные одноименно с полями класса Car
При создании нужного класса для DTO (пр. Response GetCar) мы указываем поля внутри этого класса
и реализуем нужные интерфейсы из главного enum, реализация происходит с помощью аннотации @Getter из Lombok
Валидация происходит на уровне интерфейсов, если в Request передается JSON ез нужного поля то Hibernate Validator его не пропустит
 */

// Enum для всех CarDTO
public enum CarDTO {
    ;

    private interface id {
        @NotNull(message = "ID машины не должен быть пустым 'DTO message'")
        int getId();
    }
    private interface name {
        @NotNull(message = "Идентификатор машины не должен быть пустым 'DTO message'")
        String getName();
    }
    private interface lastConsumption {
        double getLastConsumption();
    }
    private interface odometer {
        @Min(value = 1, message = "Показание одометра должно быть положительным 'DTO message'")
        int getOdometer();
    }
    private interface gasTankVolume {
        @NotNull(message = "Объем бака должен быть указан 'DTO message'")
        @Min(value = 1, message = "Объем бака положительная величина 'DTO message'")
        int getGasTankVolume();
    }

    private interface refuels {
        @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "jackson_id")
        List<RefuelDTO.Response.GetRefuelFullInfo> getRefuels();
    }

    private interface countRefuels {
        int getCountRefuels();
    }

    private interface update {
        Update getUpdate();
    }

    //Enums Requests для CarController
    public enum Request {
        ; // Пустой enum

        @Getter @Setter
        public static class DeleteCar implements name {
            String name;
        }

        @Getter @Setter
        public static class UpdateCar extends DeleteCar implements id{
            int id;
        }

        @Getter @Setter
        public static class CreateCar extends DeleteCar implements odometer, gasTankVolume, lastConsumption {
            String name;
            int odometer;
            int gasTankVolume;
            double lastConsumption;
        }
    }

    public enum Response {
        ; // Пустой enum

        public static class WildCard{}

        @Getter @Setter
        public static class GetCar extends WildCard implements name, odometer, gasTankVolume, countRefuels, update{
            String name;
            int odometer;
            int gasTankVolume;
            int countRefuels;
            Update update;

            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder("GetCar{");
                sb.append("name='").append(name).append('\'');
                sb.append(", odometer=").append(odometer);
                sb.append(", gasTankVolume=").append(gasTankVolume);
                sb.append(", countRefuels=").append(countRefuels);
                sb.append('}');
                return sb.toString();
            }
        }

        @Getter @Setter
        public static class GetCarFullInfo extends GetCar implements id, refuels{

            int id;

            @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "jackson_id")
            List<RefuelDTO.Response.GetRefuelFullInfo> refuels;

            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder(name + "info{\n");
                sb.append("id=").append(id).append(",\n");
                sb.append("name='").append(name).append('\'').append(",\n");
                sb.append("odometer=").append(odometer).append(",\n");
                sb.append("gasTankVolume=").append(gasTankVolume).append(",\n");
                sb.append("countRefuels=").append(countRefuels).append(",\n");
                sb.append("refuels=").append(refuels).append(",\n");
                sb.append('}');
                return sb.toString();
            }
        }
    }
}
