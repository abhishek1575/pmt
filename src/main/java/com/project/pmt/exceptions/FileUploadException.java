package com.project.pmt.exceptions;

public class FileUploadException extends RuntimeException {

    private String filename;

    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, String filename) {
        super(message + ": " + filename);
        this.filename = filename;
    }

    public FileUploadException(String message, String filename, Throwable cause) {
        super(message + ": " + filename, cause);
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
