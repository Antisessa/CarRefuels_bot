package ru.antisessa.models;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "temporary_car")
public class TemporaryCar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "consumption")
    private double lastConsumption;

    @Column(name = "odometer")
    private int odometer;

    @Column(name = "gas_tank_volume")
    private int gasTankVolume;

    @OneToOne()
    @JoinColumn(name = "owner_id", referencedColumnName = "telegram_user_id")
    private AppUser appUser;
}
