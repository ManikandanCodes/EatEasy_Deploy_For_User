package com.example.backend.config;

import com.example.backend.model.User;
import com.example.backend.model.Restaurant;
import com.example.backend.model.MenuCategory;
import com.example.backend.model.MenuItem;
import com.example.backend.repository.MenuCategoryRepository;
import com.example.backend.repository.MenuItemRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.CouponRepository;
import com.example.backend.model.Coupon;
import com.example.backend.repository.RestaurantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

@Component
public class DataSeeder implements CommandLineRunner {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final RestaurantRepository restaurantRepository;
        private final MenuCategoryRepository menuCategoryRepository;
        private final MenuItemRepository menuItemRepository;
        private final JdbcTemplate jdbcTemplate;
        private final CouponRepository couponRepository;

        public DataSeeder(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        RestaurantRepository restaurantRepository,
                        MenuCategoryRepository menuCategoryRepository,
                        MenuItemRepository menuItemRepository,
                        JdbcTemplate jdbcTemplate,
                        CouponRepository couponRepository) {
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.restaurantRepository = restaurantRepository;
                this.menuCategoryRepository = menuCategoryRepository;
                this.menuItemRepository = menuItemRepository;
                this.jdbcTemplate = jdbcTemplate;
                this.couponRepository = couponRepository;
        }

