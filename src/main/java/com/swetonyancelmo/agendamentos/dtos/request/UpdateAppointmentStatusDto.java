package com.swetonyancelmo.agendamentos.dtos.request;

import com.swetonyancelmo.agendamentos.models.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateAppointmentStatusDto {

    @NotNull
    private AppointmentStatus status;

    private String rejectionReason;
}
