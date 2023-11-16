package ru.antisessa.service.enums;

public enum ServiceCommand {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start"),
    FIND_ONE_CAR("/find_one_car"),
    FIND_ONE_CAR_FULL_INFO("/find_one_car_full_info"),
    FIND_ONE_REFUEL("/find_one_refuel"),
    FIND_ONE_REFUEL_FULL_INFO("/find_one_refuel_full_info");

    private final String value;

    ServiceCommand(String value) {
        this.value = value;
    }

    // Метод берет строку и сравнивает ее с каждым существующим enum,
    // возвращает совпадение или null
    public static ServiceCommand fromValue(String v){
        for (ServiceCommand c : ServiceCommand.values()) {
            if (c.value.equals(v))
                return c;
        }
        return null;
    }
}