        @Override
        public void run(String... args) throws Exception {

                System.out.println("Running Data Seeder...");

                try {
                        System.out.println("Normalizing roles in DB...");
                        jdbcTemplate.update(
                                        "UPDATE users SET role = 'RESTAURANT_OWNER' WHERE role = 'ROLE_RESTAURANT_OWNER'");
                        jdbcTemplate.update("UPDATE users SET role = 'ADMIN' WHERE role = 'ROLE_ADMIN'");
                        System.out.println("Roles normalized.");
                } catch (Exception e) {
                        System.err.println("Could not normalize roles: " + e.getMessage());
                }

                User admin = userRepository.findByEmail("admin@eateasy.com").orElse(null);

                if (admin == null) {
                        admin = new User();
                        admin.setName("Admin User");
                        admin.setEmail("admin@eateasy.com");
                        admin.setPhone("9999999999");
                        admin.setPassword(passwordEncoder.encode("admin123"));
                        admin.setRole(User.Role.ADMIN);
                        userRepository.save(admin);

                        System.out.println("Admin user created");
                } else {

                        if (admin.getRole() != User.Role.ADMIN) {
                                admin.setRole(User.Role.ADMIN);
                                userRepository.save(admin);
                                System.out.println("Admin role updated to ADMIN");
                        }
                        System.out.println("Admin user already exists");
                }

                User customer = userRepository.findByEmail("user@eateasy.com").orElse(null);

                if (customer == null) {
                        customer = new User();
                        customer.setName("Standard User");
                        customer.setEmail("user@eateasy.com");
                        customer.setPhone("9876543210");
                        customer.setPassword(passwordEncoder.encode("user123"));
                        customer.setRole(User.Role.CUSTOMER);
                        userRepository.save(customer);
                        System.out.println("Standard customer created");
                } else {
                        System.out.println("Standard customer already exists");
                }

                User owner = userRepository.findByEmail("iammanikandan.engineer@gmail.com").orElse(null);

                if (owner == null) {
                        owner = new User();
                        owner.setName("Manikandan");
                        owner.setEmail("iammanikandan.engineer@gmail.com");
                        owner.setPhone("1234567890");
                        owner.setPassword(passwordEncoder.encode("17072001"));
                        owner.setRole(User.Role.RESTAURANT_OWNER);
                        userRepository.save(owner);

                        System.out.println("Owner user created");
                } else {
                        boolean changed = false;
                        if (owner.getRole() != User.Role.RESTAURANT_OWNER) {
                                owner.setRole(User.Role.RESTAURANT_OWNER);
                                changed = true;
                        }

                        owner.setPassword(passwordEncoder.encode("17072001"));
                        changed = true;

                        if (changed) {
                                userRepository.save(owner);
                                System.out.println("Owner updated (Role/Password)");
                        }
                        System.out.println("Owner user already exists");
                }

                if (owner != null && restaurantRepository.findByOwnerId(owner.getId()).isEmpty()) {

                        Restaurant r = new Restaurant();
                        r.setName("Manikandan's Bistrot");
                        r.setDescription("Fusion of Mauritian Flavors");
                        r.setAddress("123, Royal Road, Port Louis");
                        r.setCuisines("Creole, Fusion");
                        r.setImageUrl("https://images.unsplash.com/photo-1589302168068-964664d93dc0");
                        r.setOwner(owner);
                        r.setStatus(Restaurant.ApprovalStatus.APPROVED);
                        r.setRating(4.5);
                        r.setOpeningHours("9 AM - 10 PM");

                        restaurantRepository.save(r);

                        System.out.println("Restaurant created: Manikandan's Bistrot");
                } else {
                        System.out.println("Manikandan's Bistrot already exists");
                        Restaurant existing = restaurantRepository.findByOwnerId(owner.getId()).get(0);

                        // Ensure it has the correct name if it was previously changed
                        if (existing.getName().equals("Le Flamboyant")) {
                                existing.setName("Manikandan's Bistrot");
                                existing.setDescription("Fusion of Mauritian Flavors");
                                existing.setAddress("123, Royal Road, Port Louis");
                                existing.setCuisines("Creole, Fusion");
                                restaurantRepository.save(existing);
                                System.out.println("Restored Manikandan's Bistrot name");
                        }
                }

                createOwnerAndRestaurant("Le Café Owner", "owner1@eateasy.com", "owner1",
                                "Le Café du Vieux Conseil", "Charming cafe in a historic setting.",
                                "Vieux Conseil Street, Port Louis", "Creole, European",
                                "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4");

                createOwnerAndRestaurant("Courtyard Owner", "owner2@eateasy.com", "owner2",
                                "The Courtyard", "Elegant dining with a fusion twist.",
                                "Port Louis Waterfront", "European, Fusion",
                                "https://images.unsplash.com/photo-1555396273-367ea4eb4db5");

                createOwnerAndRestaurant("Escale Owner", "owner3@eateasy.com", "owner3",
                                "Escale Creole", "Authentic home-style Creole cooking.",
                                "Moka, Mauritius", "Creole",
                                "https://images.unsplash.com/photo-1559339352-11d035aa65de");

                createOwnerAndRestaurant("Chateau Chef", "owner4@eateasy.com", "owner4",
                                "La Table du Château", "Fine dining in a colonial estate.",
                                "Labourdonnais, Mauritius", "European, Fine Dining",
                                "https://images.unsplash.com/photo-1560624052-449f5ddf0c31");

                createOwnerAndRestaurant("Tante Aurius", "owner5@eateasy.com", "owner5",
                                "Chez Tante Aurius", "Famous for seafood and Creole dishes.",
                                "Trou d'Eau Douce", "Creole, Seafood",
                                "https://images.unsplash.com/photo-1585518419759-7fe2e0fbf8a6");

                createOwnerAndRestaurant("Dragon Palace", "owner6@eateasy.com", "owner6",
                                "Dragon Palace", "Best Chinese-Mauritian food.",
                                "Quatre Bornes, Mauritius", "Chinese-Mauritian",
                                "https://images.unsplash.com/photo-1513639776629-7b61b0ca4909");

                createOwnerAndRestaurant("Dewan Chef", "owner7@eateasy.com", "owner7",
                                "Dewan", "Famous for Briani.",
                                "Rose Hill, Mauritius", "Indian-Mauritian",
                                "https://images.unsplash.com/photo-1631452180519-c014fe946bc7");

                createOwnerAndRestaurant("Ti Kouloir", "owner8@eateasy.com", "owner8",
                                "Ti Kouloir", "Best Boulettes and Mine.",
                                "Grand Baie, Mauritius", "Street Food",
                                "https://images.unsplash.com/photo-1552566626-52f8b828add9");

                createOwnerAndRestaurant("Rajah Owner", "owner9@eateasy.com", "owner9",
                                "Happy Rajah", "Authentic Indian cuisine in a royal setting.",
                                "Super U, Grand Baie", "Indian-Mauritian",
                                "https://images.unsplash.com/photo-1585937421612-70a008356f36");

                createOwnerAndRestaurant("Capitaine Owner", "owner10@eateasy.com", "owner10",
                                "Le Capitaine", "Fresh seafood with a view of the bay.",
                                "Royal Road, Grand Baie", "Seafood, Creole",
                                "https://images.unsplash.com/photo-1565557623262-b51c2513a641");

                createOwnerAndRestaurant("Luigi Owner", "owner11@eateasy.com", "owner11",
                                "Luigi's Italian Pizzeria", "Famous wood-fired pizzas and homemade pasta.",
                                "Royal Road, North Mauritius", "Italian, Pizza",
                                "https://images.unsplash.com/photo-1579751626657-72bc17010498");

                createOwnerAndRestaurant("Flamboyant Owner", "owner12@eateasy.com", "owner12",
                                "Le Flamboyant", "Authentic Mauritian Cuisine by the Sea",
                                "Royal Road, Grand Baie", "Creole, Seafood",
                                "https://images.unsplash.com/photo-1589302168068-964664d93dc0");

                createOwnerAndRestaurant("Chamarel Owner", "owner13@eateasy.com", "owner13",
                                "Le Chamarel Restaurant", "Panoramic views with authentic Creole cuisine.",
                                "Chamarel, Mauritius", "Creole",
                                "https://images.unsplash.com/photo-1590846406792-0adc7f938f1d");

                createOwnerAndRestaurant("Fangourin Chef", "owner14@eateasy.com", "owner14",
                                "Le Fangourin", "Refined cuisine in a historic setting.",
                                "Beau Plan, Pamplemousses", "International, Creole",
                                "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4");

                createOwnerAndRestaurant("Sunshine Owner", "owner15@eateasy.com", "owner15",
                                "Sunshine Fusion", "A delightful mix of flavors.",
                                "Poste Lafayette", "Fusion, Creole",
                                "https://images.unsplash.com/photo-1555396273-367ea4eb4db5");

                createOwnerAndRestaurant("Boathouse Manager", "owner16@eateasy.com", "owner16",
                                "The Boathouse Grill", "Beachside seafood and grills.",
                                "Trou aux Biches", "Seafood, Grill",
                                "https://images.unsplash.com/photo-1533777857889-4be7c70b33f7");

                createOwnerAndRestaurant("Voglia Owner", "owner17@eateasy.com", "owner17",
                                "La Voglia Matta", "The real taste of Italy.",
                                "Trou aux Biches", "Italian, Pizza",
                                "https://images.unsplash.com/photo-1595854341625-f33ee10d6e7b");

                createOwnerAndRestaurant("Anna Owner", "owner18@eateasy.com", "owner18",
                                "Domaine Anna", "Exquisite Chinese and Mauritian dishes.",
                                "Flic en Flac", "Chinese-Mauritian",
                                "https://images.unsplash.com/photo-1552566626-52f8b828add9");

                createOwnerAndRestaurant("Saffron Owner", "owner19@eateasy.com", "owner19",
                                "Saffron Grill", "Spicy and savory Indian delights.",
                                "Grand Baie", "Indian-Mauritian",
                                "https://images.unsplash.com/photo-1517244683847-7456b63c5969");

                createOwnerAndRestaurant("Antonio Owner", "owner20@eateasy.com", "owner20",
                                "1974, da Antonio e Giulia", "Traditional Italian recipes.",
                                "Trou aux Biches", "Italian",
                                "https://images.unsplash.com/photo-1514933651103-005eec06c04b");

                createOwnerAndRestaurant("Pescatore Owner", "owner21@eateasy.com", "owner21",
                                "Le Pescatore", "Fine seafood dining by the sea.",
                                "Trou aux Biches", "Seafood, Fine Dining",
                                "https://images.unsplash.com/photo-1579372786545-d24232daf584");

                createOwnerAndRestaurant("Green Island Owner", "owner22@eateasy.com", "owner22",
                                "Green Island Beach", "Relaxed beach vibe with great food.",
                                "Trou d'Eau Douce", "Creole, Seafood",
                                "https://images.unsplash.com/photo-1533777857889-4be7c70b33f7");

                createOwnerAndRestaurant("Opium Owner", "owner23@eateasy.com", "owner23",
                                "Opium Restaurant", "Modern Chinese cuisine.",
                                "Reduit", "Chinese-Mauritian",
                                "https://images.unsplash.com/photo-1563245372-f21720e32c4d");

                createOwnerAndRestaurant("Tandoori Owner", "owner24@eateasy.com", "owner24",
                                "Tandoori Express", "Quick and tasty Indian bites.",
                                "Port Louis", "Indian-Mauritian, Street Food",
                                "https://images.unsplash.com/photo-1565557623262-b51c2513a641");

                createOwnerAndRestaurant("Grill Owner", "owner25@eateasy.com", "owner25",
                                "Grill & Chill", "Perfect for meat lovers.",
                                "Flic en Flac", "Street Food, Grill",
                                "https://images.unsplash.com/photo-1555939594-58d7cb561ad1");

                createOwnerAndRestaurant("Ocean Owner", "owner26@eateasy.com", "owner26",
                                "Ocean Basket", "The Mediterranean home of seafood.",
                                "Bagatelle", "Seafood",
                                "https://images.unsplash.com/photo-1498654896293-37aacf113fd9");

                createOwnerAndRestaurant("Mugg Owner", "owner27@eateasy.com", "owner27",
                                "Mugg & Bean", "More than just a coffee shop.",
                                "Bagatelle", "European, Cafe",
                                "https://images.unsplash.com/photo-1559339352-11d035aa65de");

                createOwnerAndRestaurant("Panarottis Owner", "owner28@eateasy.com", "owner28",
                                "Panarottis", "Big on Pizza, Big on Family.",
                                "Bagatelle", "Italian, Pizza",
                                "https://images.unsplash.com/photo-1574071318508-1cdbab80d002");

                createOwnerAndRestaurant("Rozi Owner", "owner29@eateasy.com", "owner29",
                                "Rozi Darbarr", "Royal Indian dining experience.",
                                "Curepipe", "Indian-Mauritian",
                                "https://images.unsplash.com/photo-1585937421612-70a008356f36");

                createOwnerAndRestaurant("Savinia Owner", "owner30@eateasy.com", "owner30",
                                "Savinia Bistrot", "Steaks and fine wines.",
                                "Bagatelle", "European, Steakhouse",
                                "https://images.unsplash.com/photo-1600891964092-4316c288032e");

                seedCoupons();
        }

