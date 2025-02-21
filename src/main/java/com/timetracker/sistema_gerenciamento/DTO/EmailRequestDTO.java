package com.timetracker.sistema_gerenciamento.DTO;

public class EmailRequestDTO {
    private String from;
    private String subject;
    private String message;

    // Getters e setters
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}