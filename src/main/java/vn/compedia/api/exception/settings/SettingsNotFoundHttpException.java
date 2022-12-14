package vn.compedia.api.exception.settings;

import org.springframework.http.HttpStatus;
import vn.compedia.api.exception.HttpException;

public class SettingsNotFoundHttpException extends HttpException {

    private static final long serialVersionUID = -5258959358527382145L;

    public SettingsNotFoundHttpException(String message, HttpStatus status) {
        super(message, status);
    }
}
