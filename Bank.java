import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Bank {
    private File customersFile = new File("customers.txt");
    private File accountsFile = new File("accounts.txt");
    private File transactionsFile = new File("transactions.txt");
    private List<Customer> customers;
    private List<Account> accounts;
    private List<Transaction> transactions;
    private int nextCustomerId = 1;
    private int nextAccountId = 1;
    private int nextTransactionId = 1;
    private final Scanner scanner;
    private boolean isBankingOfficial = false;

    public Bank() {
        customers = new ArrayList<>();
        accounts = new ArrayList<>();
        transactions = new ArrayList<>();
        scanner = new Scanner(System.in);
        loadCustomers();
        loadAccounts();
        loadTransactions();
    }

    private void loadCustomers() {
        try (Scanner scanner = new Scanner(customersFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                customers.add(new Customer(id, name));
                nextCustomerId = Math.max(nextCustomerId, id + 1);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Customers file not found.");
        }
    }

    private void loadAccounts() {
        try (Scanner scanner = new Scanner(accountsFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                int customerId = Integer.parseInt(parts[1]);
                double balance = Double.parseDouble(parts[2]);
                accounts.add(new Account(id, customerId, balance));
                nextAccountId = Math.max(nextAccountId, id + 1);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Accounts file not found.");
        }
    }

    private void loadTransactions() {
        try (Scanner scanner = new Scanner(transactionsFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                int accountId = Integer.parseInt(parts[1]);
                double amount = Double.parseDouble(parts[2]);
                String type = parts[3];
                transactions.add(new Transaction(id, accountId, amount, type));
                nextTransactionId = Math.max(nextTransactionId, id + 1);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Transactions file not found.");
        }
    }

    private void saveCustomers() {
        try (PrintWriter writer = new PrintWriter(customersFile)) {
            for (Customer customer : customers) {
                writer.println(customer.getId() + "," + customer.getName());
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error saving customers: " + e.getMessage());
        }
    }

    private void saveAccounts() {
        try (PrintWriter writer = new PrintWriter(accountsFile)) {
            for (Account account : accounts) {
                writer.println(account.getId() + "," + account.getCustomerId() + "," + account.getBalance());
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
        }
    }

    private void saveTransactions() {
        try (PrintWriter writer = new PrintWriter(transactionsFile)) {
            for (Transaction transaction : transactions) {
                writer.println(transaction.getId() + "," + transaction.getAccountId() + "," + transaction.getAmount() + "," + transaction.getType());
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
        }
    }

    public void addCustomer(String name) {
        customers.add(new Customer(nextCustomerId, name));
        saveCustomers();
        System.out.println("Customer added successfully with ID: " + nextCustomerId);
        nextCustomerId++;
    }

    public void openAccount(int customerId, double initialBalance) {
        accounts.add(new Account(nextAccountId, customerId, initialBalance));
        saveAccounts();
        System.out.println("Account opened successfully with account number: " + nextAccountId);
        nextAccountId++;
    }

    public void deposit(int accountId, double amount) {
        for (Account account : accounts) {
            if (account.getId() == accountId) {
                account.deposit(amount);
                saveAccounts();
                transactions.add(new Transaction(nextTransactionId, accountId, amount, "Deposit"));
                saveTransactions();
                System.out.println("Deposited " + amount + " into account " + accountId);
                nextTransactionId++;
                return;
            }
        }
        System.out.println("Account not found.");
    }

    public void withdraw(int accountId, double amount) {
        for (Account account : accounts) {
            if (account.getId() == accountId) {
                if (account.withdraw(amount)) {
                    saveAccounts();
                    transactions.add(new Transaction(nextTransactionId, accountId, amount, "Withdrawal"));
                    saveTransactions();
                    System.out.println("Withdrawn " + amount + " from account " + accountId);
                    nextTransactionId++;
                } else {
                    System.out.println("Insufficient balance.");
                }
                return;
            }
        }
        System.out.println("Account not found.");
    }

    public void transfer(int fromAccountId, int toAccountId, double amount) {
        Account fromAccount = null, toAccount = null;
        for (Account account : accounts) {
            if (account.getId() == fromAccountId) {
                fromAccount = account;
            }
            if (account.getId() == toAccountId) {
                toAccount = account;
            }
        }
        if (fromAccount != null && toAccount != null) {
            if (fromAccount.withdraw(amount)) {
                toAccount.deposit(amount);
                saveAccounts();
                transactions.add(new Transaction(nextTransactionId, fromAccountId, amount, "Transfer to account " + toAccountId));
                saveTransactions();
                nextTransactionId++;
                transactions.add(new Transaction(nextTransactionId, toAccountId, amount, "Transfer from account " + fromAccountId));
                saveTransactions();
                nextTransactionId++;
                System.out.println("Transferred " + amount + " from account " + fromAccountId + " to account " + toAccountId);
            } else {
                System.out.println("Insufficient balance.");
            }
        } else {
            System.out.println("One or both accounts not found.");
        }
    }

    public void viewBalance(int accountId) {
        for (Account account : accounts) {
            if (account.getId() == accountId) {
                System.out.println("Account balance: " + account.getBalance());
                return;
            }
        }
        System.out.println("Account not found.");
    }

    public void displayCustomers() {
        if (customers.isEmpty()) {
            System.out.println("No customers.");
        } else {
            System.out.println("Customers:");
            for (Customer customer : customers) {
                System.out.println(customer.getId() + " - " + customer.getName());
            }
        }
    }

    public void displayAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts.");
        } else {
            System.out.println("Accounts:");
            for (Account account : accounts) {
                System.out.println(account.getId() + " - Customer ID: " + account.getCustomerId() + ", Balance: " + account.getBalance());
            }
        }
    }

    public void displayTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions.");
        } else {
            System.out.println("Transactions:");
            for (Transaction transaction : transactions) {
                System.out.println(transaction.getId() + " - Account ID: " + transaction.getAccountId() + ", Amount: " + transaction.getAmount() + ", Type: " + transaction.getType());
            }
        }
    }

    public static void main(String[] args) {
        Bank bank = new Bank();
        bank.displayLoginMenu();
    }

    private void displayLoginMenu() {
        System.out.println("Welcome to the Bank!");
        System.out.println("1. Customer Login");
        System.out.println("2. Banking Official Login");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        switch (choice) {
            case 1:
                customerLogin();
                break;
            case 2:
                bankingOfficialLogin();
                break;
            case 0:
                System.out.println("Exiting the banking application.");
                System.exit(0);
            default:
                System.out.println("Invalid choice. Please enter a number from the menu.");
                displayLoginMenu();
        }
    }

    private void customerLogin() {
        System.out.print("Enter your customer ID: ");
        int customerId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        boolean validCustomer = customers.stream().anyMatch(customer -> customer.getId() == customerId);
        if (validCustomer) {
            System.out.println("Customer login successful!");
            displayCustomerMenu();
        } else {
            System.out.println("Invalid customer ID.");
            displayLoginMenu();
        }
    }

    private void bankingOfficialLogin() {
        // Simple hardcoded login for demonstration
        System.out.print("Enter password for banking official: ");
        String password = scanner.nextLine();
        if ("password".equals(password)) {
            System.out.println("Banking official login successful!");
            isBankingOfficial = true;
            displayBankingOfficialMenu();
        } else {
            System.out.println("Incorrect password.");
            displayLoginMenu();
        }
    }

    private void displayCustomerMenu() {
        boolean quit = false;
        while (!quit) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. View Balance");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            switch (choice) {
                case 1:
                    System.out.print("Enter initial balance: ");
                    double initialBalance = scanner.nextDouble();
                    openAccount(customers.get(0).getId(), initialBalance); // For simplicity, we assume the first customer is logged in
                    break;
                case 2:
                    displayAccounts();
                    System.out.print("Enter account ID: ");
                    int depositAccountId = scanner.nextInt();
                    System.out.print("Enter amount to deposit: ");
                    double depositAmount = scanner.nextDouble();
                    deposit(depositAccountId, depositAmount);
                    break;
                case 3:
                    displayAccounts();
                    System.out.print("Enter account ID: ");
                    int withdrawAccountId = scanner.nextInt();
                    System.out.print("Enter amount to withdraw: ");
                    double withdrawAmount = scanner.nextDouble();
                    withdraw(withdrawAccountId, withdrawAmount);
                    break;
                case 4:
                    displayAccounts();
                    System.out.print("Enter account ID to transfer from: ");
                    int fromAccountId = scanner.nextInt();
                    System.out.print("Enter account ID to transfer to: ");
                    int toAccountId = scanner.nextInt();
                    System.out.print("Enter amount to transfer: ");
                    double transferAmount = scanner.nextDouble();
                    transfer(fromAccountId, toAccountId, transferAmount);
                    break;
                case 5:
                    displayAccounts();
                    System.out.print("Enter account ID: ");
                    int viewBalanceAccountId = scanner.nextInt();
                    viewBalance(viewBalanceAccountId);
                    break;
                case 0:
                    quit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from the menu.");
            }
        }
        System.out.println("Logging out...");
        displayLoginMenu();
    }

    private void displayBankingOfficialMenu() {
        boolean quit = false;
        while (!quit) {
            System.out.println("\n--- Banking Official Menu ---");
            System.out.println("1. Add Customer");
            System.out.println("2. Display Customers");
            System.out.println("3. Display Accounts");
            System.out.println("4. Display Transactions");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            switch (choice) {
                case 1:
                    System.out.print("Enter customer name: ");
                    String name = scanner.nextLine();
                    addCustomer(name);
                    break;
                case 2:
                    displayCustomers();
                    break;
                case 3:
                    displayAccounts();
                    break;
                case 4:
                    displayTransactions();
                    break;
                case 0:
                    quit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from the menu.");
            }
        }
        System.out.println("Logging out...");
        displayLoginMenu();
    }
}


class Customer {
    private int id;
    private String name;

    public Customer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

class Account {
    private int id;
    private int customerId;
    private double balance;

    public Account(int id, int customerId, double balance) {
        this.id = id;
        this.customerId = customerId;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public boolean withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }
}

class Transaction {
    private int id;
    private int accountId;
    private double amount;
    private String type;

    public Transaction(int id, int accountId, double amount, String type) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public int getAccountId() {
        return accountId;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }
}