        private void seedCoupons() {
                if (couponRepository.count() == 0) {
                        System.out.println("Seeding coupons...");

                        Coupon c1 = new Coupon();
                        c1.setCode("WELCOME50");
                        c1.setDiscount(50);
                        c1.setType(Coupon.DiscountType.PERCENTAGE);
                        c1.setExpiryDate(java.time.LocalDate.now().plusMonths(1));
                        c1.setMinPurchaseAmount(500);
                        couponRepository.save(c1);

                        Coupon c2 = new Coupon();
                        c2.setCode("SUPER20");
                        c2.setDiscount(20);
                        c2.setType(Coupon.DiscountType.PERCENTAGE);
                        c2.setExpiryDate(java.time.LocalDate.now().plusMonths(1));
                        c2.setMinPurchaseAmount(0); // No limit for this one
                        couponRepository.save(c2);

                        Coupon c3 = new Coupon();
                        c3.setCode("SAVE100");
                        c3.setDiscount(100);
                        c3.setType(Coupon.DiscountType.FLAT);
                        c3.setExpiryDate(java.time.LocalDate.now().plusMonths(1));
                        c3.setMinPurchaseAmount(1000);
                        couponRepository.save(c3);

                        System.out.println("Coupons seeded");
                } else {
                        // Check if WELCOME50 needs update
                        couponRepository.findByCode("WELCOME50").ifPresent(c -> {
                                if (c.getMinPurchaseAmount() == 0) {
                                        c.setMinPurchaseAmount(500);
                                        couponRepository.save(c);
                                        System.out.println("Updated WELCOME50 min purchase amount");
                                }
                        });
                }
        }

