package com.trimeshkit.state;

/**
 * Created by wahmed on 07/12/2017.
 */

public class ApplicationState {

    public enum ApplicationStateEnum {
        GENERAL(0), SKETCH(1);

        private final int value;

        private ApplicationStateEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private static ApplicationStateEnum mApplicationState;

    public static void setApplicationState(ApplicationStateEnum _applicationState)
    {
        mApplicationState = _applicationState;
    }

    public static ApplicationStateEnum getApplicationState()
    {
        return mApplicationState;
    }
}
