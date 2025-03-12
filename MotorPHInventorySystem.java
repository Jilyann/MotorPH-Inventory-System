package com.mycompany.motorphinventorysystem;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MotorPHInventorySystem {

    static class StockNode {
        String dateEntered, stockLabel, brand, engineNumber, status;
        StockNode left, right;

        StockNode(String dateEntered, String stockLabel, String brand, String engineNumber, String status) {
            this.dateEntered = dateEntered;
            this.stockLabel = stockLabel;
            this.brand = brand;
            this.engineNumber = engineNumber;
            this.status = status;
            this.left = this.right = null;
        }
    }

    private StockNode root;

    public void readCSV(String filename) {
    File file = new File("C:\\Users\\Jilianne\\Documents\\NetBeansProjects\\ArraySum\\ArraySum\\MotorPHInventorySystem\\MotorPH_Inventory.csv");

    if (!file.exists()) {
        System.err.println("Error: CSV file not found at the specified path.");
        return;
    }

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        br.readLine(); // Skip header
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            if (values.length < 5) continue; // Skip invalid rows
            root = insert(root, values[0], values[1], values[2], values[3], values[4]);
            System.out.println("Loaded stock: " + values[3]);  // For confirmation
        }
        System.out.println("CSV file successfully loaded.");
    } catch (IOException e) {
        System.err.println("Error reading CSV file: " + e.getMessage());
    }
}

    public void writeCSV(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("Date Entered,Stock Label,Brand,Engine Number,Status\n");
            inOrderWrite(root, bw);
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    private void inOrderWrite(StockNode node, BufferedWriter bw) throws IOException {
        if (node != null) {
            inOrderWrite(node.left, bw);
            bw.write(node.dateEntered + "," + node.stockLabel + "," + node.brand + "," + node.engineNumber + "," + node.status + "\n");
            inOrderWrite(node.right, bw);
        }
    }

    public void addStock(String brand, String engineNumber, String date, String label, String status) {
        StockNode beforeInsert = search(root, engineNumber);  // Check before inserting

        root = insert(root, date, label, brand, engineNumber, status);

        if (beforeInsert == null) {
            System.out.println("Stock successfully added: " + engineNumber);
            writeCSV("C:\\Users\\Jilianne\\Documents\\NetBeansProjects\\ArraySum\\ArraySum\\MotorPHInventorySystem\\MotorPH_Inventory.csv");
        }
    }

    private StockNode insert(StockNode node, String dateEntered, String stockLabel, String brand, String engineNumber, String status) {
        if (node == null) return new StockNode(dateEntered, stockLabel, brand, engineNumber, status);
        int compare = engineNumber.compareTo(node.engineNumber);
        if (compare < 0) node.left = insert(node.left, dateEntered, stockLabel, brand, engineNumber, status);
        else if (compare > 0) node.right = insert(node.right, dateEntered, stockLabel, brand, engineNumber, status);
        else System.out.println("Error: Engine Number already exists.");
        return node;
    }

    public void deleteStock(String engineNumber, Scanner scanner) {
        StockNode foundStock = search(root, engineNumber);

        if (foundStock == null) {
            System.out.println("Error: Stock not found.");
            return;
        }

        if (foundStock.status.equals("Old") || foundStock.status.equals("Sold")) {
            System.out.println("Error: Cannot delete 'Old' or 'Sold' stock.");
        } else {
            // Asking for confirmation before deletion
            System.out.print("Are you sure you want to delete this stock? (yes/no): ");
            String confirmation = scanner.nextLine();
        
            if (confirmation.equalsIgnoreCase("yes")) {
                root = delete(root, engineNumber); // Proceed with deletion
                System.out.println("Stock successfully deleted: " + engineNumber);
                writeCSV("C:\\Users\\Jilianne\\Documents\\NetBeansProjects\\ArraySum\\ArraySum\\MotorPHInventorySystem\\MotorPH_Inventory.csv");
            } else {
                System.out.println("Deletion canceled.");
            }
        }
    }

    private StockNode delete(StockNode node, String engineNumber) {
        if (node == null) {
            System.out.println("Error: Stock not found.");
            return null;
        }
        int compare = engineNumber.compareTo(node.engineNumber);
        if (compare < 0) node.left = delete(node.left, engineNumber);
        else if (compare > 0) node.right = delete(node.right, engineNumber);
        else {
            if (node.stockLabel.equals("Old") || node.status.equals("Sold")) {
                System.out.println("Error: Cannot delete 'Old' or 'Sold' stock.");
                return node;
            }
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            StockNode successor = findMin(node.right);
            node.engineNumber = successor.engineNumber;
            node.status = successor.status;
            node.right = delete(node.right, successor.engineNumber);
        }
        return node;
    }

    private StockNode findMin(StockNode node) {
        while (node.left != null) node = node.left;
        return node;
    }

    public void searchStock(String engineNumber) {
        StockNode result = search(root, engineNumber);
        if (result != null) {
            System.out.println("Stock Found: " + result.dateEntered + ", " + result.stockLabel + ", " + result.brand + ", " + result.engineNumber + ", " + result.status);
        } else {
            System.out.println("No items found.");
        }
    }

    private StockNode search(StockNode node, String engineNumber) {
        if (node == null || node.engineNumber.equals(engineNumber)) return node;
        int compare = engineNumber.compareTo(node.engineNumber);
        return (compare < 0) ? search(node.left, engineNumber) : search(node.right, engineNumber);
    }

    public void sortStockByBrand() {
        if (root == null) {
            System.out.println("Inventory is empty. No sorting required.");
            return;
        }

        List<StockNode> stockList = new ArrayList<>();
        inOrderAddToList(root, stockList);

        stockList.sort(Comparator.comparing(stock -> stock.brand));

        System.out.println("\nSorted Inventory by Brand:");
        for (StockNode stock : stockList) {
        System.out.println(stock.dateEntered + ", " + stock.stockLabel + ", " + stock.brand + ", " + stock.engineNumber + ", " + stock.status);
        }

        writeCSV("C:\\Users\\Jilianne\\Documents\\NetBeansProjects\\ArraySum\\ArraySum\\MotorPHInventorySystem\\MotorPH_Inventory.csv");
    }

    private void inOrderAddToList(StockNode node, List<StockNode> stockList) {
        if (node != null) {
            inOrderAddToList(node.left, stockList);
            stockList.add(node);
            inOrderAddToList(node.right, stockList);
        }
    }

    private void inOrderPrint(StockNode node) {
        if (node != null) {
            inOrderPrint(node.left);
            System.out.println(node.dateEntered + ", " + node.stockLabel + ", " + node.brand + ", " + node.engineNumber + ", " + node.status);
            inOrderPrint(node.right);
        }
    }

    public static void main(String[] args) {
        MotorPHInventorySystem inventory = new MotorPHInventorySystem();
        inventory.readCSV("MotorPH_Inventory.csv");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nMotorPH Stock Management System");
            System.out.println("1. Add Stock");
            System.out.println("2. Delete Stock");
            System.out.println("3. Search Stock");
            System.out.println("4. Sort Stock by Brand");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Brand: ");
                    String brand = scanner.nextLine();
                    System.out.print("Enter Engine Number: ");
                    String engineNumber = scanner.nextLine();
                    inventory.addStock(brand, engineNumber, LocalDate.now().format(DateTimeFormatter.ofPattern("M/d/yyyy")), "New", "On-hand");
                }
                case 2 -> {
                    System.out.print("Enter Engine Number to delete: ");
                    String deleteEngine = scanner.nextLine();
                    inventory.deleteStock(deleteEngine, scanner);  // ✅ Passes `scanner` for confirmation
                }
                case 3 -> {
                    System.out.print("Enter Engine Number to search: ");
                    String searchEngine = scanner.nextLine();
                    inventory.searchStock(searchEngine);
                }
                case 4 -> inventory.sortStockByBrand();
                case 5 -> {
                    System.out.println("Exiting system...");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
