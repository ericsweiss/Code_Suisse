/*
  Irrelevant Development - 05/18/2017
  Code Suisse Hackathon 2017
 */

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * Statistics Class
 * Contains methods to calculate statistics related
 * to security transactions over time. Also includes
 * methods to split securities into sectors based on the correlation
 * of their prices over time.
 */
public class Statistics {

    /**************************************************
     * Computes the price movement of a security given start and end times
     * @param symbol the unique ticker identifier of a security
     * @param start the starting timestamp
     * @param end the ending timestamp
     * @return price movement of company
     *************************************************/
    public static double priceMovement(String symbol, Date start, Date end) {

        System.out.println("----------BEGIN Price Movement----------");

        long programStart = System.currentTimeMillis(); /* Time the program */

        Company x = Main.getCompanyHashMap().get(symbol);

        Transaction a = x.lookupTransaction(start);
        Transaction b = x.lookupTransaction(end);

        double startPrice = a.getPrice();
        double endPrice = b.getPrice();

        double movement = ((endPrice - startPrice) / startPrice) * 100;

        long programEnd = System.currentTimeMillis(); /* Stop timer */

        System.out.println("Price Movement: " + movement);
        System.out.println("Total Time: " + (programEnd - programStart) + " Millisec");
        System.out.println("----------END Price Movement----------");

        return movement;
    }

    /**************************************************
     * Computes the highest price, lowest price, mean price, median price, and volatility given
     * start and end times
     * @param symbol the unique ticker identifier of a security
     * @param start the starting timestamp
     * @param end the ending timestamp
     * @return statistics analyzing company prices
     *************************************************/
    public static double[] getPriceStats(String symbol, Date start, Date end) {

        System.out.println("----------BEGIN Price Stats----------");

        long programStart = System.currentTimeMillis();

        Company company = Main.getCompanyHashMap().get(symbol);
        Transaction iterator = company.lookupTransaction(start);
        Transaction last = company.lookupTransaction(end);
        double[] stats = {Double.MIN_VALUE,Double.MAX_VALUE,0,0,0};
        ArrayList<Double> prices = new ArrayList<>();

        if(iterator != null) {
            int initialIndex = company.getDateIndex().get(iterator.getTimestamp().getDate());
            int index = initialIndex;
            while (iterator.getTimestamp().compareTo(last.getTimestamp()) <= 0) {
                if (iterator.getPrice() > stats[0]) stats[0] = iterator.getPrice();
                if(iterator.getPrice() < stats[1]) stats[1] = iterator.getPrice();
                stats[2] += iterator.getPrice();
                prices.add(iterator.getPrice());
                index++;
                iterator = company.getTransactionList().get(index);
            }

            stats[2] /= (double)(index - initialIndex);

            Collections.sort(prices);
            stats[3] = prices.get(prices.size() / 2);
        } else {
            stats[0] = 0;
            stats[1] = 0;
        }

        double[] compPrices = new double[prices.size()];
        for(int i = 0; i < prices.size(); i++) {
            compPrices[i] = prices.get(i);
        }

        double standDev = new StandardDeviation().evaluate(compPrices);
        stats[4] = standDev;

        long programEnd = System.currentTimeMillis();

        System.out.println("High Price: " + stats[0]);
        System.out.println("Low Price: " + stats[1]);
        System.out.println("Mean Price: " + stats[2]);
        System.out.println("Median Price: " + stats[3]);
        System.out.println("Volatility: " + stats[4]);
        System.out.println("Total Time: " + (programEnd - programStart) + " Millisec");

        System.out.println("----------END Price Stats----------");

        return stats;
    }

    /**************************************************
     * Classify securities into sectors
     * @return lists of securities grouped into sectors
     *************************************************/
    public static ArrayList<ArrayList<Company>> makeSects(){

        System.out.println("----------BEGIN Making Sectors----------");

        long programStart = System.currentTimeMillis();

        HashMap<String, HashMap<String, Double>> correlation = new HashMap<>();

        for(String s : Main.getCompanyHashMap().keySet()) { /* Chose to use O(n^3) algorithm instead of O(n^2) to improve accuracy - explained in design decisions  */
            Company companyA = Main.getCompanyHashMap().get(s);
            correlation.put(s, new HashMap<>());

            for(String t : Main.getCompanyHashMap().keySet()) {
                Company companyB = Main.getCompanyHashMap().get(t);
                correlation.get(s).put(companyB.getSymbol(), getCorrelation(companyA, companyB));
            }
        }

        ArrayList<ArrayList<Company>> sectors = new ArrayList<>();
        sectors.add(new ArrayList<>());

        int amountThrough = 0;
        for(String s : Main.getCompanyHashMap().keySet()) {
            Company c = Main.getCompanyHashMap().get(s);

            if(sectors.get(0).isEmpty()) sectors.get(0).add(c);
            else {
                int bestIndex = 0;
                int closest = 0;
                for(int i = 0; i < sectors.size(); i++) {
                    int avgCorr = 0;
                    int j;
                    for(j = 0; j < sectors.get(i).size(); j++) {
                        avgCorr += (correlation.get(s).get(sectors.get(i).get(j).getSymbol()) * 100);
                    }
                    avgCorr /= j;
                    if(Math.abs(avgCorr) > Math.abs(closest)) {
                        closest = avgCorr;
                        bestIndex = i;
                    }
                }


                if(((amountThrough <= Main.getCompanyHashMap().keySet().size() * .1) && Math.abs(closest) >= 90)
                        || ((amountThrough < Main.getCompanyHashMap().keySet().size() * .5) && Math.abs(closest) >= 70)
                        || ((amountThrough < Main.getCompanyHashMap().keySet().size() * .8) && Math.abs(closest) >= 40)
                        || ((amountThrough >= Main.getCompanyHashMap().keySet().size() * .8))) {
                    sectors.get(bestIndex).add(c);
                } else {
                    ArrayList<Company> temp = new ArrayList<>();
                    temp.add(c);
                    sectors.add(temp);
                }
                amountThrough++;
            }
        }

        long programEnd = System.currentTimeMillis(); /* Stop timer */

        for(int i = 0; i < sectors.size(); i++) {
            System.out.println(i + ": ");

            for(int j = 0; j < sectors.get(i).size(); j++) {
                Main.getCompanyHashMap().get(sectors.get(i).get(j).getSymbol()).setSector(i);
                System.out.println("   " + sectors.get(i).get(j).getSymbol());
            }
        }

        System.out.println("Total Time: " + (programEnd - programStart) + " Millisec");
        System.out.println("----------END Price Movement----------");

        return sectors;
    }

