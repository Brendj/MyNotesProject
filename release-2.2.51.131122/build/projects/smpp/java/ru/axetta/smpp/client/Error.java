/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.smpp.client;

interface Error {
    int ERRCODE_NOT_SET                    = 0;
    int ERRCODE_WRONG_STATE                = -1;
    int ERRCODE_UNKNOWN                    = -2;
    int ERRCODE_BIND_FAILED                = -3;
    int ERRCODE_RESPONSE_FAILED            = -4;
    int ERRCODE_UNKNOWN_SOURCE             = -5;
    int ERRCODE_EMPTY_PAYLOAD              = -6;
    int ERRCODE_SESSION_DESTROYED          = -7;
    int ERRCODE_PING_NOT_SENT              = -8;
    int ERRCODE_PING_TIMEOUT               = -9;
    int ERRCODE_TIMEOUT_SENDING_MESSAGE    = -10;
    int ERRCODE_BAD_MESSAGE                = -11;
    int ERRCODE_MESSAGE_QUEUE_FULL         = 0x14;
    int ERRCODE_THROTTLING_LIMIT_REACHED   = 0x58;
    int ERRCODE_SYSTEM_ERROR_LIMIT_REACHED = 0x8;
}
