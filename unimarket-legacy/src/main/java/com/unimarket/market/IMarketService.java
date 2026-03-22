package com.unimarket.market;

import com.unimarket.model.Product;
import com.unimarket.model.User;

import java.util.List;

/**
 * Interfaz del servicio de mercado.
 */
public interface IMarketService {

    List<Product> getCatalog();

    void addToCart(int productId);

    double getCartTotal();

    void checkout(User user);

    void clearCart();
}
