package ru.practicum.shareit.user.exception;

public class ResponseBody {
        private final String error;

        public ResponseBody(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
}
