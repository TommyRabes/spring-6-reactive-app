db.createUser(
    {
        user: "Tommy",
        pwd: "123456",
        roles: [
            { role: "readWrite", db: "sfg" }
        ],
        passwordDigestor: "server"
    }
)
