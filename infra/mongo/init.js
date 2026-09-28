// Logical database-per-service seed data. Run once against MongoDB when bootstrapping local development.
db = db.getSiblingDB('ecommerce_product');
db.products.createIndex({sku: 1}, {unique: true});
db.products.insertOne({
    sku: 'SKU-DEMO-001',
    name: 'Demo Mechanical Keyboard',
    description: 'Demo product for local checkout',
    category: 'electronics',
    price: NumberDecimal('2499.00'),
    currency: 'INR',
    active: true,
    createdAt: new Date(),
    updatedAt: new Date()
});
