package task.apriori;

public class Transaction {
    private int transactionId;
    private String productName;

    public Transaction(int transactionId, String productName) {
        this.transactionId = transactionId;
        this.productName = productName;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public String getProductName() {
        return productName;
    }

    @Override
    public String toString() {
        return "Transaction ID: " + transactionId + ", Product Name: " + productName;
    }
}
