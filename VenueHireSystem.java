package nz.ac.auckland.se281;

import java.util.ArrayList;
import nz.ac.auckland.se281.Types.CateringType;
import nz.ac.auckland.se281.Types.FloralType;

public class VenueHireSystem {

  private ArrayList<Venue> venueList = new ArrayList<Venue>();

  public VenueHireSystem() {}

  public void printVenues() {
    // No venues in the system
    if (venueList.isEmpty()) {
      MessageCli.NO_VENUES.printMessage();
      return;
    }

    // one venue in the system
    if (venueList.size() == 1) {
      MessageCli.NUMBER_VENUES.printMessage("is", "one", "");
      Venue venueOne = venueList.get(0);
      MessageCli.VENUE_ENTRY.printMessage(
          venueOne.getVenueName(),
          venueOne.getVenueCode(),
          venueOne.getCapacity(),
          venueOne.getHireFee(),
          nextAvailableDate(venueOne.getVenueCode()));
      return;
    }

    // string array to store the number of venues
    String[] numberOfVenues = {"two", "three", "four", "five", "six", "seven", "eight", "nine"};

    // more than 1 venue and less than 10 venues in the system
    if (venueList.size() > 1 && venueList.size() < 10) {
      MessageCli.NUMBER_VENUES.printMessage("are", numberOfVenues[venueList.size() - 2], "s");
      for (int i = 0; i < venueList.size(); i++) {
        Venue venue = venueList.get(i);

        MessageCli.VENUE_ENTRY.printMessage(
            venue.getVenueName(),
            venue.getVenueCode(),
            venue.getCapacity(),
            venue.getHireFee(),
            nextAvailableDate(venue.getVenueCode()));
      }
      return;
    }

    // 10 or more venues in the system
    if (venueList.size() >= 10) {
      MessageCli.NUMBER_VENUES.printMessage("are", Integer.toString(venueList.size()), "s");
      for (int i = 0; i < venueList.size(); i++) {
        Venue venue = venueList.get(i);
        MessageCli.VENUE_ENTRY.printMessage(
            venue.getVenueName(),
            venue.getVenueCode(),
            venue.getCapacity(),
            venue.getHireFee(),
            nextAvailableDate(venue.getVenueCode()));
      }
      return;
    }
  }

  public String nextAvailableDate(String venueCode) {

    // If the system date has not been set yet
    if (systemDate == null) {
      return "TODO";
    }

    // Get the next available date for the venue
    String nextDate = systemDate;

    // System Date split
    String[] nextDateParts = nextDate.split("/");
    int nextDay = Integer.parseInt(nextDateParts[0]);
    int nextMonth = Integer.parseInt(nextDateParts[1]);
    int nextYear = Integer.parseInt(nextDateParts[2]);

    for (int i = 0; i < bookingList.size(); i++) {
      Booking booking = bookingList.get(i);
      if (booking.getVenueCodeInput().equals(venueCode)) {

        // if there is a booking made on the system date, increment the next available date
        if (nextDate.equals(booking.getRequestedDate())) {
          nextDay++;
          // update the next available date by concatenating the new incremented next Day
          nextDate = String.format("%02d/%02d/%04d", nextDay, nextMonth, nextYear);
        }
      }
    }

    return nextDate;
  }

  public void createVenue(
      String venueName, String venueCode, String capacityInput, String hireFeeInput) {

    // if venue name empty, print error message
    if (venueName.trim().isEmpty()) {
      MessageCli.VENUE_NOT_CREATED_EMPTY_NAME.printMessage();
      return;
    }

    // invalid input for capacity
    try {
      int capacity = Integer.parseInt(capacityInput);

      // When the capacity is negative or not over 0, print the error message
      if (capacity <= 0) {
        MessageCli.VENUE_NOT_CREATED_INVALID_NUMBER.printMessage("capacity", " positive");
        return;
      }
      // When capacity is not a number, print the error message
    } catch (Exception e) {
      MessageCli.VENUE_NOT_CREATED_INVALID_NUMBER.printMessage("capacity", "");
      return;
    }

    // invalid input for hirefee
    try {
      int hireFee = Integer.parseInt(hireFeeInput);

      // When the hirefee is negative or not over 0, print the error message
      if (hireFee <= 0) {
        MessageCli.VENUE_NOT_CREATED_INVALID_NUMBER.printMessage("hire fee", " positive");
        return;
      }
      // When hirefee is not a number, print the error message
    } catch (Exception e) {
      MessageCli.VENUE_NOT_CREATED_INVALID_NUMBER.printMessage("hire fee", "");
      return;
    }

    // venue code already exists
    for (int i = 0; i < venueList.size(); i++) {
      if (venueList.get(i).getVenueCode().equals(venueCode)) {
        MessageCli.VENUE_NOT_CREATED_CODE_EXISTS.printMessage(
            venueCode, venueList.get(i).getVenueName());
        return;
      }
    }

    // Add the venue informations to the venueList
    Venue venue = new Venue(venueName, venueCode, capacityInput, hireFeeInput);
    venueList.add(venue);
    MessageCli.VENUE_SUCCESSFULLY_CREATED.printMessage(venueName, venueCode);
  }

