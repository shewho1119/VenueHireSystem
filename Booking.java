package nz.ac.auckland.se281;


public class Booking {
  private String bookingReference;
  private String bookingNameVenue;
  private String venueCodeInput;
  private String requestedDate;
  private String email;
  private String attendees;

  public Booking(
      String referenceCode,
      String bookingNameVenue,
      String code,
      String date,
      String email,
      String attendees) {
    this.bookingReference = referenceCode;
    this.bookingNameVenue = bookingNameVenue;
    this.venueCodeInput = code;
    this.requestedDate = date;
    this.email = email;
    this.attendees = attendees;
  }

  // Getter methods for booking entities
  public String getBookingReference() {
    return bookingReference;
  }

  public String getBookingNameVenue() {
    return bookingNameVenue;
  }

  public String getVenueCodeInput() {
    return venueCodeInput;
  }

  public String getRequestedDate() {
    return requestedDate;
  }

  public String getEmail() {
    return email;
  }

  public String getAttendees() {
    return attendees;
  }
}
