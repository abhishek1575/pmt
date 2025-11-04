package com.project.pmt.enums;

import lombok.Getter;

@Getter
public enum IssueStatus {
    TODO("To Do"),
    IN_PROGRESS("In Progress"),
    IN_REVIEW("In Review"),
    DONE("Done"),
    BLOCKED("Blocked");

    private final String displayName;
    IssueStatus(String displayName) {
        this.displayName = displayName;
    }

}
