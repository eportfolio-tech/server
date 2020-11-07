db.auth('admin-user', 'admin-password')

db = db.getSiblingDB('eportfolio')

db.createUser({
    user: 'comp30022',
    pwd: 'renlord',
    roles: [
        {
            role: 'root',
            db: 'eportfolio',
        },
    ],
});