        private void createOwnerAndRestaurant(String name, String email, String password, String restaurantName,
                        String description, String address, String cuisines, String imageUrl) {
                User owner = userRepository.findByEmail(email).orElse(null);

                if (owner == null) {
                        owner = new User();
                        owner.setName(name);
                        owner.setEmail(email);
                        owner.setPhone("9876543210");
                        owner.setPassword(passwordEncoder.encode(password));
                        owner.setRole(User.Role.RESTAURANT_OWNER);
                        userRepository.save(owner);
                        System.out.println("Owner created: " + email);
                } else {

                        if (owner.getRole() != User.Role.RESTAURANT_OWNER) {
                                owner.setRole(User.Role.RESTAURANT_OWNER);
                                userRepository.save(owner);
                        }

                        owner.setPassword(passwordEncoder.encode(password));
                        userRepository.save(owner);
                        System.out.println("Owner already exists (Password reset): " + email);
                }

                Restaurant r = restaurantRepository.findByOwnerId(owner.getId()).stream().findFirst().orElse(null);
                if (r == null) {
                        r = new Restaurant();
                        r.setOwner(owner);
                        r.setName(restaurantName);
                        r.setDescription(description);
                        r.setAddress(address);
                        r.setCuisines(cuisines);
                        r.setImageUrl(imageUrl);
                        r.setStatus(Restaurant.ApprovalStatus.APPROVED);
                        r.setRating(4.5);
                        r.setOpen(true);
                        r.setOpeningHours("9 AM - 10 PM");
                        r.setPhone("9876543210");

                        restaurantRepository.save(r);
                        seedMenu(r);
                        System.out.println("Created Restaurant: " + restaurantName);
                } else {
                        // Force update for prototype transition
                        r.setName(restaurantName);
                        r.setDescription(description);
                        r.setAddress(address);
                        r.setCuisines(cuisines);
                        r.setImageUrl(imageUrl);

                        if (r.getOpeningHours() == null || r.getOpeningHours().isEmpty()) {
                                r.setOpeningHours("9 AM - 10 PM");
                        }

                        restaurantRepository.save(r);
                        seedMenu(r);
                        System.out.println("Updated existing restaurant: " + restaurantName);
                }
        }

