db = db.getSiblingDB('citizensdb');

db.createUser({
    user: "admin",
    pwd: "admin123",
    roles: [{ role: "readWrite", db: "citizensdb" }]
});

db.createCollection('citizens');
