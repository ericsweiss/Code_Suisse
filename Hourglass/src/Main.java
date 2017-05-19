/*
  Irrelevant Development - 05/18/2017
  Code Suisse Hackathon 2017
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.jfree.ui.RefineryUtilities;

/**
 * Main class
 * Read in file and parse data
 * Analyze data and plot results
 * This is the driver program that can call other parts of the project
 */
public class Main {

    // INSTANCE VARIABLES

    private static HashMap<String, Company> companyHashMap = new HashMap<>();

    // METHODS

    // Add feature to be able to read in files in a certain day range
    /**************************************************
     * Reads in from all files and stores data in the various structures
     *************************************************/
    public static int readFile(String filePath){

        try{
            List<File> files = Files.walk(Paths.get(filePath)).filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());
            System.out.println("----------BEGIN READING FILES----------");
            for(File file : files){
                System.out.println(file.getName());
                Scanner scan = new Scanner(file);

                scan.nextLine();
                while(scan.hasNext()){
                    String line = scan.nextLine();
                    String[] transaction = line.split(",");

                    if(transaction.length != 5 || transaction[0].startsWith("#")) continue; /* Ignore comments (starting with #) */

                    Transaction t = new Transaction(transaction);

                    if(!companyHashMap.containsKey(t.getSymbol())) companyHashMap.put(t.getSymbol(), new Company(t.getSymbol())); /* If this is the first time seeing a ticker, make a new Company */

                    companyHashMap.get(t.getSymbol()).insert(t);
                }

                scan.close();
            }
        } catch (FileNotFoundException e){
            System.err.println("File not found: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("----------END READING FILES----------");


        return 0;
    }

    /**************************************************
     * Accessor for the hash map that stores all of the companies
     * @return global HashMap of companies
     *************************************************/
    public static HashMap<String, Company> getCompanyHashMap() {
        return companyHashMap;
    }

    /**************************************************
     * Main Method
     * @param args command line arguments
     *************************************************/
    public static void main(String[] args){
        readFile("../Tick_Data_Dump");

        Scanner userInput = new Scanner(System.in);
        String userIn;
        boolean quit = false;

        while(!quit) {
            System.out.println("Welcome to Project Hourglass!\nOptions include:\n1) Price movement over a given period\n" +
                    "2) Price Statistics\n3) Classify Securities into Sectors\n4) Plot Price Movement for a Security in a Sector" +
                    "\n5) Predict Security Prices");
            System.out.print("\nEnter 1-5: ");
            userIn = userInput.next();

            String symbol;
            String start;
            String end;
            switch (userIn) {
                case "1":
                    System.out.print("\nEnter a symbol: ");
                    symbol = userInput.next();
                    System.out.print("\nEnter start time YYYYMMDDHHMMSS.000000: ");
                    start = userInput.next();
                    System.out.print("\nEnter end time YYYYMMDDHHMMSS.000000: ");
                    end = userInput.next();
                    Statistics.priceMovement(symbol, new Date(start), new Date(end));
                    break;
                case "2":
                    System.out.print("\nEnter a symbol: ");
                    symbol = userInput.next();
                    System.out.print("\nEnter start time YYYYMMDDHHMMSS.000000: ");
                    start = userInput.next();
                    System.out.print("\nEnter end time YYYYMMDDHHMMSS.000000: ");
                    end = userInput.next();
                    Statistics.getPriceStats(symbol, new Date(start), new Date(end));
                    break;
                case "3":
                    Statistics.makeSects();
                    break;
                case "4":
                    System.out.print("\nEnter a symbol: ");
                    symbol = userInput.next();
                    System.out.print("\nEnter start time YYYYMMDDHHMMSS.000000: ");
                    start = userInput.next();
                    System.out.print("\nEnter end time YYYYMMDDHHMMSS.000000: ");
                    end = userInput.next();
                    String[] symbols = {symbol};
                    XYLineChart chart = new XYLineChart("Time-Based Analytics", "Time-Based Analytics", symbols, new Date(start), new Date(end));
                    chart.pack();
                    RefineryUtilities.centerFrameOnScreen(chart);
                    chart.setVisible(true);
                    break;
                case "5":
                    System.out.print("\nEnter a symbol: ");
                    symbol = userInput.next();
                    System.out.print("\nEnter start time YYYYMMDDHHMMSS.000000: ");
                    start = userInput.next();
                    System.out.print("\nEnter end time YYYYMMDDHHMMSS.000000: ");
                    end = userInput.next();
                    System.out.print("\nEnter prediction time YYYYMMDDHHMMSS.000000: ");
                    String predict = userInput.next();
                    Statistics.predictPrice(symbol, new Date(start), new Date(end), new Date(predict));
                    break;
                default:
                    System.out.println("Invalid Option!");

            }


            System.out.println("Enter \'q\' to quit - otherwise, anything else to continue: ");
            userIn = userInput.next();
            if(userIn.equals("q")) quit = true;
        }

    }


}