  private String systemDate;

  public void setSystemDate(String dateInput) {
    this.systemDate = dateInput;
    MessageCli.DATE_SET.printMessage(systemDate);
  }

  public void printSystemDate() {
    // If the system date is not set, print the message
    if (systemDate == null) {
      MessageCli.CURRENT_DATE.printMessage("not set");
      return;
    } else {
      // print the system date
      MessageCli.CURRENT_DATE.printMessage(systemDate);
    }
  }

  private ArrayList<Booking> bookingList = new ArrayList<Booking>();

  public void makeBooking(String[] options) {

    // If the system date has not been set yet
    if (systemDate == null) {
      MessageCli.BOOKING_NOT_MADE_DATE_NOT_SET.printMessage();
      return;
    }

    // If there is no venue created in the system
    if (venueList.isEmpty()) {
      MessageCli.BOOKING_NOT_MADE_NO_VENUES.printMessage();
      return;
    }

    String referenceCode = BookingReferenceGenerator.generateBookingReference();
    String code = options[0];
    String date = options[1];
    String email = options[2];
    String attendees = options[3];

    // if the Booking Date is past the System Date
    if (isBookingDatePast(date)) {
      MessageCli.BOOKING_NOT_MADE_PAST_DATE.printMessage(date, systemDate);
      return;
    }

    String bookingNameVenue = null;
    // check if the venue requested actually exists
    boolean venueCodeExist = false;

    // Check if the venue code exists - go through the venueList
    for (int i = 0; i < venueList.size(); i++) {
      Venue venue = venueList.get(i);
      if (venue.getVenueCode().equals(code)) {
        venueCodeExist = true;
        bookingNameVenue = venue.getVenueName();

        // Check if the number of attendees is <25% or >100% of the venue capacity
        int venueCapacity = Integer.parseInt(venue.getCapacity());
        int attendeesInt = Integer.parseInt(attendees);
        if (attendeesInt < (venueCapacity / 4)) {
          attendeesInt = venueCapacity / 4;
          MessageCli.BOOKING_ATTENDEES_ADJUSTED.printMessage(
              attendees, Integer.toString(attendeesInt), venue.getCapacity());
          attendees = Integer.toString(attendeesInt);
        } else if (attendeesInt > venueCapacity) {
          attendeesInt = venueCapacity;
          MessageCli.BOOKING_ATTENDEES_ADJUSTED.printMessage(
              attendees, Integer.toString(attendeesInt), venue.getCapacity());
          attendees = Integer.toString(attendeesInt);
        }

        break;
      }
    }

    // If the corresponding venue code not found in the system, print error message
    if (!venueCodeExist) {
      MessageCli.BOOKING_NOT_MADE_VENUE_NOT_FOUND.printMessage(code);
      return;
    }

    // Check for double booking
    for (int i = 0; i < bookingList.size(); i++) {
      Booking booking = bookingList.get(i);
      if (booking.getVenueCodeInput().equals(code) && booking.getRequestedDate().equals(date)) {
        MessageCli.BOOKING_NOT_MADE_VENUE_ALREADY_BOOKED.printMessage(bookingNameVenue, date);
        return;
      }
    }

    // Record the booking information into the list - Create a new booking object
    Booking booking = new Booking(referenceCode, bookingNameVenue, code, date, email, attendees);
    bookingList.add(booking);
    MessageCli.MAKE_BOOKING_SUCCESSFUL.printMessage(
        referenceCode, bookingNameVenue, date, attendees);
  }

