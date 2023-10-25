package ru.antisessa.service.enums;

public enum ServiceCommand {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

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
