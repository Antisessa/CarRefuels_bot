package ru.antisessa;

public class RabbitQueue {
    public static final String TEXT_MESSAGE_UPDATE = "text_message_update";
    public static final String ANSWER_MESSAGE = "answer_message";
    public static final String ANSWER_MESSAGE_WITH_EXCEPTION = "answer_message_with_exception";
    public static final String FIND_ONE_CAR_REQUEST = "find_one_car_request";
    public static final String FIND_ONE_CAR_RESPONSE = "find_one_car_response";
    public static final String FIND_ONE_CAR_FULL_INFO_REQUEST = "find_one_car_full_info_request";
    public static final String FIND_ONE_CAR_FULL_INFO_RESPONSE = "find_one_car_full_info_response";
    public static final String CREATE_CAR_NAME_REQUEST = "create_car_name_request";

    public static final String SWITCH_STATE_TO_BASIC = "switch_state_to_basic";
    public static final String SWITCH_STATE_TO_FIND_ONE_CAR = "switch_state_to_find_one_car";
    public static final String SWITCH_STATE_TO_FIND_ONE_CAR_FULL_INFO = "switch_state_to_find_one_car_fill_info";
    public static final String SWITCH_STATE_TO_CREATING_CAR_NAME = "switch_state_to_creating_car_name";
    public static final String SWITCH_STATE_TO_CREATING_CAR_ODOMETER = "switch_state_to_creating_car_odometer";
    public static final String SWITCH_STATE_TO_CREATING_CAR_GAS_TANK_VOLUME = "switch_state_to_creating_car_gas_tank_volume";
    public static final String SWITCH_STATE_TO_CREATING_CAR_LAST_CONSUMPTION = "switch_state_to_creating_car_last_consumption";
}