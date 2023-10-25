package ru.antisessa.models;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.antisessa.enums.UserState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Long telegramUserId;

    @CreationTimestamp // Автоматически генерируемое значение при первом создании сущности
    private LocalDateTime firstLoginDate;

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private boolean isActive;

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    @Enumerated(EnumType.STRING) // Говорим Spring что хотим записывать в БД текстовое значение Enum
    private UserState state;
}
