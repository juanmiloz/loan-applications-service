package co.com.pragma.model.util;

import co.com.pragma.model.status.Status;

import java.util.UUID;

public class StatusCreator {

    public static Status createValidStatus() {
        return new Status(
                UUID.randomUUID(),
                "APPROVED",
                "The loan application has been approved"
        );
    }

    public static Status createWithBlankName() {
        return new Status(
                UUID.randomUUID(),
                "   ",
                "Status with blank name"
        );
    }

    public static Status createWithBlankDescription() {
        return new Status(
                UUID.randomUUID(),
                "PENDING",
                "   "
        );
    }

    public static Status createWithNullDescription() {
        return new Status(
                UUID.randomUUID(),
                "REJECTED",
                null
        );
    }

    public static Status createDuplicatedName() {
        return new Status(
                UUID.randomUUID(),
                "APPROVED",
                "Duplicate status"
        );
    }

}
