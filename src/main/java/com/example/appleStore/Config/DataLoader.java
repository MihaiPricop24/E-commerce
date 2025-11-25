package com.example.appleStore.Config;

import com.example.appleStore.Model.*;
import com.example.appleStore.Repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(CategoryRepository categoryRepo,
                               ProductRepository productRepo,
                               ProductVariantRepository variantRepo) {
        return args -> {
            // Only load if database is empty
            if (categoryRepo.count() > 0) {
                return; // Data already exists
            }

            System.out.println("🔄 Loading test data...");

            // ========== CREATE CATEGORIES ==========
            Category phones = new Category();
            phones.setCategoryName("Phones");
            categoryRepo.save(phones);

            Category laptops = new Category();
            laptops.setCategoryName("Laptops");
            categoryRepo.save(laptops);

            Category tvs = new Category();
            tvs.setCategoryName("TVs");
            categoryRepo.save(tvs);

            Category displays = new Category();
            displays.setCategoryName("Displays");
            categoryRepo.save(displays);

            Category tablets = new Category();
            tablets.setCategoryName("Tablets");
            categoryRepo.save(tablets);

            // ========== PHONES CATEGORY ==========
            Product iphone15Pro = createProduct(productRepo, "iPhone 15 Pro",
                    "Latest iPhone with A17 Pro chip and titanium design", phones);
            createVariant(variantRepo, iphone15Pro, "Natural Titanium", "128GB", 999.00, 50);
            createVariant(variantRepo, iphone15Pro, "Blue Titanium", "256GB", 1099.00, 45);
            createVariant(variantRepo, iphone15Pro, "White Titanium", "512GB", 1299.00, 30);
            createVariant(variantRepo, iphone15Pro, "Black Titanium", "1TB", 1499.00, 20);
            createVariant(variantRepo, iphone15Pro, "Natural Titanium", "256GB", 1099.00, 40);

            Product iphone15 = createProduct(productRepo, "iPhone 15",
                    "Powerful iPhone with Dynamic Island and USB-C", phones);
            createVariant(variantRepo, iphone15, "Pink", "128GB", 799.00, 60);
            createVariant(variantRepo, iphone15, "Blue", "256GB", 899.00, 55);
            createVariant(variantRepo, iphone15, "Yellow", "512GB", 1099.00, 35);
            createVariant(variantRepo, iphone15, "Black", "128GB", 799.00, 50);
            createVariant(variantRepo, iphone15, "Green", "256GB", 899.00, 45);

            Product iphone14 = createProduct(productRepo, "iPhone 14",
                    "Great value iPhone with advanced camera system", phones);
            createVariant(variantRepo, iphone14, "Blue", "128GB", 699.00, 70);
            createVariant(variantRepo, iphone14, "Purple", "256GB", 799.00, 60);
            createVariant(variantRepo, iphone14, "Midnight", "512GB", 999.00, 40);
            createVariant(variantRepo, iphone14, "Starlight", "128GB", 699.00, 65);
            createVariant(variantRepo, iphone14, "Red", "256GB", 799.00, 55);

            Product iphoneSE = createProduct(productRepo, "iPhone SE",
                    "Most affordable iPhone with A15 Bionic chip", phones);
            createVariant(variantRepo, iphoneSE, "Midnight", "64GB", 429.00, 80);
            createVariant(variantRepo, iphoneSE, "Starlight", "128GB", 479.00, 75);
            createVariant(variantRepo, iphoneSE, "Red", "256GB", 579.00, 50);
            createVariant(variantRepo, iphoneSE, "Midnight", "128GB", 479.00, 70);
            createVariant(variantRepo, iphoneSE, "Starlight", "64GB", 429.00, 85);

            Product iphone13 = createProduct(productRepo, "iPhone 13",
                    "Still powerful and capable in 2024", phones);
            createVariant(variantRepo, iphone13, "Green", "128GB", 599.00, 60);
            createVariant(variantRepo, iphone13, "Pink", "256GB", 699.00, 55);
            createVariant(variantRepo, iphone13, "Blue", "512GB", 899.00, 35);
            createVariant(variantRepo, iphone13, "Midnight", "128GB", 599.00, 65);
            createVariant(variantRepo, iphone13, "Starlight", "256GB", 699.00, 50);

            // ========== LAPTOPS CATEGORY ==========
            Product mbp16 = createProduct(productRepo, "MacBook Pro 16\"",
                    "Professional laptop with M3 Max chip", laptops);
            createVariant(variantRepo, mbp16, "Space Black", "512GB", 2499.00, 25);
            createVariant(variantRepo, mbp16, "Silver", "1TB", 2899.00, 20);
            createVariant(variantRepo, mbp16, "Space Black", "1TB", 2899.00, 15);
            createVariant(variantRepo, mbp16, "Silver", "2TB", 3499.00, 10);
            createVariant(variantRepo, mbp16, "Space Black", "2TB", 3499.00, 12);

            Product mbp14 = createProduct(productRepo, "MacBook Pro 14\"",
                    "Compact powerhouse with M3 Pro chip", laptops);
            createVariant(variantRepo, mbp14, "Space Black", "512GB", 1999.00, 30);
            createVariant(variantRepo, mbp14, "Silver", "1TB", 2299.00, 25);
            createVariant(variantRepo, mbp14, "Space Black", "1TB", 2299.00, 20);
            createVariant(variantRepo, mbp14, "Silver", "512GB", 1999.00, 35);
            createVariant(variantRepo, mbp14, "Space Black", "2TB", 2899.00, 15);

            Product mbaM3 = createProduct(productRepo, "MacBook Air M3",
                    "Thin, light, and powerful everyday laptop", laptops);
            createVariant(variantRepo, mbaM3, "Midnight", "256GB", 1099.00, 40);
            createVariant(variantRepo, mbaM3, "Starlight", "512GB", 1299.00, 35);
            createVariant(variantRepo, mbaM3, "Space Gray", "256GB", 1099.00, 45);
            createVariant(variantRepo, mbaM3, "Silver", "512GB", 1299.00, 30);
            createVariant(variantRepo, mbaM3, "Midnight", "512GB", 1299.00, 38);

            Product mbaM2 = createProduct(productRepo, "MacBook Air M2",
                    "Previous gen Air with great performance", laptops);
            createVariant(variantRepo, mbaM2, "Midnight", "256GB", 999.00, 45);
            createVariant(variantRepo, mbaM2, "Starlight", "512GB", 1199.00, 40);
            createVariant(variantRepo, mbaM2, "Space Gray", "256GB", 999.00, 50);
            createVariant(variantRepo, mbaM2, "Silver", "512GB", 1199.00, 35);
            createVariant(variantRepo, mbaM2, "Midnight", "512GB", 1199.00, 42);

            Product mbaM1 = createProduct(productRepo, "MacBook Air M1",
                    "Amazing value with groundbreaking M1 chip", laptops);
            createVariant(variantRepo, mbaM1, "Space Gray", "256GB", 899.00, 55);
            createVariant(variantRepo, mbaM1, "Silver", "512GB", 1099.00, 45);
            createVariant(variantRepo, mbaM1, "Gold", "256GB", 899.00, 50);
            createVariant(variantRepo, mbaM1, "Space Gray", "512GB", 1099.00, 40);
            createVariant(variantRepo, mbaM1, "Silver", "256GB", 899.00, 60);

            // ========== TVs CATEGORY ==========
            Product appleTV4K = createProduct(productRepo, "Apple TV 4K",
                    "Stunning 4K entertainment with Dolby Atmos", tvs);
            createVariant(variantRepo, appleTV4K, "Black", "64GB", 129.00, 100);
            createVariant(variantRepo, appleTV4K, "Black", "128GB", 149.00, 90);
            createVariant(variantRepo, appleTV4K, "Black", "64GB WiFi", 129.00, 85);
            createVariant(variantRepo, appleTV4K, "Black", "128GB WiFi+Ethernet", 149.00, 75);
            createVariant(variantRepo, appleTV4K, "Black", "64GB (2023)", 129.00, 95);

            Product appleTV = createProduct(productRepo, "Apple TV HD",
                    "Full HD entertainment device", tvs);
            createVariant(variantRepo, appleTV, "Black", "32GB", 99.00, 120);
            createVariant(variantRepo, appleTV, "Black", "32GB WiFi", 99.00, 110);
            createVariant(variantRepo, appleTV, "Black", "32GB (Refurb)", 79.00, 80);
            createVariant(variantRepo, appleTV, "Black", "32GB Bundle", 119.00, 70);
            createVariant(variantRepo, appleTV, "Black", "32GB (2022)", 99.00, 100);

            Product smartTV55 = createProduct(productRepo, "Apple Smart TV 55\"",
                    "Hypothetical Apple-branded 4K OLED TV", tvs);
            createVariant(variantRepo, smartTV55, "Silver", "55 inch", 1999.00, 30);
            createVariant(variantRepo, smartTV55, "Space Gray", "55 inch", 1999.00, 25);
            createVariant(variantRepo, smartTV55, "Silver", "55 inch 4K", 1999.00, 28);
            createVariant(variantRepo, smartTV55, "Space Gray", "55 inch 8K", 2499.00, 15);
            createVariant(variantRepo, smartTV55, "Silver", "55 inch OLED", 2199.00, 20);

            Product smartTV65 = createProduct(productRepo, "Apple Smart TV 65\"",
                    "Large format entertainment center", tvs);
            createVariant(variantRepo, smartTV65, "Silver", "65 inch", 2499.00, 20);
            createVariant(variantRepo, smartTV65, "Space Gray", "65 inch", 2499.00, 18);
            createVariant(variantRepo, smartTV65, "Silver", "65 inch 8K", 2999.00, 12);
            createVariant(variantRepo, smartTV65, "Space Gray", "65 inch OLED", 2799.00, 15);
            createVariant(variantRepo, smartTV65, "Silver", "65 inch Mini-LED", 2699.00, 16);

            Product smartTV77 = createProduct(productRepo, "Apple Smart TV 77\"",
                    "Premium large screen experience", tvs);
            createVariant(variantRepo, smartTV77, "Silver", "77 inch", 3499.00, 10);
            createVariant(variantRepo, smartTV77, "Space Gray", "77 inch", 3499.00, 8);
            createVariant(variantRepo, smartTV77, "Silver", "77 inch 8K", 3999.00, 5);
            createVariant(variantRepo, smartTV77, "Space Gray", "77 inch OLED", 3799.00, 7);
            createVariant(variantRepo, smartTV77, "Silver", "77 inch QD-OLED", 3899.00, 6);

            // ========== DISPLAYS CATEGORY ==========
            Product studioDis = createProduct(productRepo, "Studio Display",
                    "5K Retina display with incredible color accuracy", displays);
            createVariant(variantRepo, studioDis, "Silver", "Standard Glass", 1599.00, 35);
            createVariant(variantRepo, studioDis, "Silver", "Nano-texture Glass", 1899.00, 25);
            createVariant(variantRepo, studioDis, "Silver", "Standard + Tilt Stand", 1599.00, 30);
            createVariant(variantRepo, studioDis, "Silver", "Nano + Tilt/Height", 2299.00, 15);
            createVariant(variantRepo, studioDis, "Silver", "Standard + VESA", 1599.00, 28);

            Product proDisplayXDR = createProduct(productRepo, "Pro Display XDR",
                    "32-inch Retina 6K HDR display for professionals", displays);
            createVariant(variantRepo, proDisplayXDR, "Silver", "Standard Glass", 4999.00, 10);
            createVariant(variantRepo, proDisplayXDR, "Silver", "Nano-texture Glass", 5999.00, 8);
            createVariant(variantRepo, proDisplayXDR, "Silver", "Standard + Pro Stand", 5999.00, 7);
            createVariant(variantRepo, proDisplayXDR, "Silver", "Nano + Pro Stand", 6999.00, 5);
            createVariant(variantRepo, proDisplayXDR, "Silver", "Standard + VESA", 4999.00, 9);

            Product display27 = createProduct(productRepo, "27\" 4K Display",
                    "High resolution display for everyday use", displays);
            createVariant(variantRepo, display27, "Silver", "27 inch 4K", 699.00, 40);
            createVariant(variantRepo, display27, "Space Gray", "27 inch 4K", 699.00, 38);
            createVariant(variantRepo, display27, "Silver", "27 inch 4K USB-C", 799.00, 35);
            createVariant(variantRepo, display27, "Space Gray", "27 inch 5K", 899.00, 30);
            createVariant(variantRepo, display27, "Silver", "27 inch 5K Retina", 999.00, 25);

            Product display24 = createProduct(productRepo, "24\" Display",
                    "Compact 4.5K Retina display", displays);
            createVariant(variantRepo, display24, "Silver", "24 inch", 499.00, 50);
            createVariant(variantRepo, display24, "Blue", "24 inch", 499.00, 45);
            createVariant(variantRepo, display24, "Pink", "24 inch", 499.00, 42);
            createVariant(variantRepo, display24, "Green", "24 inch", 499.00, 40);
            createVariant(variantRepo, display24, "Orange", "24 inch", 499.00, 38);

            Product ultrawide = createProduct(productRepo, "34\" Ultrawide Display",
                    "Immersive ultrawide display for productivity", displays);
            createVariant(variantRepo, ultrawide, "Silver", "34 inch", 1299.00, 25);
            createVariant(variantRepo, ultrawide, "Space Gray", "34 inch", 1299.00, 22);
            createVariant(variantRepo, ultrawide, "Silver", "34 inch Curved", 1399.00, 20);
            createVariant(variantRepo, ultrawide, "Space Gray", "34 inch 5K2K", 1499.00, 18);
            createVariant(variantRepo, ultrawide, "Silver", "34 inch Thunderbolt", 1599.00, 15);

            // ========== TABLETS CATEGORY ==========
            Product ipadPro129 = createProduct(productRepo, "iPad Pro 12.9\"",
                    "Ultimate iPad experience with M2 chip", tablets);
            createVariant(variantRepo, ipadPro129, "Space Gray", "128GB", 1099.00, 30);
            createVariant(variantRepo, ipadPro129, "Silver", "256GB", 1199.00, 28);
            createVariant(variantRepo, ipadPro129, "Space Gray", "512GB", 1399.00, 25);
            createVariant(variantRepo, ipadPro129, "Silver", "1TB", 1799.00, 18);
            createVariant(variantRepo, ipadPro129, "Space Gray", "2TB", 2199.00, 12);

            Product ipadPro11 = createProduct(productRepo, "iPad Pro 11\"",
                    "Portable pro tablet with M2 chip", tablets);
            createVariant(variantRepo, ipadPro11, "Space Gray", "128GB", 799.00, 35);
            createVariant(variantRepo, ipadPro11, "Silver", "256GB", 899.00, 32);
            createVariant(variantRepo, ipadPro11, "Space Gray", "512GB", 1099.00, 28);
            createVariant(variantRepo, ipadPro11, "Silver", "1TB", 1499.00, 20);
            createVariant(variantRepo, ipadPro11, "Space Gray", "2TB", 1899.00, 15);

            Product ipadAir = createProduct(productRepo, "iPad Air",
                    "Perfect balance of performance and portability", tablets);
            createVariant(variantRepo, ipadAir, "Space Gray", "64GB", 599.00, 45);
            createVariant(variantRepo, ipadAir, "Blue", "256GB", 749.00, 40);
            createVariant(variantRepo, ipadAir, "Pink", "64GB", 599.00, 42);
            createVariant(variantRepo, ipadAir, "Purple", "256GB", 749.00, 38);
            createVariant(variantRepo, ipadAir, "Starlight", "256GB", 749.00, 35);

            Product ipad = createProduct(productRepo, "iPad",
                    "Most popular iPad for everyday tasks", tablets);
            createVariant(variantRepo, ipad, "Silver", "64GB", 329.00, 60);
            createVariant(variantRepo, ipad, "Space Gray", "256GB", 479.00, 55);
            createVariant(variantRepo, ipad, "Blue", "64GB", 329.00, 52);
            createVariant(variantRepo, ipad, "Pink", "256GB", 479.00, 48);
            createVariant(variantRepo, ipad, "Yellow", "64GB", 329.00, 50);

            Product ipadMini = createProduct(productRepo, "iPad mini",
                    "Pocket-sized powerhouse with A15 Bionic", tablets);
            createVariant(variantRepo, ipadMini, "Space Gray", "64GB", 499.00, 40);
            createVariant(variantRepo, ipadMini, "Pink", "256GB", 649.00, 35);
            createVariant(variantRepo, ipadMini, "Purple", "64GB", 499.00, 38);
            createVariant(variantRepo, ipadMini, "Starlight", "256GB", 649.00, 32);
            createVariant(variantRepo, ipadMini, "Space Gray", "256GB", 649.00, 30);

            System.out.println("   - Test data loaded successfully!");
            System.out.println("   - Created:");
            System.out.println("   - 5 Categories");
            System.out.println("   - 25 Products (5 per category)");
            System.out.println("   - 125 Product Variants (5 per product)");
        };
    }

    private Product createProduct(ProductRepository repo, String name, String description, Category category) {
        Product product = new Product();
        product.setProductName(name);
        product.setProductDescription(description);
        product.setCategory(category);
        return repo.save(product);
    }

    private void createVariant(ProductVariantRepository repo, Product product,
                               String color, String storage, Double price, Integer quantity) {
        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setColor(color);
        variant.setStorage(storage);

        // Generate filename from product name (ONE IMAGE PER PRODUCT)
        String filename = product.getProductName().toLowerCase()
                .replace(" ", "-")
                .replaceAll("[^a-z0-9-]", "")
                + ".jpg";

        variant.setImageUrl("/images/products/" + filename);

        variant.setPrice(price);
        variant.setQuantity(quantity);
        repo.save(variant);
    }
}