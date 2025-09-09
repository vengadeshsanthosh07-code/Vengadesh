import java.util.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Base Plan class
class Plan {
    protected int planId;
    protected String name;
    protected double monthlyPrice;
    protected String features;
    protected int trialDays;

    public Plan(int planId, String name, double monthlyPrice, String features, int trialDays) {
        this.planId = planId;
        this.name = name;
        this.monthlyPrice = monthlyPrice;
        this.features = features;
        this.trialDays = trialDays;
    }

    // Getters and Setters
    public int getPlanId() { return planId; }
    public String getName() { return name; }
    public double getMonthlyPrice() { return monthlyPrice; }
    public String getFeatures() { return features; }
    public int getTrialDays() { return trialDays; }

    public void setName(String name) { this.name = name; }
    public void setMonthlyPrice(double price) { this.monthlyPrice = price; }
    public void setFeatures(String features) { this.features = features; }
    public void setTrialDays(int days) { this.trialDays = days; }

    // Compute amount (can be overridden)
    public double computeAmount(int daysUsed) {
        return monthlyPrice;
    }
}

// MonthlyPlan subclass
class MonthlyPlan extends Plan {
    public MonthlyPlan(int planId, String name, double monthlyPrice, String features, int trialDays) {
        super(planId, name, monthlyPrice, features, trialDays);
    }

    @Override
    public double computeAmount(int daysUsed) {
        // Apply proration
        return (monthlyPrice / 30) * daysUsed;
    }
}

// AnnualPlan subclass
class AnnualPlan extends Plan {
    public AnnualPlan(int planId, String name, double monthlyPrice, String features, int trialDays) {
        super(planId, name, monthlyPrice, features, trialDays);
    }

    @Override
    public double computeAmount(int daysUsed) {
        // Annual discount: pay for 12 months at 10% discount
        return monthlyPrice * 12 * 0.9;
    }
}

// Subscriber class
class Subscriber {
    private int id;
    private String name;
    private String email;
    private Plan currentPlan;
    private String status; // Active, Inactive

    public Subscriber(int id, String name, String email, Plan plan) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.currentPlan = plan;
        this.status = "Active";
    }

    // Getters & Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Plan getCurrentPlan() { return currentPlan; }
    public String getStatus() { return status; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setCurrentPlan(Plan plan) { this.currentPlan = plan; }
    public void setStatus(String status) { this.status = status; }

    // Change plan
    public void changePlan(Plan newPlan) {
        System.out.println(name + " changed plan from " + currentPlan.getName() + " to " + newPlan.getName());
        this.currentPlan = newPlan;
    }
}

// Invoice class
class Invoice {
    private static int counter = 1000;
    private int invoiceNo;
    private int subscriberId;
    private double amount;
    private LocalDate dueDate;
    private String state; // Paid, Unpaid, Overdue

    public Invoice(int subscriberId, double amount, LocalDate dueDate) {
        this.invoiceNo = counter++;
        this.subscriberId = subscriberId;
        this.amount = amount;
        this.dueDate = dueDate;
        this.state = "Unpaid";
    }

    // Getters & Setters
    public int getInvoiceNo() { return invoiceNo; }
    public int getSubscriberId() { return subscriberId; }
    public double getAmount() { return amount; }
    public LocalDate getDueDate() { return dueDate; }
    public String getState() { return state; }

    public void setAmount(double amount) { this.amount = amount; }
    public void setDueDate(LocalDate date) { this.dueDate = date; }

    // Record payment
    public void recordPayment() {
        this.state = "Paid";
        System.out.println("Invoice " + invoiceNo + " has been paid.");
    }

    // Check overdue
    public boolean isOverdue() {
        return state.equals("Unpaid") && dueDate.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return "Invoice#" + invoiceNo + " | SubscriberID: " + subscriberId + " | Amount: $" + amount + " | Due: " + dueDate + " | State: " + state;
    }
class BillingService {
    private List<Invoice> invoices = new ArrayList<>();
    public Invoice generateInvoice(Subscriber subscriber) {
        double amount = subscriber.getCurrentPlan().computeAmount(subscriber.getCurrentPlan().getTrialDays());
        Invoice invoice = new Invoice(subscriber.getId(), amount, LocalDate.now().plusDays(30));
        invoices.add(invoice);
        System.out.println("Invoice generated for " + subscriber.getName() + ": $" + amount);
        return invoice;
    }
    public Invoice generateInvoice(Subscriber subscriber, int daysUsed, String discountCode) {
        double amount = subscriber.getCurrentPlan().computeAmount(daysUsed);
        if (discountCode.equalsIgnoreCase("DISC10")) {
            amount *= 0.9; // 10% discount
        }
        Invoice invoice = new Invoice(subscriber.getId(), amount, LocalDate.now().plusDays(30));
        invoices.add(invoice);
        System.out.println("Invoice generated for " + subscriber.getName() + ": $" + amount + " with discount " + discountCode);
        return invoice;
    }

    public void recordPayment(Invoice invoice) {
        invoice.recordPayment();
    }

    public void showRevenueReport() {
        double total = 0;
        for (Invoice inv : invoices) {
            if (inv.getState().equals("Paid")) {
                total += inv.getAmount();
            }
        }
        System.out.println("Total Revenue Collected: $" + total);
    }

    public void showAgingReport() {
        System.out.println("Overdue Invoices:");
        for (Invoice inv : invoices) {
            if (inv.isOverdue()) {
                System.out.println(inv);
            }
// Main class
public class BillingAppMain {
    public static void main(String[] args) {
        Plan basicMonthly = new MonthlyPlan(1, "Basic", 50, "Email Support", 7);
        Plan proAnnual = new AnnualPlan(2, "Pro", 100, "Priority Support + Analytics", 14);
        Subscriber alice = new Subscriber(101, "Alice", "alice@example.com", basicMonthly);
        Subscriber bob = new Subscriber(102, "Bob", "bob@example.com", proAnnual);

        // Billing Service
        BillingService billingService = new BillingService();

        // Generate invoices
        Invoice invoice1 = billingService.generateInvoice(alice); // no proration
        Invoice invoice2 = billingService.generateInvoice(bob, 365, "DISC10"); // with discount

        alice.changePlan(proAnnual);        
        Invoice invoice3 = billingService.generateInvoice(alice, 30, "DISC10");

        
        billingService.recordPayment(invoice1);
        billingService.recordPayment(invoice2);
        billingService.showRevenueReport();
        billingService.showAgingReport();
    }
}
