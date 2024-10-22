package com.egls.transactia;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AgeCheck {

    /**
     * Checks if the provided date string indicates that the user is at least 16 years old.
     *
     * @param dateString the date string in "dd/MM/yyyy" format
     * @return true if the user is at least 16 years old, false otherwise
     */
    public boolean isAtLeast16YearsOld(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            // Parse the date string to a Date object
            Date birthDate = sdf.parse(dateString);
            if (birthDate != null) {
                // Get today's date
                Calendar today = Calendar.getInstance();

                // Create a calendar object for the birth date
                Calendar birthCalendar = Calendar.getInstance();
                birthCalendar.setTime(birthDate);

                // Calculate age
                int age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);

                // Adjust age if the birthday hasn't occurred this year yet
                if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                    age--;
                }

                // Return true if age is 16 or more
                return age >= 16;
            }
        } catch (ParseException e) {
            // Handle the parsing error (optional: log it or show a message)
            e.printStackTrace();
        }

        // Return false if parsing fails or if birthDate is null
        return false;
    }
}
