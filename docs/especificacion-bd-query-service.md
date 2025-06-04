## 🗂️ Formato de Documentos - Colección `citizens`

```json
{
  "_id": "string",
  "name": "string",
  "hasHumanPet": true,
  "species": {
    "id": "string",
    "name": "string",
    "weight": 0.0,
    "height": 0.0
  },
  "roleNames": ["string"]
}
```

---

## 🔍 Índices en la colección `citizens`

```js

db.citizens.createIndex({ name: 1 });
db.citizens.createIndex({ hasHumanPet: 1 });
db.citizens.createIndex({ roleNames: 1 });

db.citizens.createIndex({ "species.id": 1 });
db.citizens.createIndex({ "species.name": 1 });
db.citizens.createIndex({ "species.weight": 1 });
db.citizens.createIndex({ "species.height": 1 });

db.citizens.createIndex({ "species.name": 1, hasHumanPet: 1 });
db.citizens.createIndex({ roleNames: 1, hasHumanPet: 1 });
```