        private void seedMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        System.out.println("Seeding menu for: " + r.getName());

                        MenuCategory breakfast = new MenuCategory();
                        breakfast.setName("Breakfast");
                        breakfast.setRestaurant(r);
                        menuCategoryRepository.save(breakfast);

                        MenuCategory lunch = new MenuCategory();
                        lunch.setName("Lunch");
                        lunch.setRestaurant(r);
                        menuCategoryRepository.save(lunch);

                        MenuCategory dinner = new MenuCategory();
                        dinner.setName("Dinner");
                        dinner.setRestaurant(r);
                        menuCategoryRepository.save(dinner);

                        MenuCategory snacks = new MenuCategory();
                        snacks.setName("Snacks");
                        snacks.setRestaurant(r);
                        menuCategoryRepository.save(snacks);

                        MenuCategory desserts = new MenuCategory();
                        desserts.setName("Dessert");
                        desserts.setRestaurant(r);
                        menuCategoryRepository.save(desserts);

                        String cuisine = r.getCuisines().toLowerCase();

                        if (cuisine.contains("creole") || cuisine.contains("seafood")) {
                                // Breakfast
                                createItem(breakfast, "Pain Maison", "Fresh bread with butter and jam", 50, true,
                                                "https://placehold.co/400x300/8B4513/FFFFFF?text=Pain+Maison");
                                createItem(breakfast, "Gateau Piment Sandwich", "Bread with chili cakes", 40, true,
                                                "https://placehold.co/400x300/FF6347/FFFFFF?text=Gateau+Piment");

                                // Lunch
                                createItem(lunch, "Fish Vindaye", "Pickled fish with mustard seeds and rice", 300,
                                                false,
                                                "https://placehold.co/400x300/4682B4/FFFFFF?text=Fish+Vindaye");
                                createItem(lunch, "Rougaille Saucisse", "Sausages in spicy tomato sauce", 250, false,
                                                "https://placehold.co/400x300/DC143C/FFFFFF?text=Rougaille+Saucisse");

                                // Dinner
                                createItem(dinner, "Grilled Fish", "Catch of the day with lemon butter sauce", 380,
                                                false,
                                                "https://placehold.co/400x300/20B2AA/FFFFFF?text=Grilled+Fish");
                                createItem(dinner, "Octopus Curry", "Tender octopus slow cooked in spices", 450, false,
                                                "https://placehold.co/400x300/8B008B/FFFFFF?text=Octopus+Curry");

                                // Snacks
                                createItem(snacks, "Samoussa", "Fried pastry with savory filling", 25, true,
                                                "https://placehold.co/400x300/DAA520/FFFFFF?text=Samoussa");
                                createItem(snacks, "Gateau Arouille", "Taro fritters", 30, true,
                                                "https://placehold.co/400x300/8B7355/FFFFFF?text=Gateau+Arouille");

                                // Dessert
                                createItem(desserts, "Banana Tart", "Shortcrust pastry with banana filling", 80, true,
                                                "https://placehold.co/400x300/FFD700/000000?text=Banana+Tart");
                                createItem(desserts, "Caramelized Pineapple", "Pineapple slices with cinnamon", 120,
                                                true,
                                                "https://placehold.co/400x300/FFA500/FFFFFF?text=Pineapple");

                        } else if (cuisine.contains("italian") || cuisine.contains("pizza")) {
                                // Breakfast
                                createItem(breakfast, "Espresso & Biscotti", "Strong coffee with almond biscuits", 120,
                                                true,
                                                "https://placehold.co/400x300/654321/FFFFFF?text=Espresso");
                                createItem(breakfast, "Frittata", "Italian style omelet with spinach", 180, true,
                                                "https://placehold.co/400x300/FFD700/000000?text=Frittata");

                                // Lunch
                                createItem(lunch, "Spaghetti Carbonara", "Pasta with creamy sauce and bacon", 280,
                                                false,
                                                "https://placehold.co/400x300/F5DEB3/000000?text=Carbonara");
                                createItem(lunch, "Caprese Salad", "Tomatoes, fresh mozzarella and basil", 220, true,
                                                "https://placehold.co/400x300/228B22/FFFFFF?text=Caprese");

                                // Dinner
                                createItem(dinner, "Marlin Pizza", "Wood-fired pizza with smoked marlin", 350, false,
                                                "https://placehold.co/400x300/FF6347/FFFFFF?text=Pizza");
                                createItem(dinner, "Lasagna", "Layered pasta with meat sauce", 320, false,
                                                "https://placehold.co/400x300/CD853F/FFFFFF?text=Lasagna");

                                // Snacks
                                createItem(snacks, "Bruschetta", "Grilled bread with tomatoes and basil", 150, true,
                                                "https://placehold.co/400x300/FF4500/FFFFFF?text=Bruschetta");
                                createItem(snacks, "Garlic Bread", "Toasted bread with garlic butter", 100, true,
                                                "https://placehold.co/400x300/D2691E/FFFFFF?text=Garlic+Bread");

                                // Dessert
                                createItem(desserts, "Panna Cotta", "Creamy Italian pudding", 150, true,
                                                "https://placehold.co/400x300/FFF8DC/000000?text=Panna+Cotta");
                                createItem(desserts, "Tiramisu", "Coffee flavored masterpiece", 200, true,
                                                "https://placehold.co/400x300/8B4513/FFFFFF?text=Tiramisu");

                        } else if (cuisine.contains("chinese-mauritian")) {
                                // Breakfast
                                createItem(breakfast, "Rice Congee", "Savory rice porridge", 150, true,
                                                "https://placehold.co/400x300/F5F5DC/000000?text=Congee");
                                createItem(breakfast, "Steamed Bao", "Fluffy buns with filling", 100, true,
                                                "https://placehold.co/400x300/FFFACD/000000?text=Bao");

                                // Lunch
                                createItem(lunch, "Mine Frire", "Mauritian fried noodles with egg and veg", 180, true,
                                                "https://placehold.co/400x300/FFD700/000000?text=Mine+Frire");
                                createItem(lunch, "Bol Renversé", "Magic bowl with rice, egg and chop suey", 250, false,
                                                "https://placehold.co/400x300/FF8C00/FFFFFF?text=Bol+Renverse");

                                // Dinner
                                createItem(dinner, "Sizzling Beef", "Beef stir-fry served on hot plate", 350, false,
                                                "https://placehold.co/400x300/8B0000/FFFFFF?text=Sizzling+Beef");
                                createItem(dinner, "Sweet and Sour Fish", "Crispy fish in tangy sauce", 320, false,
                                                "https://placehold.co/400x300/FF6347/FFFFFF?text=Sweet+Sour+Fish");

                                // Snacks
                                createItem(snacks, "Wonton Frit", "Fried dumplings", 100, false,
                                                "https://placehold.co/400x300/DAA520/FFFFFF?text=Wonton");
                                createItem(snacks, "Spring Rolls", "Crispy vegetable rolls", 80, true,
                                                "https://placehold.co/400x300/CD853F/FFFFFF?text=Spring+Rolls");

                                // Dessert
                                createItem(desserts, "Grass Jelly", "Refreshing herbal jelly", 90, true,
                                                "https://placehold.co/400x300/2F4F4F/FFFFFF?text=Grass+Jelly");
                                createItem(desserts, "Mooncake", "Dense pastry with sweet filling", 150, true,
                                                "https://placehold.co/400x300/FFD700/000000?text=Mooncake");

                        } else if (cuisine.contains("indian-mauritian")) {
                                // Breakfast
                                createItem(breakfast, "Puri Bhaji", "Fried bread with potato curry", 120, true,
                                                "https://placehold.co/400x300/FF8C00/FFFFFF?text=Puri+Bhaji");
                                createItem(breakfast, "Masala Tea", "Spiced milk tea", 50, true,
                                                "https://placehold.co/400x300/8B4513/FFFFFF?text=Masala+Tea");

                                // Lunch
                                createItem(lunch, "Chicken Briani", "Aromatic rice with chicken and spices", 280, false,
                                                "https://placehold.co/400x300/DAA520/FFFFFF?text=Briani");
                                createItem(lunch, "Dholl Puri", "Flatbread stuffed with yellow peas, served with curry",
                                                60, true,
                                                "https://placehold.co/400x300/F4A460/000000?text=Dholl+Puri");

                                // Dinner
                                createItem(dinner, "Butter Chicken", "Chicken in creamy tomato sauce", 350, false,
                                                "https://placehold.co/400x300/FF6347/FFFFFF?text=Butter+Chicken");
                                createItem(dinner, "Mutton Rogan Josh", "Slow cooked mutton curry", 400, false,
                                                "https://placehold.co/400x300/8B0000/FFFFFF?text=Rogan+Josh");

                                // Snacks
                                createItem(snacks, "Pakora", "Deep fried veggie fritters", 80, true,
                                                "https://placehold.co/400x300/CD853F/FFFFFF?text=Pakora");
                                createItem(snacks, "Chilli Cakes", "Gateau Piment", 40, true,
                                                "https://placehold.co/400x300/FF4500/FFFFFF?text=Chilli+Cakes");

                                // Dessert
                                createItem(desserts, "Alouda", "Sweet milk drink with basil seeds", 70, true,
                                                "https://placehold.co/400x300/FFB6C1/000000?text=Alouda");
                                createItem(desserts, "Rasgulla", "Soft cheese balls in syrup", 100, true,
                                                "https://placehold.co/400x300/FFFACD/000000?text=Rasgulla");

                        } else if (cuisine.contains("street food")) {
                                // Breakfast
                                createItem(breakfast, "Roti Aka", "Flatbread with curries", 50, true,
                                                "https://placehold.co/400x300/D2B48C/000000?text=Roti+Aka");
                                createItem(breakfast, "Gateau Piment", "Chili cakes", 40, true,
                                                "https://placehold.co/400x300/FF6347/FFFFFF?text=Gateau+Piment");

                                // Lunch
                                createItem(lunch, "Boulettes Soup", "Dumpling soup", 150, false,
                                                "https://placehold.co/400x300/F0E68C/000000?text=Boulettes");
                                createItem(lunch, "Mine Bouilli", "Boiled noodles with toppings", 120, false,
                                                "https://placehold.co/400x300/FFD700/000000?text=Mine+Bouilli");

                                // Dinner
                                createItem(dinner, "Halim", "Spicy wheat and meat soup", 180, false,
                                                "https://placehold.co/400x300/8B4513/FFFFFF?text=Halim");
                                createItem(dinner, "Kebab", "Grilled meat in baguette", 150, false,
                                                "https://placehold.co/400x300/A0522D/FFFFFF?text=Kebab");

                                // Snacks
                                createItem(snacks, "Baja", "Fried chickpea flour fritters", 40, true,
                                                "https://placehold.co/400x300/DAA520/FFFFFF?text=Baja");
                                createItem(snacks, "Samosa", "Veggie samosa", 25, true,
                                                "https://placehold.co/400x300/CD853F/FFFFFF?text=Samosa");

                                // Dessert
                                createItem(desserts, "Napolitaine", "Shortbread with pink icing", 50, true,
                                                "https://placehold.co/400x300/FFB6C1/000000?text=Napolitaine");
                                createItem(desserts, "Laddoo", "Sweet gram flour balls", 40, true,
                                                "https://placehold.co/400x300/FFD700/000000?text=Laddoo");

                        } else if (cuisine.contains("european")) {
                                // Breakfast
                                createItem(breakfast, "Croissant", "Buttery pastry", 80, true,
                                                "https://placehold.co/400x300/D2691E/FFFFFF?text=Croissant");
                                createItem(breakfast, "English Breakfast", "Eggs, sausages, beans, toast", 250, false,
                                                "https://placehold.co/400x300/8B4513/FFFFFF?text=English+Breakfast");

                                // Lunch
                                createItem(lunch, "Smoked Marlin Salad", "Fresh salad with smoked marlin", 280, false,
                                                "https://placehold.co/400x300/228B22/FFFFFF?text=Marlin+Salad");
                                createItem(lunch, "Club Sandwich", "Triple decker sandwich with chicken", 220, false,
                                                "https://placehold.co/400x300/F4A460/000000?text=Club+Sandwich");

                                // Dinner
                                createItem(dinner, "Steak Frites", "Grilled steak with french fries", 450, false,
                                                "https://placehold.co/400x300/8B0000/FFFFFF?text=Steak+Frites");
                                createItem(dinner, "Lobster Thermidor", "Lobster with creamy sauce", 800, false,
                                                "https://placehold.co/400x300/DC143C/FFFFFF?text=Lobster");

                                // Snacks
                                createItem(snacks, "Quiche Lorraine", "Savory tart", 150, false,
                                                "https://placehold.co/400x300/FFD700/000000?text=Quiche");
                                createItem(snacks, "Mini Burgers", "Sliders", 180, false,
                                                "https://placehold.co/400x300/8B4513/FFFFFF?text=Mini+Burgers");

                                // Dessert
                                createItem(desserts, "Crème Brûlée", "Custard with caramelized sugar", 200, true,
                                                "https://placehold.co/400x300/FFFACD/000000?text=Creme+Brulee");
                                createItem(desserts, "Chocolate Fondant", "Molten chocolate cake", 220, true,
                                                "https://placehold.co/400x300/654321/FFFFFF?text=Chocolate+Fondant");
                        } else {
                                // Default
                                createItem(breakfast, "Toast & Jam", "Bread with fruit jam", 50, true,
                                                "https://placehold.co/400x300/D2691E/FFFFFF?text=Toast+Jam");
                                createItem(lunch, "Burger", "Classic burger", 150, false,
                                                "https://placehold.co/400x300/8B4513/FFFFFF?text=Burger");
                                createItem(dinner, "Pizza", "Cheese pizza", 200, true,
                                                "https://placehold.co/400x300/FF6347/FFFFFF?text=Pizza");
                                createItem(snacks, "Fries", "Deep fried potatoes", 100, true,
                                                "https://placehold.co/400x300/DAA520/FFFFFF?text=Fries");
                                createItem(desserts, "Ice Cream", "Vanilla scoop", 80, true,
                                                "https://placehold.co/400x300/FFB6C1/000000?text=Ice+Cream");
                        }
                }
        }

        private void createItem(MenuCategory category, String name, String desc, double price, boolean veg,
                        String img) {
                MenuItem item = new MenuItem();
                item.setName(name);
                item.setDescription(desc);
                item.setPrice(price);
                item.setVeg(veg);
                item.setImageUrl(img);
                item.setCategory(category);
                item.setBestSeller(false);
                item.setOutOfStock(false);
                menuItemRepository.save(item);
        }
}
