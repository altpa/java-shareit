package ru.practicum.shareit.booking.model;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.Instant;

@Data
@Entity
@Table(name = "bookings")
@DynamicUpdate
@DynamicInsert
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "start_time")
    private Instant start;

    @Column(name = "end_time")
    private Instant end;

    @OneToOne
    @JoinColumn(name = "item")
    @ToString.Exclude
    private Item item;

    @ManyToOne
    @JoinColumn(name = "booker")
    @ToString.Exclude
    private User booker;

    private BookingStatus status;
}