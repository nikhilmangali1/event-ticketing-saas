-- add user_credentials table
CREATE TABLE user_credentials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL UNIQUE,

    password_hash VARCHAR(255) NOT NULL,

    last_login_at TIMESTAMP,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_credentials_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- move existing password_hash from users table to user_credentials
INSERT INTO user_credentials (
    user_id,
    password_hash
)
SELECT
    id,
    password_hash
FROM users;

-- remove column: password_hash from users
ALTER TABLE users
DROP COLUMN password_hash;

ALTER TABLE users
    ADD CONSTRAINT chk_user_role
    CHECK (role IN ('USER', 'ORGANIZER', 'ADMIN'));

--add updated_at column to users
ALTER TABLE users
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;


-- add new table to store refresh tokens
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL,

    refresh_token VARCHAR(512) NOT NULL UNIQUE,

    expires_at TIMESTAMP NOT NULL,

    revoked BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);