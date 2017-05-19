/*
  Irrelevant Development - 05/18/2017
  Code Suisse Hackathon 2017
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

/**
 * Company Class
 * This represents and individual security
 * Each Company has a symbol (ticker) and sector (assigned when classified)
 * There is a hash map used to lookup transactions by date quickly
 * There is a list of transactions for the given security
 */
public class Company {

    //INSTANCE VARIABLES

    private String symbol;
    private int sector;
    private HashMap<Double, Integer> dateIndex = new HashMap<>();
    private ArrayList<Transaction> transactionList = new ArrayList<>();

    /**************************************************
     * Constructor for Company setting the ticker
     * @param symbol ticker symbol used to uniquely identify a company
     *************************************************/
    public Company(String symbol){
        this.symbol = symbol;
    }

    // METHODS

    // Accessors

    /**************************************************
     * Accessor for symbol
     * @return the company's unique ticker identifier
     *************************************************/
    public String getSymbol(){
        return symbol;
    }

    /**************************************************
     * Accessor for sector
     * @return the numerical identifier for the sector of a given company based on makeSectors()
     *************************************************/
    public int getSector(){
        return sector;
    }

    /**************************************************
     * Accessor for dateIndex
     * @return the table used to quickly lookup the index of a transaction based on timestamp
     *************************************************/
    public HashMap<Double, Integer> getDateIndex(){
        return dateIndex;
    }

    /**************************************************
     * Accessor for transactionList
     * @return the list of transactions for the company
     *************************************************/
    public ArrayList<Transaction> getTransactionList() {
        return transactionList;
    }

    // Mutators

    /**************************************************
     * Mutator for sector
     * @param sector the numerical identifier for the sector of a given company based on makeSectors()
     *************************************************/
    public void setSector(int sector){
        this.sector = sector;
    }

    /**************************************************
     * Inserts a transaction into the company's list of transactions and hashes the time for fast lookup
     * @param t the transaction to insert
     *************************************************/
    public void insert(Transaction t){
        transactionList.add(t);
        dateIndex.put(t.getTimestamp().getDate(), transactionList.size() - 1);
    }

    /**************************************************
     * Orders transactions chronologically and rehashes the dates based on the new indices
     *************************************************/
    public void resetTransactionData(){ /* Since transactions are in order for each security, this should not be necessary */
        Collections.sort(transactionList);
        for(int i = 0; i < transactionList.size(); i++){
            dateIndex.put(transactionList.get(i).getTimestamp().getDate(), i);
        }
    }

    /**************************************************
     * Returns a transaction performed at the given date
     * If there is no transaction from that date, return the closest earlier transaction
     * @param d the date of the transaction to lookup
     * @return a transaction from the company's list
     *************************************************/
    public Transaction lookupTransaction(Date d){
        if(dateIndex.get(d.getDate()) != null){
            return transactionList.get(dateIndex.get(d.getDate()));
        } else {
            int offset = transactionList.size()/2;
            int index = transactionList.size()/2;
            if(d.compareTo(transactionList.get(0).getTimestamp()) < 0) return null;
            while(true){ /* Binary Search to find the closest earlier transaction */
                if((transactionList.size() - 1) == index){ /* Index is end of the list */
                    return transactionList.get(index);
                } else if((d.compareTo(transactionList.get(index).getTimestamp()) == 1) && (d.compareTo(transactionList.get(index + 1).getTimestamp()) == -1)){ /* Previous transaction is earlier, next is later */
                    return transactionList.get(index);
                } else {
                    offset /= 2;
                    if(offset== 0) offset++;
                    index = d.compareTo(transactionList.get(index).getTimestamp()) == -1 ? index - offset : index + offset;
                }
            }
        }
    }
}