  private boolean isBookingDatePast(String bookingDate) {
    // Booking Date split
    String[] bookingDateParts = bookingDate.split("/");
    int bookingDay = Integer.parseInt(bookingDateParts[0]);
    int bookingMonth = Integer.parseInt(bookingDateParts[1]);
    int bookingYear = Integer.parseInt(bookingDateParts[2]);

    // System Date split
    String[] systemDateParts = systemDate.split("/");
    int systemDay = Integer.parseInt(systemDateParts[0]);
    int systemMonth = Integer.parseInt(systemDateParts[1]);
    int systemYear = Integer.parseInt(systemDateParts[2]);

    // Compare the booking date with the system date
    if (bookingYear < systemYear) {
      return true;
    } else if (bookingYear == systemYear && bookingMonth < systemMonth) {
      return true;
    } else if (bookingYear == systemYear && bookingMonth == systemMonth && bookingDay < systemDay) {
      return true;
    }

    return false;
  }

  public void printBookings(String venueCode) {

    Boolean venueExists = false;

    // Check if venue exists
    for (int i = 0; i < venueList.size(); i++) {
      if (venueList.get(i).getVenueCode().equals(venueCode)) {
        venueExists = true;

        // Check if booking exists
        Boolean bookingExists = false;
        for (int a = 0; a < bookingList.size(); a++) {
          if (bookingList.get(a).getVenueCodeInput().equals(venueCode)) {
            bookingExists = true;
          }
        }

        // Print the header that includes venue name
        if (bookingExists) {
          MessageCli.PRINT_BOOKINGS_HEADER.printMessage(venueList.get(i).getVenueName());

          // Print each bookings with its reference code and requested date
          for (int b = 0; b < bookingList.size(); b++) {
            if (bookingList.get(b).getVenueCodeInput().equals(venueCode)) {
              MessageCli.PRINT_BOOKINGS_ENTRY.printMessage(
                  bookingList.get(b).getBookingReference(), bookingList.get(b).getRequestedDate());
            }
          }
        }

        // If there is no booking for the venue, print the message
        if (!bookingExists) {
          MessageCli.PRINT_BOOKINGS_HEADER.printMessage(venueList.get(i).getVenueName());
          MessageCli.PRINT_BOOKINGS_NONE.printMessage(venueList.get(i).getVenueName());
        }

        break;
      }
    }

    // If there are no venue in the system or could not be found, print the error message
    if (!venueExists) {
      MessageCli.PRINT_BOOKINGS_VENUE_NOT_FOUND.printMessage(venueCode);
    }
  }

  // Catering arraylist to store catering services added to booking
  private ArrayList<Catering> cateringList = new ArrayList<Catering>();

  public void addCateringService(String bookingReference, CateringType cateringType) {

    boolean bookingExists = false;

    // Check if the booking exists
    for (int i = 0; i < bookingList.size(); i++) {
      if (bookingList.get(i).getBookingReference().equals(bookingReference)) {
        bookingExists = true;

        // Create a new catering object and add catering object to the cateringList
        Catering cateringService = new Catering(bookingReference, cateringType);

        cateringList.add(cateringService);

        MessageCli.ADD_SERVICE_SUCCESSFUL.printMessage(
            "Catering (" + cateringType.getName() + ")", bookingReference);
      }
    }

    // If the booking does not exist, print the error message
    if (!bookingExists) {
      MessageCli.SERVICE_NOT_ADDED_BOOKING_NOT_FOUND.printMessage("Catering", bookingReference);
    }
  }

  // Music arraylist to store music services added to booking
  private ArrayList<Music> musicList = new ArrayList<Music>();

  public void addServiceMusic(String bookingReference) {
    boolean bookingExists = false;

    // Check if the booking exists
    for (int i = 0; i < bookingList.size(); i++) {
      if (bookingList.get(i).getBookingReference().equals(bookingReference)) {
        bookingExists = true;

        // Create a new music object and add music object to the musicList
        Music musicService = new Music(bookingReference);

        musicList.add(musicService);

        MessageCli.ADD_SERVICE_SUCCESSFUL.printMessage("Music", bookingReference);
      }
    }

    // If the booking does not exist, print the error message
    if (!bookingExists) {
      MessageCli.SERVICE_NOT_ADDED_BOOKING_NOT_FOUND.printMessage("Music", bookingReference);
    }
  }

