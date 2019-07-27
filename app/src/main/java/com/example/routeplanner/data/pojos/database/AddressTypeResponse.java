package com.example.routeplanner.data.pojos.database;

public class AddressTypeResponse {

    private boolean error;
    private boolean message;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isMessage() {
        return message;
    }

    public void setMessage(boolean message) {
        this.message = message;
    }
}
