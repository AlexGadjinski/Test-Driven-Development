import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InstockTest {
    private static final String LABEL = "test_product";
    private static final double PRICE = 13.50;
    private static final int QUANTITY = 21;
    private ProductStock stock;
    private Product product;

    @BeforeEach
    public void setUp() {
        this.stock = new Instock();
        this.product = new Product(LABEL, PRICE, QUANTITY);
    }

    @Test
    public void test_AddAndContains_ReturnsCorrectResult_BeforeAndAfter_Addition() {
        assertFalse(stock.contains(product));
        stock.add(product);
        assertTrue(stock.contains(product));
    }

    @Test
    public void test_GetCount_ReturnsCorrectSize() {
        assertEquals(0, stock.getCount());
        stock.add(product);
        assertEquals(1, stock.getCount());
    }

    @Test
    public void test_Find_WithInvalidIndex_Throws() {
        assertThrows(IndexOutOfBoundsException.class, () -> stock.find(0));
    }

    @Test
    public void test_Find_WithInvalidIndexWhenNegative_Throws() {
        assertThrows(IndexOutOfBoundsException.class, () -> stock.find(-1));
    }

    @Test
    public void test_Find_WithValidIndex_ReturnsCorrectProduct() {
        stock.add(product);
        Product actual = stock.find(0);
        assertNotNull(actual);
        assertEquals(product.getLabel(), actual.getLabel());
    }

    @Test
    public void test_Find_WithValidIndexWithMultipleProducts_ReturnsCorrectProduct() {
        populateProducts();
        stock.add(product);
        Product actual = stock.find(stock.getCount() - 1);
        assertNotNull(actual);
        assertEquals(product.getLabel(), actual.getLabel());
    }

    @Test
    public void test_ChangeQuantity_WhenProductMissing_Throws() {
        stock.add(product);
        assertThrows(IllegalArgumentException.class,
                () -> stock.changeQuantity(LABEL + 3.14, 10));
    }

    @Test
    public void test_ChangeQuantity_CorrectlyUpdatesTheQuantity() {
        stock.add(product);
        populateProducts();
        int newQuantity = QUANTITY + 13;

        stock.changeQuantity(LABEL, newQuantity);
        assertEquals(newQuantity, product.getQuantity());
    }

    @Test
    public void test_FindByLabel_WhenProductMissing_Throws() {
        stock.add(product);
        assertThrows(IllegalArgumentException.class, () -> stock.findByLabel(LABEL + 4.5));
    }

    @Test
    public void test_FindByLabel_ReturnsCorrectProduct() {
        populateProducts();
        stock.add(product);
        Product actual = stock.findByLabel(LABEL);
        assertNotNull(actual);
        assertEquals(LABEL, actual.getLabel());
    }

    @Test
    public void test_FindFirstByAlphabeticalOrder_WhenNotEnoughProducts_ReturnsEmptyCollection() {
        int productsCount = populateProducts().size();
        Iterable<Product> iterable = stock.findFirstByAlphabeticalOrder(productsCount + 1);
        assertTrue(listFromIterable(iterable).isEmpty());
    }

    @Test
    public void test_FindFirstByAlphabeticalOrder_ReturnsCorrectNumberOfProducts() {
        int productsCount = populateProducts().size();
        stock.add(product);
        Iterable<Product> iterable = stock.findFirstByAlphabeticalOrder(productsCount);
        assertEquals(productsCount, listFromIterable(iterable).size());
    }

    @Test
    public void test_FindFirstByAlphabeticalOrder_ReturnsProductsOrdered() {
        List<Product> expected = populateProducts().stream()
                        .sorted(Comparator.comparing(Product::getLabel))
                        .toList();

        stock.add(product);
        Iterable<Product> iterable = stock.findFirstByAlphabeticalOrder(expected.size());
        List<Product> actual = listFromIterable(iterable);
        assertEquals(expected, actual);
    }

    @Test
    public void test_FindAllInRange_WhenNoneInRange_ReturnsEmptyCollection() {
        populateProducts();
        Iterable<Product> iterable = stock.findAllInRange(42.00, 70.00);
        assertTrue(listFromIterable(iterable).isEmpty());
    }

    @Test
    public void test_FindAllInRange_ReturnsInCorrectRange() {
        populateProducts();
        double lower = 3.00;
        double upper = 33.00;
        Iterable<Product> iterable = stock.findAllInRange(lower, upper);
        List<Product> products = listFromIterable(iterable);
        assertTrue(products.stream()
                .map(Product::getPrice)
                .allMatch(price -> price > lower && price <= upper));
    }

    @Test
    public void test_FindAllInRange_ReturnsInCorrectOrder() {
        double lower = 3.00;
        double upper = 33.00;
        List<Product> expected = populateProducts().stream()
                .filter(p -> p.getPrice() > lower && p.getPrice() <= upper)
                .sorted(Comparator.comparing(Product::getPrice).reversed())
                .toList();
        Iterable<Product> iterable = stock.findAllInRange(lower, upper);
        List<Product> actual = listFromIterable(iterable);
        assertEquals(expected, actual);
    }

    @Test
    public void test_FindAllByPrice_WhenNoneMatch_ReturnsEmptyCollection() {
        populateProducts();
        Iterable<Product> iterable = stock.findAllByPrice(100.00);
        assertTrue(listFromIterable(iterable).isEmpty());
    }

    @Test
    public void test_FindAllByPrice_ReturnsOnlyMatchingProducts() {
        populateProducts();
        double price = 2.00;
        Iterable<Product> iterable = stock.findAllByPrice(price);
        assertTrue(listFromIterable(iterable).stream()
                .map(Product::getPrice)
                .allMatch(p -> p == price));

    }

    @Test
    public void test_FindFirstMostExpensiveProducts_WhenLessProductsPresent_Throws() {
        int productsCount = populateProducts().size();
        assertThrows(IllegalArgumentException.class,
                () -> stock.findFirstMostExpensiveProducts(productsCount + 1));
    }

    @Test
    public void test_FindFirstMostExpensiveProducts_ReturnsCorrectProducts() {
        int count = 4;
        List<Product> expected = populateProducts().stream()
                .sorted(Comparator.comparing(Product::getPrice).reversed())
                .limit(count)
                .toList();
        Iterable<Product> iterable = stock.findFirstMostExpensiveProducts(count);
        List<Product> actual = listFromIterable(iterable);
        assertEquals(expected, actual);
    }

    @Test
    public void test_FindAllByQuantity_WhenNoneMatch_ReturnsEmptyCollection() {
        stock.add(product);
        Iterable<Product> iterable = stock.findAllByQuantity(QUANTITY + 10);
        assertTrue(listFromIterable(iterable).isEmpty());
    }

    @Test
    public void test_FindAllByQuantity_ReturnsOnlyProductsWithMatchingQuantity() {
        stock.add(product);
        populateProducts();
        int quantity = 69;
        Iterable<Product> iterable = stock.findAllByQuantity(quantity);
        assertTrue(listFromIterable(iterable).stream()
                .map(Product::getQuantity)
                .allMatch(q -> q == quantity));
    }

    @Test
    public void test_Iterator_WhenEmpty_ReturnsEmptyCollection() {
        Iterator<Product> iterator = stock.iterator();
        assertNotNull(iterator);
        assertFalse(iterator.hasNext());
    }

    @Test
    public void test_Iterator_ReturnsAllProducts() {
        List<Product> expected = populateProducts();

        Iterator<Product> iterator = stock.iterator();
        List<Product> actual = listFromIterator(iterator);

        assertEquals(expected, actual);
    }


    private List<Product> populateProducts() {
        List<Product> products = createProducts();
        products.forEach(stock::add);
        return products;
    }

    private List<Product> createProducts() {
        return List.of(
                new Product("test_label_3", 42.00, 31),
                new Product("test_label_4", 33.00, 69),
                new Product("test_label_8", 2.00, 2),
                new Product("test_label_6", 21.00, 69),
                new Product("test_label_2", 7.00, 3),
                new Product("test_label_9", 2.00, 8),
                new Product("test_label_5", 3.00, 13),
                new Product("test_label_10", 2.00, 73),
                new Product("test_label_7", 2.00, 69)
        );
    }

    private List<Product> listFromIterable(Iterable<Product> iterable) {
        assertNotNull(iterable);
//        List<Product> products = new ArrayList<>();
//        iterable.forEach(products::add);
//        return products;
        return listFromIterator(iterable.iterator());
    }

    private List<Product> listFromIterator(Iterator<Product> iterator) {
        assertNotNull(iterator);
        List<Product> products = new ArrayList<>();
        while (iterator.hasNext()) {
            products.add(iterator.next());
        }
        return products;
    }
}
