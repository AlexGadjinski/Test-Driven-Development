import jdk.jshell.spi.ExecutionControl;

import java.util.*;
import java.util.stream.Collectors;

public class Instock implements ProductStock {
    private List<Product> products;

    public Instock() {
        this.products = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public boolean contains(Product product) {
        return products.stream()
                .anyMatch(p -> p.getLabel().equals(product.getLabel()));
//        return products.contains(product);
    }

    @Override
    public void add(Product product) {
        products.add(product);
    }

    @Override
    public void changeQuantity(String label, int quantity) {
        Product product = getProduct(label);
        product.setQuantity(quantity);
    }

    private Product getProduct(String label) {
        return products.stream()
                .filter(p -> p.getLabel().equals(label))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public Product find(int index) {
        if (index < 0 || index >= getCount()) {
            throw new IndexOutOfBoundsException();
        }
        return products.get(index);
    }

    @Override
    public Product findByLabel(String label) {
        return getProduct(label);
    }

    @Override
    public Iterable<Product> findFirstByAlphabeticalOrder(int count) {
        if (count > getCount()) {
            return new ArrayList<>();
        }

        return products.stream()
                .sorted(Comparator.comparing(Product::getLabel))
                .limit(count)
                .toList();
    }

    @Override
    public Iterable<Product> findAllInRange(double lo, double hi) {
        return products.stream()
                .filter(p -> p.getPrice() > lo && p.getPrice() <= hi)
                .sorted(Comparator.comparing(Product::getPrice).reversed())
                .toList();
    }

    @Override
    public Iterable<Product> findAllByPrice(double price) {
        return products.stream()
                .filter(p -> p.getPrice() == price)
                .toList();
    }

    @Override
    public Iterable<Product> findFirstMostExpensiveProducts(int count) {
        if (count > getCount()) {
            throw new IllegalArgumentException();
        }
        return products.stream()
                .sorted(Comparator.comparing(Product::getPrice).reversed())
                .limit(count)
                .toList();
    }

    @Override
    public Iterable<Product> findAllByQuantity(int quantity) {
        return products.stream()
                .filter(p -> p.getQuantity() == quantity)
                .toList();
    }

    @Override
    public Iterator<Product> iterator() {
        return products.iterator();
    }
}