    /**************************************************
     * Return the correlation between two companies
     * @param companyA the first company
     * @param companyB the second company
     * @return the Pearsons correlation between the inputs
     *************************************************/
    public static double getCorrelation(Company companyA, Company companyB) {

        int minNumberTransactions = Math.min(companyA.getTransactionList().size(), companyB.getTransactionList().size());
        minNumberTransactions = Math.min(minNumberTransactions, 2000);

        double[] pricesA = new double[minNumberTransactions - 1];
        double[] pricesB = new double[minNumberTransactions - 1];

        double startA = companyA.getTransactionList().get(0).getTimestamp().getDate();
        double endA = companyA.getTransactionList().get(companyA.getTransactionList().size() - 1).getTimestamp().getDate();

        double startB = companyB.getTransactionList().get(0).getTimestamp().getDate();
        double endB = companyB.getTransactionList().get(companyB.getTransactionList().size() - 1).getTimestamp().getDate();

        double intervalA = (endA - startA) / minNumberTransactions;
        double intervalB = (endB - startB) / minNumberTransactions;

        for(int i = 0; i < minNumberTransactions - 1; i++) {
            DecimalFormat df = new DecimalFormat("#");
            df.setMaximumFractionDigits(6);
            pricesA[i] = ((companyA.lookupTransaction(new Date(df.format(startA + ((double)(i + 1) * intervalA)))).getPrice() - companyA.lookupTransaction(new Date(df.format(startA + ((double)i * intervalA)))).getPrice()) / companyA.lookupTransaction(new Date(df.format(startA + ((double)i * intervalA)))).getPrice()) * 100;
            pricesB[i] = ((companyB.lookupTransaction(new Date(df.format(startB + ((double)(i + 1) * intervalB)))).getPrice() - companyB.lookupTransaction(new Date(df.format(startB + ((double)i * intervalB)))).getPrice()) / companyB.lookupTransaction(new Date(df.format(startB + ((double)i * intervalB)))).getPrice()) * 100;
        }

        return new PearsonsCorrelation().correlation(pricesA, pricesB);
    }

    /**************************************************
     * Generates a regression line from the start and end dates and
     * predicts a price based on the regression model
     *
     * #NOTE: The TRAINING DATA given demonstrates ticks on a millsecond basis
     * Predictions are recommended to be consistent with the training data.
     *
     * @param symbol the unique ticker identifier
     * @param start the starting timestamp
     * @param end the ending timestamp
     * @param predict the date of the price to predict
     * @return the predicted price given date
     *************************************************/
    public static double predictPrice(String symbol, Date start, Date end, Date predict) {

        System.out.println("----------BEGIN Price Prediction----------");

        long programStart = System.currentTimeMillis();

        Company company = Main.getCompanyHashMap().get(symbol);

        Transaction iteratorA = company.lookupTransaction(start);
        Transaction lastA = company.lookupTransaction(end);

        SimpleRegression regression = new SimpleRegression();

        if(iteratorA != null) {
            int initialIndex = company.getDateIndex().get(iteratorA.getTimestamp().getDate());
            int index = initialIndex;
            while (iteratorA.getTimestamp().compareTo(lastA.getTimestamp()) <= 0) {
                regression.addData(iteratorA.getTimestamp().getDate(), iteratorA.getPrice());
                index++;
                iteratorA = company.getTransactionList().get(index);
            }
        }

        long programEnd = System.currentTimeMillis();
        System.out.println("y = " + regression.getSlope() + "x + " + regression.getIntercept());

        System.out.println("Total Time: " + (programEnd - programStart) + " Millisec");
        System.out.println("----------END Price Prediction----------");

        return regression.predict(predict.getDate());
    }
}
