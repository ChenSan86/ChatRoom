package com.chensan.common;

import java.io.Serializable;

public enum MessageType implements Serializable {
    LOGIN_SUCCESS,
    LOGIN_FAILURE,
    COMM_MESSAGE,
    ALL_MESSAGE,
    GET_USER,
    RET_USER,
    CLIENT_EXIT,
    COMM_FILE,

    AGREE_FILE,
    REFUSE_FILE,
    FILE_ENQUIRE,
    FILE_RESULT,
    REGISTER_SUCCESS,
    REGISTER_FAILURE,

    SERVER_CLOSE,
    FORCED_EXIT,

}
