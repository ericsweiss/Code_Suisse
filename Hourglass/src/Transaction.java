/*
  Irrelevant Development - 05/18/2017
  Code Suisse Hackathon 2017
 */

import java.util.InputMismatchException;

/**
 * Transaction Class
 * Stores the data for an individual transaction
 * This includes the symbol, timestamp, trade price, and volume
 */
public class Transaction implements Comparable<Transaction>{

    // INSTANCE VARIABLES

    private String symbol;
    private Date timestamp;
    private double price;
    private double volume;


    // METHODS

    /**************************************************
     * Constructor
     * Return a new transaction with all of the instance values set
     *************************************************/
    public Transaction(String[] input) {

        try {
            if (input.length != 5) { /* Ensure that the data for the transaction has the correct number of components */
                throw new InputMismatchException();
            }

           symbol = input[0];

            timestamp = new Date(input[1]);

            try { /* Ensure price and volume are formatted properly when reading in from the file */
                price = Double.parseDouble(input[3]);
                volume = Double.parseDouble(input[4]);
            } catch (NumberFormatException e) {
                System.err.println("Price or Volume is not a valid number!");
                price = 0;
                volume = 0;
            }

        } catch (InputMismatchException e) { /* If transaction data is invalid (missing/too long), print out an error message, but continue */
            System.err.println("Incorrect data format! Transactions have 5 components, not " + input.length);
            price = 0;
            volume = 0;
        }
    }

    // Accessors

    /**************************************************
     * Accessor for the ticker
     * @return the unique ticker identifier
     *************************************************/
    public String getSymbol() {
        return symbol;
    }

    /**************************************************
     * Accessor for the date and time
     * @return the timestamp from the transaction
     *************************************************/
    public Date getTimestamp() {
        return timestamp;
    }

    /**************************************************
     * Accessor for the price
     * @return the price from the transaction
     *************************************************/
    public double getPrice() {
        return price;
    }

    /**************************************************
     * Accessor for the volume
     * @return the number of shares traded in the transaction
     *************************************************/
    public double getVolume() {
        return volume;
    }

    /**************************************************
     * Compares transactions for chronological ordering
     * @param t the transaction to compare to
     * @return 0 if they are equal, -1 if performed earlier than the argument transaction, 1 if performed later
     *************************************************/
    @Override
    public int compareTo(Transaction t) {
        return timestamp.compareTo(t.getTimestamp());
    }
}
