package ru.antisessa.models;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@NoArgsConstructor
@Entity
@Table(name = "car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotNull(message = "Идентификатор машины не должен быть пустым")
    @Column(name = "name")
    private String name;

    @NotNull(message = "Значение среднего расхода топлива не может быть пустым")
    @DecimalMax(value = "99.99", message = "Проверьте данные, значение расхода должно быть меньше 100 литров")
    @DecimalMin(value = "1.00", message = "Проверьте данные, значение расхода должно быть больше 1 литра")
    @Column(name = "consumption")
    private double lastConsumption;

    @Min(value = 0, message = "Показание одометра должно быть положительным")
    @Column(name = "odometer")
    private int odometer;

    @NotNull(message = "Объем бака должен быть указан")
    @Min(value = 0, message = "Объем бака положительная величина")
    @Column(name = "gas_tank_volume")
    private int gasTankVolume;

    @OneToMany(mappedBy = "car")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "jackson_id")
    private List<Refuel> refuels;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Car{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", lastConsumption=").append(lastConsumption);
        sb.append(", odometer=").append(odometer);
        sb.append(", GasTankVolume=").append(gasTankVolume);
        sb.append(", refuels=").append(refuels);
        sb.append('}');
        return sb.toString();
    }
}
