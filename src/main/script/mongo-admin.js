use admin
db.createUser(
    {
        user: "admin",
        pwd: "123456",
        roles: [
            { role: "userAdminAnyDatabase", db: "admin" },
            { role: "readWriteAnyDatabase", db: "admin" }
        ]
    }
)

db.adminCommand({ shutdown: 1 })