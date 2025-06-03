db = db.getSiblingDB('citizensdb');

db.createUser({
    user: "admin",
    pwd: "admin123",
    roles: [{ role: "readWrite", db: "citizensdb" }]
});

db.createCollection('citizens');

db.citizens.createIndex({ name: 1 });
db.citizens.createIndex({ hasHumanPet: 1 });
db.citizens.createIndex({ roleNames: 1 });

db.citizens.createIndex({ "species.id": 1 });
db.citizens.createIndex({ "species.name": 1 });
db.citizens.createIndex({ "species.weight": 1 });
db.citizens.createIndex({ "species.height": 1 });

db.citizens.createIndex({ "species.name": 1, hasHumanPet: 1 });
db.citizens.createIndex({ roleNames: 1, hasHumanPet: 1 });
