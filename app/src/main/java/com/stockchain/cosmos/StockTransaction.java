package com.stockchain.cosmos;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class StockTransaction {
    private Context ctx;
    private String blockchainPath;
    private String homeDir;

    public StockTransaction(Context ctx) {
        this.ctx = ctx;
        this.blockchainPath = ctx.getApplicationInfo().nativeLibraryDir + "/blockchaind.so";
        this.homeDir = ctx.getFilesDir().getAbsolutePath() + "/.blockchaind";
    }

    public void createStockTransaction(String username, String code, int count) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(this.blockchainPath, "tx", "blockchain", "create-stock-transaction", code, String.valueOf(count), "--from", username, "--keyring-backend", "test", "--home", homeDir, "--chain-id", "stock-chain", "--gas=auto", "-y");
        Process process = builder.start();
        BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = stdOut.readLine();
        if (line == null) {
            throw new IOException("dosen't exists");
        }
    }

    public void deleteStockTransaction(String username, String code, int count)throws IOException{
        ProcessBuilder builder = new ProcessBuilder(this.blockchainPath, "tx", "blockchain", "delete-stock-transaction", code, String.valueOf(count), "--from", username, "--keyring-backend", "test", "--home", homeDir, "--chain-id", "stock-chain", "-y");
        Process process = builder.start();
        BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = stdOut.readLine();
        if (line == null) {
            throw new IOException("dosen't exists");
        }
    }

    private ArrayList<StockTransactionInform> getStockTransaction(String address) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(this.blockchainPath, "query", "blockchain", "show-stock-transaction", address, "--home", homeDir);
        Process process = builder.start();

        BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = stdOut.readLine();
        if (line == null) {
            throw new IOException("dosen't exists");
        }else {
            stdOut.readLine(); stdOut.readLine();

            ArrayList<StockTransactionInform> StockTransactionInformList = new ArrayList<StockTransactionInform>();
            while ((line = stdOut.readLine()) != null) {
                String[] line_split = line.split(" ");
                String code = line_split[line_split.length - 1].replace("\"", "");

                line = stdOut.readLine();
                line_split = line.split(" ");
                int count = (int) Double.parseDouble(line_split[line_split.length - 1]);

                line = stdOut.readLine();
                line_split = line.split(" ");
                int puerchase_amount = (int) Double.parseDouble(line_split[line_split.length - 1]);

                StockTransactionInform StockTransaction = new StockTransactionInform(code, count, puerchase_amount);
                StockTransactionInformList.add(StockTransaction);
            }

            return StockTransactionInformList;
        }
    }

    public ArrayList<StockTransactionInform> getStockTransactionInform(String address) throws IOException {
        StockData stockData = new StockData(this.ctx);
        ArrayList<StockTransactionInform> StockTransactionInformList = this.getStockTransaction(address);

        for(int i=0;i<StockTransactionInformList.size();i++){
            StockTransactionInform stockTransactionInform = StockTransactionInformList.get(i);
            StockDataInform stockDatainform = stockData.getStockData(stockTransactionInform.code);

            stockTransactionInform.name = stockDatainform.name;
            stockTransactionInform.currentAmount = stockDatainform.amount * stockTransactionInform.count;
            stockTransactionInform.earningPrice = (((double)stockTransactionInform.currentAmount / stockTransactionInform.purchaseAmount) - 1) * 100;
        }
        return StockTransactionInformList;
    }

    private int getCurrentStockTransactionTotalAmount(String address) throws IOException{
        ArrayList<StockTransactionInform> stockTransactionInformList = this.getStockTransactionInform(address);
        int amount = 0;
        for(StockTransactionInform h : stockTransactionInformList){
            amount += h.currentAmount;
        }
        return amount;
    }

    private long getBalance(String address) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(this.blockchainPath, "query", "bank", "balances", address, "--home", homeDir);
        Process process = builder.start();

        BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = stdOut.readLine();
        if (line == null) {
            throw new IOException("dosen't exists");
        }

        line = stdOut.readLine();
        String[] line_split = line.split(" ");
        String amount = line_split[line_split.length - 1].replace("\"", "");
        return Long.parseLong(amount);
    }

    public StockBankInform getStockBankeInform(String address) throws IOException {
        long balances = this.getBalance(address);
        long currentStockTotalAmount = this.getCurrentStockTransactionTotalAmount(address);
        long currentTotalAmount = balances + currentStockTotalAmount;
        double earning_rate = (((double) currentTotalAmount / 1000000) - 1) * 100;

        StockBankInform stockBankInform = new StockBankInform(currentTotalAmount, balances, currentStockTotalAmount, earning_rate);
        return stockBankInform;
    }

    public ArrayList<StockTransactionRecordInform> getStockTransactionRecord(String address) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(this.blockchainPath, "query", "blockchain", "show-stock-transaction-record", address, "--home", homeDir);
        Process process = builder.start();
        BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line = stdOut.readLine();
        if (line == null) {
            throw new IOException("dosen't exists");
        }else {
            stdOut.readLine(); stdOut.readLine();

            ArrayList<StockTransactionRecordInform> stockTransactionRecordInformList = new ArrayList<StockTransactionRecordInform>();
            while ((line = stdOut.readLine()) != null) {
                String[] line_split = line.split(" ");
                long amount = Long.parseLong(line_split[line_split.length - 1]);

                line = stdOut.readLine();
                line_split = line.split(" ");
                String code = line_split[line_split.length - 1].replace("\"", "");

                line = stdOut.readLine();
                line_split = line.split(" ");
                int count = (int) Double.parseDouble(line_split[line_split.length - 1]);

                line = stdOut.readLine();
                line_split = line.split(" ");
                String date = line_split[line_split.length - 1].replace("\"", "");

                line = stdOut.readLine();
                line_split = line.split(" ");
                String recordType = line_split[line_split.length - 1].replace("\"", "");

                StockTransactionRecordInform stockTransactionRecordInform = new StockTransactionRecordInform(code, amount, count, date, recordType);
                stockTransactionRecordInformList.add(stockTransactionRecordInform);
            }

            return stockTransactionRecordInformList;
        }
    }
}