package ru.tickets.trainschedulebot.botApi.handlers.state;

/**
 * Possible bot states
 *
 * @author Elgun Dilanchiev
 */

public enum BotState {
    TRAINS_SEARCH,
    TRAINS_SEARCH_STARTED,
    TRAINS_SEARCH_FINISH,
    ASK_STATION_DEPART,
    ASK_STATION_ARRIVAL,
    ASK_DATE_DEPART,
    DATE_DEPART_RECEIVED,
    SHOW_MAIN_MENU,
    TRAIN_INFO_RESPONSE_AWAITING,
    SHOW_SUBSCRIPTIONS,
    SHOW_STATIONS_BOOK_MENU,
    STATIONS_SEARCH,
    ASK_STATION_NAME_PART,
    STATION_NAME_PART_RECEIVED,
    SHOW_HELP_MENU;
}