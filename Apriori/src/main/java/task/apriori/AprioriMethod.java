package task.apriori;

import java.util.*;

public class AprioriMethod {
    private final int ALL_TRANSACTIONS_NUMBER = 100;
    private List<Transaction> transactions;
    private final double MIN_SUPPORT;
    private final double MIN_CREDIBILITY;
    private List<Sets> frequentSets = new ArrayList<>();
    private List<AssociativeRule> associativeRules = new ArrayList<>();
    private Set<String> productsSet;

    public AprioriMethod(List<Transaction> data, double MIN_SUPPORT, double MIN_CREDIBILITY) {
        this.transactions = data;
        this.MIN_SUPPORT = MIN_SUPPORT;
        this.MIN_CREDIBILITY = MIN_CREDIBILITY;
    }

    public void start() {
        productsSet = getAllProducts();
        generateFrequentSets();
        printFrequentSets();
        createAssociativeRules();
        printAssociativeRules();
    }

    private Set<String> getAllProducts() {
            Set<String> allProducts = new HashSet<>();
            for (Transaction transaction : transactions) {
                allProducts.add(transaction.getProductName());
            }
            return allProducts;
    }

    private void generateFrequentSets() {
        int coefficient = 1;
        List<Set<String>> currentFrequentSets = new ArrayList<>();
        List<Set<String>> nextFrequentSets;

        while (true) {
            Set<Set<String>> combinations = generateCombinations(new ArrayList<>(productsSet), coefficient);

            if (combinations.isEmpty()) {
                break;
            }

            nextFrequentSets = new ArrayList<>();

            for (Set<String> setOfProducts : combinations) {

                if (setOfProducts.size() > 2 && !areAllSubsetsFrequent(setOfProducts, currentFrequentSets)) {
                    continue;
                }

                double support = calculateSupportForSet(setOfProducts);
                if (support >= MIN_SUPPORT) {
                    Sets frequentSet = new Sets(setOfProducts, support);
                    frequentSets.add(frequentSet);
                    nextFrequentSets.add(setOfProducts);
                }
            }

            if (nextFrequentSets.isEmpty()) {
                break;
            }

            currentFrequentSets = nextFrequentSets;
            coefficient++;
        }
    }

    private boolean areAllSubsetsFrequent(Set<String> setOfProducts, List<Set<String>> currentFrequentSets) {
        List<String> productList = new ArrayList<>(setOfProducts);
        Set<Set<String>> subsets = generateCombinations(productList, setOfProducts.size() - 1);
        for (Set<String> subset : subsets) {
            if (!currentFrequentSets.contains(subset)) {
                return false;
            }
        }
        return true;
    }

    private Set<Set<String>> generateCombinations(List<String> products, int length) {
        Set<Set<String>> result = new HashSet<>();
        generateCombinationsRecursive(products, length, 0, new HashSet<>(), result);
        return result;
    }

    private void generateCombinationsRecursive(List<String> products, int length, int start, Set<String> current, Set<Set<String>> result) {
        if (current.size() == length) {
            result.add(new HashSet<>(current));
            return;
        }

        for (int i = start; i < products.size(); i++) {
            current.add(products.get(i));
            generateCombinationsRecursive(products, length, i + 1, current, result);
            current.remove(products.get(i));
        }
    }

    private double calculateSupportForSet(Set<String> products) {
        int count = 0;

        Map<Integer, Set<String>> transactionMap = new HashMap<>();
        for (Transaction transaction : transactions) {
            transactionMap
                    .computeIfAbsent(transaction.getTransactionId(), k -> new HashSet<>())
                    .add(transaction.getProductName());
        }

        for (Set<String> productSet : transactionMap.values()) {
            if (productSet.containsAll(products)) {
                count++;
            }
        }

        return (double) count / ALL_TRANSACTIONS_NUMBER;
    }


    private void printFrequentSets() {
        if(frequentSets.isEmpty()) {
            System.out.println("There aren't frequent sets that satisfy the condition - min support");
        } else {
            System.out.println(" Frequent Sets: ");
            for (Sets frequentSet : frequentSets) {
                System.out.println(frequentSet);
            }
        }
    }

    public void createAssociativeRules() {
        List<AssociativeRule> intermediateRules = new ArrayList<>();

        for (Sets frequentSet : frequentSets) {
            Set<String> products = frequentSet.getProducts();
            if (products.size() < 2) continue;

            List<String> productList = new ArrayList<>(products);
            for (int i = 1; i < productList.size(); i++) {
                Set<Set<String>> subsets = generateCombinations(productList, i);
                for (Set<String> subset : subsets) {
                    Set<String> condition = subset;
                    Set<String> result = new HashSet<>(products);
                    result.removeAll(condition);

                    if (!result.isEmpty()) {
                        double confidence = calculateCredibility(condition, products);
                        AssociativeRule intermediateRule = new AssociativeRule(condition, result, confidence);
                        intermediateRules.add(intermediateRule);
                    }
                }
            }
        }

        for (AssociativeRule rule : intermediateRules) {
            if (rule.getConfidence() >= MIN_CREDIBILITY) {
                associativeRules.add(rule);
            }
        }
    }


    private double calculateCredibility(Set<String> condition, Set<String> products) {
        double conditionSupport = calculateSupportForSet(condition);
        double productSupport = calculateSupportForSet(products);
        return productSupport / conditionSupport;
    }

    private void printAssociativeRules() {
        if(associativeRules.isEmpty()) {
            System.out.println("\nThere aren't associative rules that satisfy the condition - min confidence");
        } else {
            System.out.println("\n Associative Rules: ");
            for (AssociativeRule rule : associativeRules) {
                System.out.println(rule);
            }
        }

    }

    private static class Sets {
        private Set<String> products;
        private double minSupportValue;

        public Sets(Set<String> products, double minSupportValue) {
            this.products = products;
            this.minSupportValue = minSupportValue;
        }

        public Set<String> getProducts() {
            return products;
        }

        public double getMinSupportValue() {
            return minSupportValue;
        }

        @Override
        public String toString() {
            return products + ", minSupportValue = " + minSupportValue + "}";
        }
    }

    private static class AssociativeRule {
        private Set<String> condition;
        private Set<String> result;
        private double confidence;

        public AssociativeRule(Set<String> condition, Set<String> result, double confidence) {
            this.condition = condition;
            this.result = result;
            this.confidence = confidence;
        }

        public double getConfidence() {
            return confidence;
        }

        @Override
        public String toString() {
            return String.format("Rule: {%s} -> {%s}  confidence: %.2f", condition, result, confidence);
        }
    }
}
