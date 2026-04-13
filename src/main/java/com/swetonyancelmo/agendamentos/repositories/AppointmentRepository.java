package com.swetonyancelmo.agendamentos.repositories;

import com.swetonyancelmo.agendamentos.models.Appointment;
import com.swetonyancelmo.agendamentos.models.Customer;
import com.swetonyancelmo.agendamentos.models.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByCustomer(Customer customer);

    List<Appointment> findByBusinessIdAndAppointmentDate(UUID businessId, LocalDate date);

    List<Appointment> findByBusinessIdAndAppointmentDateBetween(
            UUID businessId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Appointment> findByBusinessIdAndAppointmentDateBetweenAndStatus(
            UUID businessId,
            LocalDate startDate,
            LocalDate endDate,
            AppointmentStatus status
    );

    @Query("""
        SELECT a FROM Appointment a
        WHERE a.business.id = :businessId
        AND a.appointmentDate = :date
        AND a.startTime < :endTime
        AND a.endTime > :startTime
        AND a.status IN ('PENDING', 'CONFIRMED')
    """)
    List<Appointment> findConflictingAppointments(
            UUID businessId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    );

    @Query("""
        SELECT a FROM Appointment a
        WHERE a.status = 'COMPLETED'            \s
   \s""")
    List<Appointment> findAllCompletedAppointments();
}
