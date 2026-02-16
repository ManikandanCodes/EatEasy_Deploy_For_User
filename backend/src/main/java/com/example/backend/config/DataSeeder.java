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

                createOwnerAndRestaurant("Le Café Owner", "owner1@eateasy.com", "owner1",
                                "Le Café du Vieux Conseil", "Charming cafe in a historic setting.",
                                "Vieux Conseil Street, Port Louis", "Creole, European",
                                "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4", 0);

                createOwnerAndRestaurant("Courtyard Owner", "owner2@eateasy.com", "owner2",
                                "The Courtyard", "Elegant dining with a fusion twist.",
                                "Port Louis Waterfront", "European, Fusion",
                                "https://images.unsplash.com/photo-1555396273-367ea4eb4db5", 0);

                createOwnerAndRestaurant("Escale Owner", "owner3@eateasy.com", "owner3",
                                "Escale Creole", "Authentic home-style Creole cooking.",
                                "Moka, Mauritius", "Creole",
                                "https://images.unsplash.com/photo-1559339352-11d035aa65de", 0);

                createOwnerAndRestaurant("Chateau Chef", "owner4@eateasy.com", "owner4",
                                "La Table du Château", "Fine dining in a colonial estate.",
                                "Labourdonnais, Mauritius", "European, Fine Dining",
                                "https://images.unsplash.com/photo-1560624052-449f5ddf0c31", 4);

                createOwnerAndRestaurant("Tante Aurius", "owner5@eateasy.com", "owner5",
                                "Chez Tante Aurius", "Famous for seafood and Creole dishes.",
                                "Trou d'Eau Douce", "Creole, Seafood",
                                "https://images.unsplash.com/photo-1585518419759-7fe2e0fbf8a6", 5);

                createOwnerAndRestaurant("Dragon Palace", "owner6@eateasy.com", "owner6",
                                "Dragon Palace", "Best Chinese-Mauritian food.",
                                "Quatre Bornes, Mauritius", "Chinese-Mauritian",
                                "https://images.unsplash.com/photo-1513639776629-7b61b0ca4909", 6);

                createOwnerAndRestaurant("Dewan Chef", "owner7@eateasy.com", "owner7",
                                "Dewan", "Famous for Briani.",
                                "Rose Hill, Mauritius", "Indian-Mauritian",
                                "https://images.unsplash.com/photo-1631452180519-c014fe946bc7", 7);

                createOwnerAndRestaurant("Ti Kouloir", "owner8@eateasy.com", "owner8",
                                "Ti Kouloir", "Best Boulettes and Mine.",
                                "Grand Baie, Mauritius", "Street Food",
                                "https://images.unsplash.com/photo-1552566626-52f8b828add9", 8);

                createOwnerAndRestaurant("Rajah Owner", "owner9@eateasy.com", "owner9",
                                "Happy Rajah", "Authentic Indian cuisine in a royal setting.",
                                "Super U, Grand Baie", "Indian-Mauritian",
                                "https://images.unsplash.com/photo-1585937421612-70a008356f36", 9);

                createOwnerAndRestaurant("Capitaine Owner", "owner10@eateasy.com", "owner10",
                                "Le Capitaine", "Fresh seafood with a view of the bay.",
                                "Royal Road, Grand Baie", "Seafood, Creole",
                                "https://images.unsplash.com/photo-1565557623262-b51c2513a641", 10);

                createOwnerAndRestaurant("Luigi Owner", "owner11@eateasy.com", "owner11",
                                "Luigi's Italian Pizzeria", "Famous wood-fired pizzas and homemade pasta.",
                                "Royal Road, North Mauritius", "Italian, Pizza",
                                "https://images.unsplash.com/photo-1579751626657-72bc17010498", 11);

                createOwnerAndRestaurant("Flamboyant Owner", "owner12@eateasy.com", "owner12",
                                "Le Flamboyant", "Authentic Mauritian Cuisine by the Sea",
                                "Royal Road, Grand Baie", "Creole, Seafood",
                                "https://images.unsplash.com/photo-1589302168068-964664d93dc0", 12);

                createOwnerAndRestaurant("Chamarel Owner", "owner13@eateasy.com", "owner13",
                                "Le Chamarel Restaurant", "Panoramic views with authentic Creole cuisine.",
                                "Chamarel, Mauritius", "Creole",
                                "https://images.unsplash.com/photo-1590846406792-0adc7f938f1d", 13);

                createOwnerAndRestaurant("Fangourin Chef", "owner14@eateasy.com", "owner14",
                                "Le Fangourin", "Refined cuisine in a historic setting.",
                                "Beau Plan, Pamplemousses", "International, Creole",
                                "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4", 14);

                createOwnerAndRestaurant("Sunshine Owner", "owner15@eateasy.com", "owner15",
                                "Sunshine Fusion", "A delightful mix of flavors.",
                                "Poste Lafayette", "Fusion, Creole",
                                "https://images.unsplash.com/photo-1555396273-367ea4eb4db5", 15);

                createOwnerAndRestaurant("Boathouse Manager", "owner16@eateasy.com", "owner16",
                                "The Boathouse Grill", "Beachside seafood and grills.",
                                "Trou aux Biches", "Seafood, Grill",
                                "https://images.unsplash.com/photo-1533777857889-4be7c70b33f7", 16);

                createOwnerAndRestaurant("Voglia Owner", "owner17@eateasy.com", "owner17",
                                "La Voglia Matta", "The real taste of Italy.",
                                "Trou aux Biches", "Italian, Pizza",
                                "https://images.unsplash.com/photo-1595854341625-f33ee10d6e7b", 17);

                createOwnerAndRestaurant("Anna Owner", "owner18@eateasy.com", "owner18",
                                "Domaine Anna", "Exquisite Chinese and Mauritian dishes.",
                                "Flic en Flac", "Chinese-Mauritian",
                                "https://images.unsplash.com/photo-1552566626-52f8b828add9", 18);

                createOwnerAndRestaurant("Saffron Owner", "owner19@eateasy.com", "owner19",
                                "Saffron Grill", "Spicy and savory Indian delights.",
                                "Grand Baie", "Indian-Mauritian",
                                "https://images.unsplash.com/photo-1517244683847-7456b63c5969", 19);

                createOwnerAndRestaurant("Antonio Owner", "owner20@eateasy.com", "owner20",
                                "1974, da Antonio e Giulia", "Traditional Italian recipes.",
                                "Trou aux Biches", "Italian",
                                "https://images.unsplash.com/photo-1514933651103-005eec06c04b", 20);

                createOwnerAndRestaurant("Pescatore Owner", "owner21@eateasy.com", "owner21",
                                "Le Pescatore", "Fine seafood dining by the sea.",
                                "Trou aux Biches", "Seafood, Fine Dining",
                                "https://images.unsplash.com/photo-1579372786545-d24232daf584", 21);

                createOwnerAndRestaurant("Green Island Owner", "owner22@eateasy.com", "owner22",
                                "Green Island Beach", "Relaxed beach vibe with great food.",
                                "Trou d'Eau Douce", "Creole, Seafood",
                                "https://images.unsplash.com/photo-1533777857889-4be7c70b33f7", 22);

                createOwnerAndRestaurant("Opium Owner", "owner23@eateasy.com", "owner23",
                                "Opium Restaurant", "Modern Chinese cuisine.",
                                "Reduit", "Chinese-Mauritian",
                                "https://images.unsplash.com/photo-1563245372-f21720e32c4d", 23);

                createOwnerAndRestaurant("Tandoori Owner", "owner24@eateasy.com", "owner24",
                                "Tandoori Express", "Quick and tasty Indian bites.",
                                "Port Louis", "Indian-Mauritian, Street Food",
                                "https://images.unsplash.com/photo-1565557623262-b51c2513a641", 24);

                createOwnerAndRestaurant("Grill Owner", "owner25@eateasy.com", "owner25",
                                "Grill & Chill", "Perfect for meat lovers.",
                                "Flic en Flac", "Street Food, Grill",
                                "https://images.unsplash.com/photo-1555939594-58d7cb561ad1", 25);

                createOwnerAndRestaurant("Ocean Owner", "owner26@eateasy.com", "owner26",
                                "Ocean Basket", "The Mediterranean home of seafood.",
                                "Bagatelle", "Seafood",
                                "https://images.unsplash.com/photo-1498654896293-37aacf113fd9", 26);

                createOwnerAndRestaurant("Mugg Owner", "owner27@eateasy.com", "owner27",
                                "Mugg & Bean", "More than just a coffee shop.",
                                "Bagatelle", "European, Cafe",
                                "https://images.unsplash.com/photo-1559339352-11d035aa65de", 27);

                createOwnerAndRestaurant("Panarottis Owner", "owner28@eateasy.com", "owner28",
                                "Panarottis", "Big on Pizza, Big on Family.",
                                "Bagatelle", "Italian, Pizza",
                                "https://images.unsplash.com/photo-1574071318508-1cdbab80d002", 28);

                createOwnerAndRestaurant("Rozi Owner", "owner29@eateasy.com", "owner29",
                                "Rozi Darbarr", "Royal Indian dining experience.",
                                "Curepipe", "Indian-Mauritian",
                                "https://images.unsplash.com/photo-1585937421612-70a008356f36", 29);

                createOwnerAndRestaurant("Savinia Owner", "owner30@eateasy.com", "owner30",
                                "Savinia Bistrot", "Steaks and fine wines.",
                                "Bagatelle", "European, Steakhouse",
                                "https://images.unsplash.com/photo-1600891964092-4316c288032e", 30);

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
                        c2.setMinPurchaseAmount(0);
                        couponRepository.save(c2);

                        Coupon c3 = new Coupon();
                        c3.setCode("SAVE100");
                        c3.setDiscount(100);
                        c3.setType(Coupon.DiscountType.FLAT);
                        c3.setExpiryDate(java.time.LocalDate.now().plusMonths(1));
                        c3.setMinPurchaseAmount(1000);
                        couponRepository.save(c3);

                        System.out.println("Coupons seeded");
                }
        }

        private void createOwnerAndRestaurant(String name, String email, String password, String restaurantName,
                        String description, String address, String cuisines, String imageUrl, int restaurantId) {
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
                        seedMenuForRestaurant(r, restaurantId);
                        System.out.println("Created Restaurant: " + restaurantName);
                } else {
                        if (r.getOpeningHours() == null || r.getOpeningHours().isEmpty()) {
                                r.setOpeningHours("9 AM - 10 PM");
                                restaurantRepository.save(r);
                        }

                        seedMenuForRestaurant(r, restaurantId);
                        System.out.println("Restaurant already exists, skipping overwrite: " + restaurantName);
                }

        }

        private void seedMenuForRestaurant(Restaurant r, int restaurantId) {
                if (restaurantId == 4)
                        seedChateauMenu(r);
                else if (restaurantId == 5)
                        seedTanteAuriusMenu(r);
                else if (restaurantId == 6)
                        seedDragonPalaceMenu(r);
                else if (restaurantId == 7)
                        seedDewanMenu(r);
                else if (restaurantId == 8)
                        seedTiKouloirMenu(r);
                else if (restaurantId == 9)
                        seedHappyRajahMenu(r);
                else if (restaurantId == 10)
                        seedLeCapitaineMenu(r);
                else if (restaurantId == 11)
                        seedLuigiMenu(r);
                else if (restaurantId == 12)
                        seedFlamboyantMenu(r);
                else if (restaurantId == 13)
                        seedChamarelMenu(r);
                else if (restaurantId == 14)
                        seedFangourinMenu(r);
                else if (restaurantId == 15)
                        seedSunshineMenu(r);
                else if (restaurantId == 16)
                        seedBoathouseMenu(r);
                else if (restaurantId == 17)
                        seedVogliaMenu(r);
                else if (restaurantId == 18)
                        seedDomaineAnnaMenu(r);
                else if (restaurantId == 19)
                        seedSaffronMenu(r);
                else if (restaurantId == 20)
                        seedAntonioMenu(r);
                else if (restaurantId == 21)
                        seedPescatoreMenu(r);
                else if (restaurantId == 22)
                        seedGreenIslandMenu(r);
                else if (restaurantId == 23)
                        seedOpiumMenu(r);
                else if (restaurantId == 24)
                        seedTandooriMenu(r);
                else if (restaurantId == 25)
                        seedGrillChillMenu(r);
                else if (restaurantId == 26)
                        seedOceanBasketMenu(r);
                else if (restaurantId == 27)
                        seedMuggBeanMenu(r);
                else if (restaurantId == 28)
                        seedPanarottisMenu(r);
                else if (restaurantId == 29)
                        seedRoziMenu(r);
                else if (restaurantId == 30)
                        seedSaviniaMenu(r);
                else
                        seedMenu(r);
        }

        private void seedChateauMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        System.out.println("Seeding unique menu for: " + r.getName());

                        MenuCategory appetizers = new MenuCategory();
                        appetizers.setName("Appetizers");
                        appetizers.setRestaurant(r);
                        menuCategoryRepository.save(appetizers);

                        createItem(appetizers, "Foie Gras Terrine", "Duck liver pâté with fig chutney", 450, false,
                                        "https://placehold.co/400x300/8B4513/FFFFFF?text=Foie+Gras");
                        createItem(appetizers, "Escargots de Bourgogne", "Snails in garlic butter", 380, false,
                                        "https://placehold.co/400x300/556B2F/FFFFFF?text=Escargots");
                        createItem(appetizers, "Smoked Marlin Carpaccio", "Thinly sliced marlin with capers", 420,
                                        false,
                                        "https://placehold.co/400x300/4682B4/FFFFFF?text=Carpaccio");
                        createItem(appetizers, "Lobster Bisque", "Creamy lobster soup", 480, false,
                                        "https://placehold.co/400x300/DC143C/FFFFFF?text=Bisque");
                        createItem(appetizers, "Truffle Mushroom Salad", "Wild mushrooms with truffle oil", 390, true,
                                        "https://placehold.co/400x300/8B7355/FFFFFF?text=Truffle+Salad");

                        MenuCategory mains = new MenuCategory();
                        mains.setName("Main Course");
                        mains.setRestaurant(r);
                        menuCategoryRepository.save(mains);

                        createItem(mains, "Beef Wellington", "Tender beef in puff pastry", 850, false,
                                        "https://placehold.co/400x300/8B0000/FFFFFF?text=Beef+Wellington");
                        createItem(mains, "Pan-Seared Sea Bass", "With saffron cream sauce", 720, false,
                                        "https://placehold.co/400x300/20B2AA/FFFFFF?text=Sea+Bass");
                        createItem(mains, "Duck à l'Orange", "Roasted duck with orange glaze", 780, false,
                                        "https://placehold.co/400x300/A0522D/FFFFFF?text=Duck+Orange");
                        createItem(mains, "Rack of Lamb", "Herb-crusted lamb with rosemary jus", 820, false,
                                        "https://placehold.co/400x300/8B4513/FFFFFF?text=Lamb+Rack");
                        createItem(mains, "Vegetarian Ratatouille", "Provençal vegetable stew", 480, true,
                                        "https://placehold.co/400x300/FF6347/FFFFFF?text=Ratatouille");
                }
        }

        private void seedTanteAuriusMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        System.out.println("Seeding unique menu for: " + r.getName());

                        MenuCategory seafood = new MenuCategory();
                        seafood.setName("Fresh Seafood");
                        seafood.setRestaurant(r);
                        menuCategoryRepository.save(seafood);

                        createItem(seafood, "Grilled Lobster", "Fresh lobster with garlic butter", 950, false,
                                        "https://placehold.co/400x300/DC143C/FFFFFF?text=Lobster");
                        createItem(seafood, "Octopus Vindaye", "Pickled octopus Mauritian style", 520, false,
                                        "https://placehold.co/400x300/8B008B/FFFFFF?text=Octopus+Vindaye");
                        createItem(seafood, "Camarons Flambés", "Flambéed prawns in rum sauce", 680, false,
                                        "https://placehold.co/400x300/FF6347/FFFFFF?text=Camarons");
                        createItem(seafood, "Fish Rougaille", "Red snapper in spicy tomato sauce", 480, false,
                                        "https://placehold.co/400x300/DC143C/FFFFFF?text=Fish+Rougaille");
                        createItem(seafood, "Crab Curry", "Blue crab in coconut curry", 580, false,
                                        "https://placehold.co/400x300/FF8C00/FFFFFF?text=Crab+Curry");

                        MenuCategory creole = new MenuCategory();
                        creole.setName("Creole Specialties");
                        creole.setRestaurant(r);
                        menuCategoryRepository.save(creole);

                        createItem(creole, "Daube de Poisson", "Fish stew with vegetables", 420, false,
                                        "https://placehold.co/400x300/4682B4/FFFFFF?text=Daube");
                        createItem(creole, "Palmiste Salad", "Heart of palm salad", 280, true,
                                        "https://placehold.co/400x300/90EE90/000000?text=Palmiste");
                        createItem(creole, "Bouillon Poisson", "Traditional fish broth", 380, false,
                                        "https://placehold.co/400x300/F0E68C/000000?text=Bouillon");
                        createItem(creole, "Cari Poulet", "Chicken curry with rice", 350, false,
                                        "https://placehold.co/400x300/DAA520/FFFFFF?text=Cari+Poulet");
                        createItem(creole, "Achards Légumes", "Pickled vegetables", 180, true,
                                        "https://placehold.co/400x300/FFD700/000000?text=Achards");
                }
        }

        private void seedDragonPalaceMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        System.out.println("Seeding unique menu for: " + r.getName());

                        MenuCategory noodles = new MenuCategory();
                        noodles.setName("Noodles & Rice");
                        noodles.setRestaurant(r);
                        menuCategoryRepository.save(noodles);

                        createItem(noodles, "Mine Frire Special", "Fried noodles with seafood", 280, false,
                                        "https://placehold.co/400x300/FFD700/000000?text=Mine+Frire");
                        createItem(noodles, "Bol Renversé Deluxe", "Magic bowl with prawns", 320, false,
                                        "https://placehold.co/400x300/FF8C00/FFFFFF?text=Bol+Renverse");
                        createItem(noodles, "Cantonese Fried Rice", "Egg fried rice with char siu", 250, false,
                                        "https://placehold.co/400x300/F4A460/000000?text=Fried+Rice");
                        createItem(noodles, "Hakka Noodles", "Spicy stir-fried noodles", 240, true,
                                        "https://placehold.co/400x300/CD853F/FFFFFF?text=Hakka+Noodles");
                        createItem(noodles, "Singapore Noodles", "Curry-flavored rice noodles", 260, false,
                                        "https://placehold.co/400x300/DAA520/FFFFFF?text=Singapore+Noodles");

                        MenuCategory mains = new MenuCategory();
                        mains.setName("Main Dishes");
                        mains.setRestaurant(r);
                        menuCategoryRepository.save(mains);

                        createItem(mains, "Peking Duck", "Crispy duck with pancakes", 780, false,
                                        "https://placehold.co/400x300/8B4513/FFFFFF?text=Peking+Duck");
                        createItem(mains, "Sweet & Sour Pork", "Crispy pork in tangy sauce", 380, false,
                                        "https://placehold.co/400x300/FF6347/FFFFFF?text=Sweet+Sour");
                        createItem(mains, "Kung Pao Chicken", "Spicy chicken with peanuts", 340, false,
                                        "https://placehold.co/400x300/8B0000/FFFFFF?text=Kung+Pao");
                        createItem(mains, "Szechuan Beef", "Spicy beef stir-fry", 420, false,
                                        "https://placehold.co/400x300/A0522D/FFFFFF?text=Szechuan+Beef");
                        createItem(mains, "Vegetable Chop Suey", "Mixed vegetables in sauce", 280, true,
                                        "https://placehold.co/400x300/228B22/FFFFFF?text=Chop+Suey");
                }
        }

        private void seedDewanMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        System.out.println("Seeding unique menu for: " + r.getName());

                        MenuCategory briani = new MenuCategory();
                        briani.setName("Briani Specialties");
                        briani.setRestaurant(r);
                        menuCategoryRepository.save(briani);

                        createItem(briani, "Chicken Briani", "Aromatic rice with tender chicken", 320, false,
                                        "https://placehold.co/400x300/DAA520/FFFFFF?text=Chicken+Briani");
                        createItem(briani, "Mutton Briani", "Slow-cooked mutton with basmati", 420, false,
                                        "https://placehold.co/400x300/8B4513/FFFFFF?text=Mutton+Briani");
                        createItem(briani, "Fish Briani", "Marlin briani with spices", 380, false,
                                        "https://placehold.co/400x300/4682B4/FFFFFF?text=Fish+Briani");
                        createItem(briani, "Prawn Briani", "Jumbo prawns in saffron rice", 480, false,
                                        "https://placehold.co/400x300/FF6347/FFFFFF?text=Prawn+Briani");
                        createItem(briani, "Vegetable Briani", "Mixed vegetables with aromatic rice", 280, true,
                                        "https://placehold.co/400x300/228B22/FFFFFF?text=Veg+Briani");

                        MenuCategory curries = new MenuCategory();
                        curries.setName("Curries");
                        curries.setRestaurant(r);
                        menuCategoryRepository.save(curries);

                        createItem(curries, "Butter Chicken", "Creamy tomato chicken curry", 380, false,
                                        "https://placehold.co/400x300/FF6347/FFFFFF?text=Butter+Chicken");
                        createItem(curries, "Rogan Josh", "Kashmiri mutton curry", 450, false,
                                        "https://placehold.co/400x300/8B0000/FFFFFF?text=Rogan+Josh");
                        createItem(curries, "Palak Paneer", "Spinach with cottage cheese", 320, true,
                                        "https://placehold.co/400x300/228B22/FFFFFF?text=Palak+Paneer");
                        createItem(curries, "Dal Makhani", "Black lentils in creamy sauce", 280, true,
                                        "https://placehold.co/400x300/8B4513/FFFFFF?text=Dal+Makhani");
                        createItem(curries, "Vindaloo Pork", "Spicy Goan pork curry", 390, false,
                                        "https://placehold.co/400x300/DC143C/FFFFFF?text=Vindaloo");
                }
        }

        private void seedTiKouloirMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        System.out.println("Seeding unique menu for: " + r.getName());

                        MenuCategory boulettes = new MenuCategory();
                        boulettes.setName("Boulettes");
                        boulettes.setRestaurant(r);
                        menuCategoryRepository.save(boulettes);

                        createItem(boulettes, "Boulettes Poisson", "Fish dumplings in broth", 180, false,
                                        "https://placehold.co/400x300/4682B4/FFFFFF?text=Boulettes+Poisson");
                        createItem(boulettes, "Boulettes Crevettes", "Prawn dumplings", 200, false,
                                        "https://placehold.co/400x300/FF6347/FFFFFF?text=Boulettes+Crevettes");
                        createItem(boulettes, "Boulettes Chouchou", "Chayote dumplings", 150, true,
                                        "https://placehold.co/400x300/90EE90/000000?text=Boulettes+Chouchou");
                        createItem(boulettes, "Boulettes Viande", "Meat dumplings", 180, false,
                                        "https://placehold.co/400x300/8B4513/FFFFFF?text=Boulettes+Viande");
                        createItem(boulettes, "Mixed Boulettes Platter", "Assorted dumplings", 280, false,
                                        "https://placehold.co/400x300/DAA520/FFFFFF?text=Mixed+Boulettes");

                        MenuCategory mine = new MenuCategory();
                        mine.setName("Mine (Noodles)");
                        mine.setRestaurant(r);
                        menuCategoryRepository.save(mine);

                        createItem(mine, "Mine Bouilli", "Boiled noodles with toppings", 150, false,
                                        "https://placehold.co/400x300/FFD700/000000?text=Mine+Bouilli");
                        createItem(mine, "Mine Frire", "Fried noodles with vegetables", 180, true,
                                        "https://placehold.co/400x300/FF8C00/FFFFFF?text=Mine+Frire");
                        createItem(mine, "Mine Soup", "Noodle soup with chicken", 160, false,
                                        "https://placehold.co/400x300/F0E68C/000000?text=Mine+Soup");
                        createItem(mine, "Mine Sauté Seafood", "Stir-fried noodles with seafood", 220, false,
                                        "https://placehold.co/400x300/DC143C/FFFFFF?text=Mine+Saute");
                        createItem(mine, "Mine Frit Légumes", "Crispy fried noodles with veggies", 170, true,
                                        "https://placehold.co/400x300/CD853F/FFFFFF?text=Mine+Frit");
                }
        }

        private void seedHappyRajahMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        System.out.println("Seeding unique menu for: " + r.getName());

                        MenuCategory main = new MenuCategory();
                        main.setName("Royal Indian");
                        main.setRestaurant(r);
                        menuCategoryRepository.save(main);

                        createItem(main, "Chicken Tikka Masala", "Grilled chicken in spicy tomato gravy", 420, false,
                                        "https://placehold.co/400x300/FF6347/FFFFFF?text=Tikka+Masala");
                        createItem(main, "Lamb Rogan Josh", "Kashmiri style lamb curry", 480, false,
                                        "https://placehold.co/400x300/8B0000/FFFFFF?text=Lamb+Rogan");
                        createItem(main, "Dal Fry", "Yellow lentils tempered with garlic", 280, true,
                                        "https://placehold.co/400x300/FFD700/000000?text=Dal+Fry");
                        createItem(main, "Mutton Biryani", "Fragrant rice with slow cooked mutton", 450, false,
                                        "https://placehold.co/400x300/A0522D/FFFFFF?text=Mutton+Biryani");
                        createItem(main, "Garlic Naan", "Soft bread with garlic butter", 60, true,
                                        "https://placehold.co/400x300/F4A460/000000?text=Garlic+Naan");
                }
        }

        private void seedLeCapitaineMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        System.out.println("Seeding unique menu for: " + r.getName());

                        MenuCategory seafood = new MenuCategory();
                        seafood.setName("Ocean's Best");
                        seafood.setRestaurant(r);
                        menuCategoryRepository.save(seafood);

                        createItem(seafood, "Grilled Dorado Fillet", "With lemon butter sauce", 520, false,
                                        "https://placehold.co/400x300/20B2AA/FFFFFF?text=Dorado");
                        createItem(seafood, "Seafood Platter", "Assorted seafood grilled to perfection", 1200, false,
                                        "https://placehold.co/400x300/1E90FF/FFFFFF?text=Seafood+Platter");
                        createItem(seafood, "Calamari Rings", "Crispy fried calamari", 380, false,
                                        "https://placehold.co/400x300/F0E68C/000000?text=Calamari");
                        createItem(seafood, "Crab Soup", "Rich and creamy crab bisque", 320, false,
                                        "https://placehold.co/400x300/DC143C/FFFFFF?text=Crab+Soup");
                        createItem(seafood, "Prawn Curry", "Spicy prawn curry with rice", 550, false,
                                        "https://placehold.co/400x300/FFA500/FFFFFF?text=Prawn+Curry");
                }
        }

        private void seedLuigiMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        System.out.println("Seeding unique menu for: " + r.getName());

                        MenuCategory pizza = new MenuCategory();
                        pizza.setName("Wood Fired Pizza");
                        pizza.setRestaurant(r);
                        menuCategoryRepository.save(pizza);

                        createItem(pizza, "Pizza Margherita", "Tomato, mozzarella, basil", 280, true,
                                        "https://placehold.co/400x300/FF6347/FFFFFF?text=Margherita");
                        createItem(pizza, "Pizza Diavola", "Spicy salami and chili oil", 350, false,
                                        "https://placehold.co/400x300/8B0000/FFFFFF?text=Diavola");
                        createItem(pizza, "Pizza Quattro Formaggi", "Four cheese delight", 380, true,
                                        "https://placehold.co/400x300/FFD700/000000?text=4+Cheese");
                        createItem(pizza, "Pasta Carbonara", "Authentic style with guanciale", 320, false,
                                        "https://placehold.co/400x300/F5DEB3/000000?text=Carbonara");
                        createItem(pizza, "Tiramisu", "Classic coffee dessert", 200, true,
                                        "https://placehold.co/400x300/8B4513/FFFFFF?text=Tiramisu");
                }
        }

        private void seedFlamboyantMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        System.out.println("Seeding unique menu for: " + r.getName());

                        MenuCategory creole = new MenuCategory();
                        creole.setName("Mauritian Classics");
                        creole.setRestaurant(r);
                        menuCategoryRepository.save(creole);

                        createItem(creole, "Chicken Curry", "With eggplant and potatoes", 250, false,
                                        "https://placehold.co/400x300/FF8C00/FFFFFF?text=Chicken+Curry");
                        createItem(creole, "Fish Vindaye", "Pickled fish with turmeric", 300, false,
                                        "https://placehold.co/400x300/FFD700/000000?text=Vindaye");
                        createItem(creole, "Rougaille Saucisse", "Sausages in tomato sauce", 220, false,
                                        "https://placehold.co/400x300/DC143C/FFFFFF?text=Rougaille");
                        createItem(creole, "Bol Renverse", "Upside down bowl", 280, false,
                                        "https://placehold.co/400x300/CD853F/FFFFFF?text=Bol+Renverse");
                        createItem(creole, "Gateau Piment", "Chili cakes", 50, true,
                                        "https://placehold.co/400x300/32CD32/FFFFFF?text=Gateau+Piment");
                }
        }

        private void seedChamarelMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        System.out.println("Seeding unique menu for: " + r.getName());

                        MenuCategory lunch = new MenuCategory();
                        lunch.setName("Lunch with a View");
                        lunch.setRestaurant(r);
                        menuCategoryRepository.save(lunch);

                        createItem(lunch, "Venison Curry", "Local deer meat curry", 550, false,
                                        "https://placehold.co/400x300/8B4513/FFFFFF?text=Venison");
                        createItem(lunch, "Wild Boar Salmis", "Braised wild boar", 580, false,
                                        "https://placehold.co/400x300/A0522D/FFFFFF?text=Wild+Boar");
                        createItem(lunch, "Palm Heart Salad", "Fresh palm heart with dressing", 350, true,
                                        "https://placehold.co/400x300/F0FFF0/000000?text=Palm+Heart");
                        createItem(lunch, "Rum Baba", "Cake soaked in Chamarel rum", 250, true,
                                        "https://placehold.co/400x300/DAA520/FFFFFF?text=Rum+Baba");
                        createItem(lunch, "Coffee of Chamarel", "Locally grown coffee", 120, true,
                                        "https://placehold.co/400x300/654321/FFFFFF?text=Coffee");
                }
        }

        private void seedFangourinMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        System.out.println("Seeding unique menu for: " + r.getName());

                        MenuCategory gourmet = new MenuCategory();
                        gourmet.setName("Gourmet Selection");
                        gourmet.setRestaurant(r);
                        menuCategoryRepository.save(gourmet);

                        createItem(gourmet, "Smoked Marlin Salad", "With citrus dressing", 380, false,
                                        "https://placehold.co/400x300/FA8072/FFFFFF?text=Marlin");
                        createItem(gourmet, "Duck Magret", "With honey sauce", 650, false,
                                        "https://placehold.co/400x300/8B0000/FFFFFF?text=Duck");
                        createItem(gourmet, "Scallops Risotto", "Creamy risotto with scallops", 720, false,
                                        "https://placehold.co/400x300/FFF8DC/000000?text=Scallops");
                        createItem(gourmet, "Molten Chocolate Cake", "With vanilla ice cream", 280, true,
                                        "https://placehold.co/400x300/000000/FFFFFF?text=Choco+Cake");
                        createItem(gourmet, "Fruit Carpaccio", "Seasonal fruits thinly sliced", 220, true,
                                        "https://placehold.co/400x300/00FF00/000000?text=Fruit");
                }
        }

        private void seedSunshineMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        System.out.println("Seeding unique menu for: " + r.getName());

                        MenuCategory fusion = new MenuCategory();
                        fusion.setName("Fusion Delights");
                        fusion.setRestaurant(r);
                        menuCategoryRepository.save(fusion);

                        createItem(fusion, "Chicken Satay Burger", "Burger with peanut sauce", 280, false,
                                        "https://placehold.co/400x300/DAA520/FFFFFF?text=Satay+Burger");
                        createItem(fusion, "Tuna Wrap", "Grilled tuna with avocado", 250, false,
                                        "https://placehold.co/400x300/000080/FFFFFF?text=Tuna+Wrap");
                        createItem(fusion, "Spicy Pasta", "Pasta with arrabbiata sauce", 220, true,
                                        "https://placehold.co/400x300/FF0000/FFFFFF?text=Spicy+Pasta");
                        createItem(fusion, "Mango Smoothie", "Fresh mango blend", 150, true,
                                        "https://placehold.co/400x300/FFD700/000000?text=Mango");
                        createItem(fusion, "Cheesecake", "New York style", 180, true,
                                        "https://placehold.co/400x300/FFFDD0/000000?text=Cheesecake");
                }
        }

        private void seedBoathouseMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        System.out.println("Seeding unique menu for: " + r.getName());

                        MenuCategory grill = new MenuCategory();
                        grill.setName("From the Grill");
                        grill.setRestaurant(r);
                        menuCategoryRepository.save(grill);

                        createItem(grill, "Grilled Red Snapper", "Whole fish with herbs", 580, false,
                                        "https://placehold.co/400x300/FF6347/FFFFFF?text=Red+Snapper");
                        createItem(grill, "BBQ Ribs", "Pork ribs with sticky sauce", 450, false,
                                        "https://placehold.co/400x300/8B4513/FFFFFF?text=Ribs");
                        createItem(grill, "Prawn Skewers", "Grilled prawns with lemon", 420, false,
                                        "https://placehold.co/400x300/FFA07A/FFFFFF?text=Prawns");
                        createItem(grill, "Surf and Turf", "Steak and prawns combo", 850, false,
                                        "https://placehold.co/400x300/A52A2A/FFFFFF?text=Surf+Turf");
                        createItem(grill, "Corn on Cob", "Grilled sweet corn", 120, true,
                                        "https://placehold.co/400x300/FFFF00/000000?text=Corn");
                }
        }

        private void seedVogliaMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        System.out.println("Seeding unique menu for: " + r.getName());

                        MenuCategory pizza = new MenuCategory();
                        pizza.setName("Giant Pizza");
                        pizza.setRestaurant(r);
                        menuCategoryRepository.save(pizza);

                        createItem(pizza, "Pizza Family Size", "Huge margherita for sharing", 800, true,
                                        "https://placehold.co/400x300/FF4500/FFFFFF?text=Family+Pizza");
                        createItem(pizza, "Pizza Prosciutto", "Ham and mushroom", 350, false,
                                        "https://placehold.co/400x300/A0522D/FFFFFF?text=Prosciutto");
                        createItem(pizza, "Gnocchi Gorgonzola", "Potato dumplings in cheese sauce", 320, true,
                                        "https://placehold.co/400x300/FFF8DC/000000?text=Gnocchi");
                        createItem(pizza, "Cannoli", "Ricotta filled pastry", 150, true,
                                        "https://placehold.co/400x300/F5DEB3/000000?text=Cannoli");
                        createItem(pizza, "Limoncello", "Lemon liqueur digestif", 180, true,
                                        "https://placehold.co/400x300/FFFF00/000000?text=Limoncello");
                }
        }

        private void seedDomaineAnnaMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        System.out.println("Seeding unique menu for: " + r.getName());

                        MenuCategory exclusive = new MenuCategory();
                        exclusive.setName("Chef's Specials");
                        exclusive.setRestaurant(r);
                        menuCategoryRepository.save(exclusive);

                        createItem(exclusive, "Whole Steamed Fish", "With ginger and soy sauce", 650, false,
                                        "https://placehold.co/400x300/C0C0C0/000000?text=Steamed+Fish");
                        createItem(exclusive, "Salt and Pepper Squid", "Crispy squid chunks", 320, false,
                                        "https://placehold.co/400x300/F5F5DC/000000?text=Squid");
                        createItem(exclusive, "Sizzling Lamb", "Lamb with black bean sauce", 480, false,
                                        "https://placehold.co/400x300/8B0000/FFFFFF?text=Sizzling+Lamb");
                        createItem(exclusive, "Seafood Fried Rice", "Luxurious fried rice", 350, false,
                                        "https://placehold.co/400x300/DEB887/000000?text=Seafood+Rice");
                        createItem(exclusive, "Lychee Sorbet", "Refreshing dessert", 150, true,
                                        "https://placehold.co/400x300/FFB6C1/000000?text=Lychee");
                }
        }

        private void seedSaffronMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        System.out.println("Seeding unique menu for: " + r.getName());

                        MenuCategory grill = new MenuCategory();
                        grill.setName("Tandoor & Grill");
                        grill.setRestaurant(r);
                        menuCategoryRepository.save(grill);

                        createItem(grill, "Seekh Kebab", "Minced lamb skewers", 280, false,
                                        "https://placehold.co/400x300/8B4513/FFFFFF?text=Seekh+Kebab");
                        createItem(grill, "Paneer Tikka", "Grilled cottage cheese", 250, true,
                                        "https://placehold.co/400x300/FFFFE0/000000?text=Paneer+Tikka");
                        createItem(grill, "Tandoori Chicken", "Half chicken marinated in yogurt", 350, false,
                                        "https://placehold.co/400x300/FF4500/FFFFFF?text=Tandoori");
                        createItem(grill, "Naan Basket", "Assorted indian breads", 180, true,
                                        "https://placehold.co/400x300/F4A460/000000?text=Naan");
                        createItem(grill, "Kulfi", "Indian ice cream", 120, true,
                                        "https://placehold.co/400x300/FFFACD/000000?text=Kulfi");
                }
        }

        private void seedAntonioMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        System.out.println("Seeding unique menu for: " + r.getName());

                        MenuCategory traditional = new MenuCategory();
                        traditional.setName("Traditional Recipes");
                        traditional.setRestaurant(r);
                        menuCategoryRepository.save(traditional);

                        createItem(traditional, "Osso Buco", "Braised veal shanks", 580, false,
                                        "https://placehold.co/400x300/8B0000/FFFFFF?text=Osso+Buco");
                        createItem(traditional, "Risotto ai Funghi", "Mushroom rice", 350, true,
                                        "https://placehold.co/400x300/F5DEB3/000000?text=Risotto");
                        createItem(traditional, "Spaghetti Vongole", "Clams pasta", 420, false,
                                        "https://placehold.co/400x300/FFDEAD/000000?text=Vongole");
                        createItem(traditional, "Minestrone Soup", "Vegetable soup", 180, true,
                                        "https://placehold.co/400x300/228B22/FFFFFF?text=Minestrone");
                        createItem(traditional, "Zabaglione", "Custard dessert", 200, true,
                                        "https://placehold.co/400x300/FFFFE0/000000?text=Zabaglione");
                }
        }

        private void seedPescatoreMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        MenuCategory fine = new MenuCategory();
                        fine.setName("Exquisite Seafood");
                        fine.setRestaurant(r);
                        menuCategoryRepository.save(fine);

                        createItem(fine, "Oysters platter", "Fresh oysters", 800, false,
                                        "https://placehold.co/400x300/8B0000/FFFFFF?text=Oysters");
                        createItem(fine, "Bouillabaisse", "Traditional fish soup", 650, false,
                                        "https://placehold.co/400x300/FF7F50/FFFFFF?text=Bouillabaisse");
                        createItem(fine, "Seared Scallops", "With truffle oil", 720, false,
                                        "https://placehold.co/400x300/F5F5DC/000000?text=Scallops");
                        createItem(fine, "Crab Ravioli", "Homemade pasta", 450, false,
                                        "https://placehold.co/400x300/FFFFE0/000000?text=Ravioli");
                        createItem(fine, "Lemon Sorbet", "Palate cleanser", 120, true,
                                        "https://placehold.co/400x300/FFFF00/000000?text=Sorbet");
                }
        }

        private void seedGreenIslandMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        MenuCategory beach = new MenuCategory();
                        beach.setName("Beachside Grill");
                        beach.setRestaurant(r);
                        menuCategoryRepository.save(beach);

                        createItem(beach, "Catch of the Day", "Grilled fresh fish", 450, false,
                                        "https://placehold.co/400x300/00BFFF/FFFFFF?text=Fish");
                        createItem(beach, "Calamari Fritti", "Fried squid rings", 280, false,
                                        "https://placehold.co/400x300/F4A460/000000?text=Calamari");
                        createItem(beach, "Chicken Sandwich", "Served with fries", 220, false,
                                        "https://placehold.co/400x300/D2691E/FFFFFF?text=Sandwich");
                        createItem(beach, "Tropical Salad", "Pineapple and mango salad", 180, true,
                                        "https://placehold.co/400x300/32CD32/FFFFFF?text=Salad");
                        createItem(beach, "Coconut Ice Cream", "Served int a coconut", 150, true,
                                        "https://placehold.co/400x300/F0F8FF/000000?text=Coconut");
                }
        }

        private void seedOpiumMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        MenuCategory modern = new MenuCategory();
                        modern.setName("Modern Chinese");
                        modern.setRestaurant(r);
                        menuCategoryRepository.save(modern);

                        createItem(modern, "Dim Sum Platter", "Assorted steamed dumplings", 420, false,
                                        "https://placehold.co/400x300/F5DEB3/000000?text=Dim+Sum");
                        createItem(modern, "Crispy duck Salad", "Duck with hoisin dressing", 350, false,
                                        "https://placehold.co/400x300/8B4513/FFFFFF?text=Duck+Salad");
                        createItem(modern, "General Tso Chicken", "Sweet and spicy chicken", 380, false,
                                        "https://placehold.co/400x300/FF0000/FFFFFF?text=General+Tso");
                        createItem(modern, "Beef Black Bean", "Stir fry beef", 400, false,
                                        "https://placehold.co/400x300/8B0000/FFFFFF?text=Beef");
                        createItem(modern, "Mango Pudding", "Creamy dessert", 120, true,
                                        "https://placehold.co/400x300/FFD700/000000?text=Mango+Pudding");
                }
        }

        private void seedTandooriMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        MenuCategory quick = new MenuCategory();
                        quick.setName("Express Indian");
                        quick.setRestaurant(r);
                        menuCategoryRepository.save(quick);

                        createItem(quick, "Chicken Roll", "Kebab wrapped in naan", 150, false,
                                        "https://placehold.co/400x300/D2691E/FFFFFF?text=Chicken+Roll");
                        createItem(quick, "Samosa Chaat", "Samosa with chutney", 120, true,
                                        "https://placehold.co/400x300/DAA520/FFFFFF?text=Chaat");
                        createItem(quick, "Biryani Box", "Compact meal", 200, false,
                                        "https://placehold.co/400x300/FF8C00/FFFFFF?text=Biryani+Box");
                        createItem(quick, "Lassi", "Yogurt drink", 80, true,
                                        "https://placehold.co/400x300/FFFFFF/000000?text=Lassi");
                        createItem(quick, "Gulab Jamun", "Sweet dumplings", 50, true,
                                        "https://placehold.co/400x300/8B0000/FFFFFF?text=Gulab+Jamun");
                }
        }

        private void seedGrillChillMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        MenuCategory grill = new MenuCategory();
                        grill.setName("BBQ Favorites");
                        grill.setRestaurant(r);
                        menuCategoryRepository.save(grill);

                        createItem(grill, "Mixed Grill", "Chicken, lamb sausages", 550, false,
                                        "https://placehold.co/400x300/8B0000/FFFFFF?text=Mix+Grill");
                        createItem(grill, "Lamb Chops", "Grilled with mint sauce", 480, false,
                                        "https://placehold.co/400x300/A0522D/FFFFFF?text=Lamb+Chops");
                        createItem(grill, "Grilled Veggies", "Skewered vegetables", 220, true,
                                        "https://placehold.co/400x300/008000/FFFFFF?text=Veggies");
                        createItem(grill, "Garlic Bread", "Toasted baguette", 80, true,
                                        "https://placehold.co/400x300/F4A460/000000?text=Garlic+Bread");
                        createItem(grill, "Brownie", "Chocolate nut brownie", 120, true,
                                        "https://placehold.co/400x300/8B4513/FFFFFF?text=Brownie");
                }
        }

        private void seedOceanBasketMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        MenuCategory seafood = new MenuCategory();
                        seafood.setName("Mediterranean Seafood");
                        seafood.setRestaurant(r);
                        menuCategoryRepository.save(seafood);

                        createItem(seafood, "Fish and Chips", "Hake fillet with chips", 280, false,
                                        "https://placehold.co/400x300/FFDEAD/000000?text=Fish+Chips");
                        createItem(seafood, "Platter for Two", "Prawns, calamari, fish", 850, false,
                                        "https://placehold.co/400x300/ADD8E6/000000?text=Platter");
                        createItem(seafood, "Mussels", "In lemon garlic sauce", 260, false,
                                        "https://placehold.co/400x300/000080/FFFFFF?text=Mussels");
                        createItem(seafood, "Sushi Platter", "Assorted sushi", 450, false,
                                        "https://placehold.co/400x300/FFB6C1/000000?text=Sushi");
                        createItem(seafood, "Baklava", "Pastry with nuts", 120, true,
                                        "https://placehold.co/400x300/DAA520/FFFFFF?text=Baklava");
                }
        }

        private void seedMuggBeanMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        MenuCategory cafe = new MenuCategory();
                        cafe.setName("Cafe Delights");
                        cafe.setRestaurant(r);
                        menuCategoryRepository.save(cafe);

                        createItem(cafe, "Giant Muffin", "Blueberry or chocolate", 120, true,
                                        "https://placehold.co/400x300/8B4513/FFFFFF?text=Muffin");
                        createItem(cafe, "Eggs Benedict", "Poached eggs on toast", 280, false,
                                        "https://placehold.co/400x300/FFFF00/000000?text=Benedict");
                        createItem(cafe, "Chicken Mayo Sandwich", "Toasted sandwich", 180, false,
                                        "https://placehold.co/400x300/F5DEB3/000000?text=Mayo+Sandwich");
                        createItem(cafe, "Caesar Salad", "Chicken and cos lettuce", 250, false,
                                        "https://placehold.co/400x300/90EE90/000000?text=Caesar");
                        createItem(cafe, "Cappuccino", "Frothy coffee", 90, true,
                                        "https://placehold.co/400x300/A0522D/FFFFFF?text=Cappuccino");
                }
        }

        private void seedPanarottisMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        MenuCategory pizza = new MenuCategory();
                        pizza.setName("Family Pizza & Pasta");
                        pizza.setRestaurant(r);
                        menuCategoryRepository.save(pizza);

                        createItem(pizza, "Monster Pizza", "Large pizza with 3 toppings", 550, false,
                                        "https://placehold.co/400x300/FF4500/FFFFFF?text=Monster+Pizza");
                        createItem(pizza, "Alfredo Pasta", "Creamy mushroom sauce", 280, true,
                                        "https://placehold.co/400x300/F0E68C/000000?text=Alfredo");
                        createItem(pizza, "Carnivore Pizza", "Loaded meat pizza", 420, false,
                                        "https://placehold.co/400x300/8B0000/FFFFFF?text=Carnivore");
                        createItem(pizza, "Mexican Pizza", "Mince and chili", 380, false,
                                        "https://placehold.co/400x300/FF0000/FFFFFF?text=Mexican");
                        createItem(pizza, "Waffle", "With ice cream", 150, true,
                                        "https://placehold.co/400x300/DEB887/000000?text=Waffle");
                }
        }

        private void seedRoziMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        MenuCategory royal = new MenuCategory();
                        royal.setName("Royal Indian");
                        royal.setRestaurant(r);
                        menuCategoryRepository.save(royal);

                        createItem(royal, "Shahi Paneer", "Paneer in cashew gravy", 350, true,
                                        "https://placehold.co/400x300/FFFFE0/000000?text=Shahi+Paneer");
                        createItem(royal, "Murgh Malai", "Creamy chicken kebab", 380, false,
                                        "https://placehold.co/400x300/FFDAB9/000000?text=Murgh");
                        createItem(royal, "Hyderabadi Biryani", "Spicy lamb biryani", 450, false,
                                        "https://placehold.co/400x300/D2691E/FFFFFF?text=Biryani");
                        createItem(royal, "Roomali Roti", "Thin soft bread", 60, true,
                                        "https://placehold.co/400x300/F5DEB3/000000?text=Roti");
                        createItem(royal, "Gajar Halwa", "Carrot pudding", 120, true,
                                        "https://placehold.co/400x300/FF4500/FFFFFF?text=Halwa");
                }
        }

        private void seedSaviniaMenu(Restaurant r) {
                if (menuCategoryRepository.findByRestaurant(r).isEmpty()) {
                        MenuCategory steak = new MenuCategory();
                        steak.setName("Steakhouse Cuts");
                        steak.setRestaurant(r);
                        menuCategoryRepository.save(steak);

                        createItem(steak, "Ribeye Steak", "300g premium cut", 850, false,
                                        "https://placehold.co/400x300/8B0000/FFFFFF?text=Ribeye");
                        createItem(steak, "T-Bone Steak", "The best of both worlds", 950, false,
                                        "https://placehold.co/400x300/A52A2A/FFFFFF?text=T-Bone");
                        createItem(steak, "Grilled Salmon", "With asparagus", 750, false,
                                        "https://placehold.co/400x300/FA8072/FFFFFF?text=Salmon");
                        createItem(steak, "Onion Soup", "French style gratinated", 220, false,
                                        "https://placehold.co/400x300/D2691E/FFFFFF?text=Onion+Soup");
                        createItem(steak, "Red Velvet Cake", "Classic layer cake", 200, true,
                                        "https://placehold.co/400x300/DC143C/FFFFFF?text=Red+Velvet");
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
                                createItem(breakfast, "Pain Maison", "Fresh bread with butter and jam", 50, true,
                                                "https://placehold.co/400x300/8B4513/FFFFFF?text=Pain+Maison");
                                createItem(breakfast, "Gateau Piment Sandwich", "Bread with chili cakes", 40, true,
                                                "https://placehold.co/400x300/FF6347/FFFFFF?text=Gateau+Piment");

                                createItem(lunch, "Fish Vindaye", "Pickled fish with mustard seeds and rice", 300,
                                                false,
                                                "https://placehold.co/400x300/4682B4/FFFFFF?text=Fish+Vindaye");
                                createItem(lunch, "Rougaille Saucisse", "Sausages in spicy tomato sauce", 250, false,
                                                "https://placehold.co/400x300/DC143C/FFFFFF?text=Rougaille+Saucisse");

                                createItem(dinner, "Grilled Fish", "Catch of the day with lemon butter sauce", 380,
                                                false,
                                                "https://placehold.co/400x300/20B2AA/FFFFFF?text=Grilled+Fish");
                                createItem(dinner, "Octopus Curry", "Tender octopus slow cooked in spices", 450, false,
                                                "https://placehold.co/400x300/8B008B/FFFFFF?text=Octopus+Curry");

                                createItem(snacks, "Samoussa", "Fried pastry with savory filling", 25, true,
                                                "https://placehold.co/400x300/DAA520/FFFFFF?text=Samoussa");
                                createItem(snacks, "Gateau Arouille", "Taro fritters", 30, true,
                                                "https://placehold.co/400x300/8B7355/FFFFFF?text=Gateau+Arouille");

                                createItem(desserts, "Banana Tart", "Shortcrust pastry with banana filling", 80, true,
                                                "https://placehold.co/400x300/FFD700/000000?text=Banana+Tart");
                                createItem(desserts, "Caramelized Pineapple", "Pineapple slices with cinnamon", 120,
                                                true,
                                                "https://placehold.co/400x300/FFA500/FFFFFF?text=Pineapple");

                        } else if (cuisine.contains("italian") || cuisine.contains("pizza")) {
                                createItem(breakfast, "Espresso & Biscotti", "Strong coffee with almond biscuits", 120,
                                                true,
                                                "https://placehold.co/400x300/654321/FFFFFF?text=Espresso");
                                createItem(breakfast, "Frittata", "Italian style omelet with spinach", 180, true,
                                                "https://placehold.co/400x300/FFD700/000000?text=Frittata");

                                createItem(lunch, "Spaghetti Carbonara", "Pasta with creamy sauce and bacon", 280,
                                                false,
                                                "https://placehold.co/400x300/F5DEB3/000000?text=Carbonara");
                                createItem(lunch, "Caprese Salad", "Tomatoes, fresh mozzarella and basil", 220, true,
                                                "https://placehold.co/400x300/228B22/FFFFFF?text=Caprese");

                                createItem(dinner, "Marlin Pizza", "Wood-fired pizza with smoked marlin", 350, false,
                                                "https://placehold.co/400x300/FF6347/FFFFFF?text=Pizza");
                                createItem(dinner, "Lasagna", "Layered pasta with meat sauce", 320, false,
                                                "https://placehold.co/400x300/CD853F/FFFFFF?text=Lasagna");

                                createItem(snacks, "Bruschetta", "Grilled bread with tomatoes and basil", 150, true,
                                                "https://placehold.co/400x300/FF4500/FFFFFF?text=Bruschetta");
                                createItem(snacks, "Garlic Bread", "Toasted bread with garlic butter", 100, true,
                                                "https://placehold.co/400x300/D2691E/FFFFFF?text=Garlic+Bread");

                                createItem(desserts, "Panna Cotta", "Creamy Italian pudding", 150, true,
                                                "https://placehold.co/400x300/FFF8DC/000000?text=Panna+Cotta");
                                createItem(desserts, "Tiramisu", "Coffee flavored masterpiece", 200, true,
                                                "https://placehold.co/400x300/8B4513/FFFFFF?text=Tiramisu");

                        } else if (cuisine.contains("chinese-mauritian")) {
                                createItem(breakfast, "Rice Congee", "Savory rice porridge", 150, true,
                                                "https://placehold.co/400x300/F5F5DC/000000?text=Congee");
                                createItem(breakfast, "Steamed Bao", "Fluffy buns with filling", 100, true,
                                                "https://placehold.co/400x300/FFFACD/000000?text=Bao");

                                createItem(lunch, "Mine Frire", "Mauritian fried noodles with egg and veg", 180, true,
                                                "https://placehold.co/400x300/FFD700/000000?text=Mine+Frire");
                                createItem(lunch, "Bol Renversé", "Magic bowl with rice, egg and chop suey", 250, false,
                                                "https://placehold.co/400x300/FF8C00/FFFFFF?text=Bol+Renverse");

                                createItem(dinner, "Sizzling Beef", "Beef stir-fry served on hot plate", 350, false,
                                                "https://placehold.co/400x300/8B0000/FFFFFF?text=Sizzling+Beef");
                                createItem(dinner, "Sweet and Sour Fish", "Crispy fish in tangy sauce", 320, false,
                                                "https://placehold.co/400x300/FF6347/FFFFFF?text=Sweet+Sour+Fish");

                                createItem(snacks, "Wonton Frit", "Fried dumplings", 100, false,
                                                "https://placehold.co/400x300/DAA520/FFFFFF?text=Wonton");
                                createItem(snacks, "Spring Rolls", "Crispy vegetable rolls", 80, true,
                                                "https://placehold.co/400x300/CD853F/FFFFFF?text=Spring+Rolls");

                                createItem(desserts, "Grass Jelly", "Refreshing herbal jelly", 90, true,
                                                "https://placehold.co/400x300/2F4F4F/FFFFFF?text=Grass+Jelly");
                                createItem(desserts, "Mooncake", "Dense pastry with sweet filling", 150, true,
                                                "https://placehold.co/400x300/FFD700/000000?text=Mooncake");

                        } else if (cuisine.contains("indian-mauritian")) {
                                createItem(breakfast, "Puri Bhaji", "Fried bread with potato curry", 120, true,
                                                "https://placehold.co/400x300/FF8C00/FFFFFF?text=Puri+Bhaji");
                                createItem(breakfast, "Masala Tea", "Spiced milk tea", 50, true,
                                                "https://placehold.co/400x300/8B4513/FFFFFF?text=Masala+Tea");

                                createItem(lunch, "Chicken Briani", "Aromatic rice with chicken and spices", 280, false,
                                                "https://placehold.co/400x300/DAA520/FFFFFF?text=Briani");
                                createItem(lunch, "Dholl Puri", "Flatbread stuffed with yellow peas, served with curry",
                                                60, true,
                                                "https://placehold.co/400x300/F4A460/000000?text=Dholl+Puri");

                                createItem(dinner, "Butter Chicken", "Chicken in creamy tomato sauce", 350, false,
                                                "https://placehold.co/400x300/FF6347/FFFFFF?text=Butter+Chicken");
                                createItem(dinner, "Mutton Rogan Josh", "Slow cooked mutton curry", 400, false,
                                                "https://placehold.co/400x300/8B0000/FFFFFF?text=Rogan+Josh");

                                createItem(snacks, "Pakora", "Deep fried veggie fritters", 80, true,
                                                "https://placehold.co/400x300/CD853F/FFFFFF?text=Pakora");
                                createItem(snacks, "Chilli Cakes", "Gateau Piment", 40, true,
                                                "https://placehold.co/400x300/FF4500/FFFFFF?text=Chilli+Cakes");

                                createItem(desserts, "Alouda", "Sweet milk drink with basil seeds", 70, true,
                                                "https://placehold.co/400x300/FFB6C1/000000?text=Alouda");
                                createItem(desserts, "Rasgulla", "Soft cheese balls in syrup", 100, true,
                                                "https://placehold.co/400x300/FFFACD/000000?text=Rasgulla");

                        } else if (cuisine.contains("street food")) {
                                createItem(breakfast, "Roti Aka", "Flatbread with curries", 50, true,
                                                "https://placehold.co/400x300/D2B48C/000000?text=Roti+Aka");
                                createItem(breakfast, "Gateau Piment", "Chili cakes", 40, true,
                                                "https://placehold.co/400x300/FF6347/FFFFFF?text=Gateau+Piment");

                                createItem(lunch, "Boulettes Soup", "Dumpling soup", 150, false,
                                                "https://placehold.co/400x300/F0E68C/000000?text=Boulettes");
                                createItem(lunch, "Mine Bouilli", "Boiled noodles with toppings", 120, false,
                                                "https://placehold.co/400x300/FFD700/000000?text=Mine+Bouilli");

                                createItem(dinner, "Halim", "Spicy wheat and meat soup", 180, false,
                                                "https://placehold.co/400x300/8B4513/FFFFFF?text=Halim");
                                createItem(dinner, "Kebab", "Grilled meat in baguette", 150, false,
                                                "https://placehold.co/400x300/A0522D/FFFFFF?text=Kebab");

                                createItem(snacks, "Baja", "Fried chickpea flour fritters", 40, true,
                                                "https://placehold.co/400x300/DAA520/FFFFFF?text=Baja");
                                createItem(snacks, "Samosa", "Veggie samosa", 25, true,
                                                "https://placehold.co/400x300/CD853F/FFFFFF?text=Samosa");

                                createItem(desserts, "Napolitaine", "Shortbread with pink icing", 50, true,
                                                "https://placehold.co/400x300/FFB6C1/000000?text=Napolitaine");
                                createItem(desserts, "Laddoo", "Sweet gram flour balls", 40, true,
                                                "https://placehold.co/400x300/FFD700/000000?text=Laddoo");

                        } else if (cuisine.contains("european")) {
                                createItem(breakfast, "Croissant", "Buttery pastry", 80, true,
                                                "https://placehold.co/400x300/D2691E/FFFFFF?text=Croissant");
                                createItem(breakfast, "English Breakfast", "Eggs, sausages, beans, toast", 250, false,
                                                "https://placehold.co/400x300/8B4513/FFFFFF?text=English+Breakfast");

                                createItem(lunch, "Smoked Marlin Salad", "Fresh salad with smoked marlin", 280, false,
                                                "https://placehold.co/400x300/228B22/FFFFFF?text=Marlin+Salad");
                                createItem(lunch, "Club Sandwich", "Triple decker sandwich with chicken", 220, false,
                                                "https://placehold.co/400x300/F4A460/000000?text=Club+Sandwich");

                                createItem(dinner, "Steak Frites", "Grilled steak with french fries", 450, false,
                                                "https://placehold.co/400x300/8B0000/FFFFFF?text=Steak+Frites");
                                createItem(dinner, "Lobster Thermidor", "Lobster with creamy sauce", 800, false,
                                                "https://placehold.co/400x300/DC143C/FFFFFF?text=Lobster");

                                createItem(snacks, "Quiche Lorraine", "Savory tart", 150, false,
                                                "https://placehold.co/400x300/FFD700/000000?text=Quiche");
                                createItem(snacks, "Mini Burgers", "Sliders", 180, false,
                                                "https://placehold.co/400x300/8B4513/FFFFFF?text=Mini+Burgers");

                                createItem(desserts, "Crème Brûlée", "Custard with caramelized sugar", 200, true,
                                                "https://placehold.co/400x300/FFFACD/000000?text=Creme+Brulee");
                                createItem(desserts, "Chocolate Fondant", "Molten chocolate cake", 220, true,
                                                "https://placehold.co/400x300/654321/FFFFFF?text=Chocolate+Fondant");
                        } else {
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