  // Floral arraylist to store floral services added to booking
  private ArrayList<FloralSystem> floralList = new ArrayList<FloralSystem>();

  public void addServiceFloral(String bookingReference, FloralType floralType) {
    boolean bookingExists = false;

    // Check if the booking exists
    for (int i = 0; i < bookingList.size(); i++) {
      if (bookingList.get(i).getBookingReference().equals(bookingReference)) {
        bookingExists = true;

        // Create a new floral object and add floral object to the floralList
        FloralSystem floralService = new FloralSystem(bookingReference, floralType);

        floralList.add(floralService);

        MessageCli.ADD_SERVICE_SUCCESSFUL.printMessage(
            "Floral (" + floralType.getName() + ")", bookingReference);
      }
    }

    // If the booking does not exist, print the error message
    if (!bookingExists) {
      MessageCli.SERVICE_NOT_ADDED_BOOKING_NOT_FOUND.printMessage("Floral", bookingReference);
    }
  }

  public void viewInvoice(String bookingReference) {

    // Check if the booking exists
    boolean bookingExist = false;

    // Check if the booking exists by looping through the bookingList
    for (int i = 0; i < bookingList.size(); i++) {
      if (bookingList.get(i).getBookingReference().equals(bookingReference)) {
        // if booking exists, boolean 'bookingExist' is set to true
        bookingExist = true;
        Booking booking = bookingList.get(i);

        // find the venue that the booking was made
        for (int j = 0; j < venueList.size(); j++) {
          if (venueList.get(j).getVenueCode().equals(booking.getVenueCodeInput())) {
            Venue venue = venueList.get(j);

            // Top Half of the invoice content is printed
            MessageCli.INVOICE_CONTENT_TOP_HALF.printMessage(
                bookingReference,
                booking.getEmail(),
                systemDate,
                booking.getRequestedDate(),
                booking.getAttendees(),
                booking.getBookingNameVenue());

            // invoice content for venue hire fee is printed
            MessageCli.INVOICE_CONTENT_VENUE_FEE.printMessage(venue.getHireFee());

            // initialise the number of attendees
            int numPeople = Integer.parseInt(booking.getAttendees());

            // initialise the total cost
            int totalAmount = Integer.parseInt(venue.getHireFee());

            // check if catering service is added and print invoice for catering services
            for (int a = 0; a < cateringList.size(); a++) {
              if (cateringList.get(a).getBookingReference().equals(bookingReference)) {
                MessageCli.INVOICE_CONTENT_CATERING_ENTRY.printMessage(
                    cateringList.get(a).getserviceTypeName(),
                    Integer.toString(cateringList.get(a).getCost() * numPeople));

                // Add the cost of catering service to the total amount
                totalAmount = totalAmount + cateringList.get(a).getCost() * numPeople;
                break;
              }
            }

            // check if music service is added and print invoice for music services
            for (int b = 0; b < musicList.size(); b++) {
              if (musicList.get(b).getBookingReference().equals(bookingReference)) {
                MessageCli.INVOICE_CONTENT_MUSIC_ENTRY.printMessage(
                    Integer.toString(musicList.get(b).getCost()));

                // Add the cost of music service to the total amount
                totalAmount = totalAmount + musicList.get(b).getCost();
                break;
              }
            }

            // check if floral service is added and print invoice for floral services
            for (int c = 0; c < floralList.size(); c++) {
              if (floralList.get(c).getBookingReference().equals(bookingReference)) {
                MessageCli.INVOICE_CONTENT_FLORAL_ENTRY.printMessage(
                    floralList.get(c).getserviceTypeName(),
                    Integer.toString(floralList.get(c).getCost()));

                // Add the cost of floral service to the total amount
                totalAmount = totalAmount + floralList.get(c).getCost();
                break;
              }
            }

            // Bottom Half of the invoice content is printed
            MessageCli.INVOICE_CONTENT_BOTTOM_HALF.printMessage(Integer.toString(totalAmount));
            break;
          }
        }
        break;
      }
    }

    // If the booking does not exist, print the error message
    if (!bookingExist) {
      MessageCli.VIEW_INVOICE_BOOKING_NOT_FOUND.printMessage(bookingReference);
    }
  }
}
