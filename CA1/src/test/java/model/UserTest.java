package model;

import exceptions.CommodityIsNotInBuyList;
import exceptions.InsufficientCredit;
import exceptions.InvalidCreditRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User tests")
public class UserTest {
    User user;
    String commodityId = "1";

    Commodity createBuyList(int quantity) {
        Commodity commodity = new Commodity();
        commodity.setId(commodityId);
        for (int i = 0; i < quantity; i++)
            user.addBuyItem(commodity);
        return commodity;
    }

    void createPurchaseList(int amount, int... quantities) {
        for (int i = 0; i < amount; i++)
            user.addPurchasedItem(commodityId, quantities[i]);
    }

    @BeforeEach
    void init() {
        user = new User("username", "pass", "email", "2000-01-01", "address");
    }

    @ParameterizedTest
    @ValueSource(ints = { 2, 1, 0 })
    @DisplayName("Add user credit test")
    void addCreditTest(int amount) {
        float beforeCredit = user.getCredit();
        assertDoesNotThrow(() -> user.addCredit(amount));
        float afterCredit = user.getCredit();
        assertEquals(beforeCredit + amount, afterCredit);
    }

    @Test
    @DisplayName("Throw invalid credit range test")
    void addCreditExceptionTest() {
        assertThrows(InvalidCreditRange.class, () -> user.addCredit(-1));
    }

    @ParameterizedTest
    @ValueSource(ints = { 2, 1, 0 })
    @DisplayName("Withdraw user credit test")
    void withdrawCreditTest(int amount) {
        float beforeCredit = 2;
        user.setCredit(beforeCredit);
        assertDoesNotThrow(() -> user.withdrawCredit(amount));
        float afterCredit = user.getCredit();
        assertEquals(beforeCredit - amount, afterCredit);
    }

    @Test
    @DisplayName("Throw insufficient credit test")
    void withdrawCreditExceptionTest() {
        user.setCredit(2);
        assertThrows(InsufficientCredit.class, () -> user.withdrawCredit(3));
    }

    @Test
    @DisplayName("Add new commodity in the buy list test")
    void addNewCommodityTest() {
        createBuyList(1);
        var buyList = user.getBuyList();
        int quantity = buyList.get(commodityId);
        assertEquals(1, quantity);
    }

    @Test
    @DisplayName("Add existing commodity in the buy list test")
    void addExistingCommodityTest() {
        createBuyList(2);
        var buyList = user.getBuyList();
        int quantity = buyList.get(commodityId);
        assertEquals(2, quantity);
    }

    @Test
    @DisplayName("Reduce commodity quantity in the buy list test")
    void reduceCommodityQuantityTest() {
        var commodity = createBuyList(2);
        assertDoesNotThrow(() -> user.removeItemFromBuyList(commodity));
        var buyList = user.getBuyList();
        int quantity = buyList.get(commodityId);
        assertEquals(1, quantity);
    }

    @Test
    @DisplayName("Remove commodity from the buy list test")
    void removeCommodityTest() {
        var commodity = createBuyList(1);
        assertDoesNotThrow(() -> user.removeItemFromBuyList(commodity));
        var buyList = user.getBuyList();
        assertFalse(buyList.containsKey(commodityId));
    }

    @Test
    @DisplayName("Throw commodity is not in buy list test")
    void removeCommodityExceptionTest() {
        var commodity = new Commodity();
        commodity.setId(commodityId);
        assertThrows(CommodityIsNotInBuyList.class, () -> user.removeItemFromBuyList(commodity));
    }

    @Test
    @DisplayName("Purchase new item test")
    void purchaseNewItemTest() {
        int quantity = 3;
        createPurchaseList(1, quantity);
        var purchasedList = user.getPurchasedList();
        assertEquals(quantity, purchasedList.get(commodityId));
    }

    @Test
    @DisplayName("Purchase existing item test")
    void purchaseExistingItemTest() {
        int[] quantities = {3, 5};
        createPurchaseList(2, quantities);
        var purchasedList = user.getPurchasedList();
        assertEquals(IntStream.of(quantities).sum(), purchasedList.get(commodityId));
    }
}
