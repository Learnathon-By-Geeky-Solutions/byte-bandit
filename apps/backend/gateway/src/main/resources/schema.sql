CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
   id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   email         VARCHAR(255) UNIQUE NOT NULL,
   password_hash VARCHAR(72),
   name          VARCHAR(255),
   oauth_id      VARCHAR(255),
   verified      BOOLEAN NOT NULL DEFAULT FALSE,
   created_at    TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
   updated_at    TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
   CONSTRAINT users_email_unique UNIQUE (email)
);

CREATE TABLE tokens (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token_hash    VARCHAR(72) NOT NULL UNIQUE,
    is_used       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at    TIMESTAMP(6) NOT NULL,
    type          VARCHAR(255) NOT NULL CHECK (type IN ('EMAIL_VERIFICATION', 'PASSWORD_RESET', 'REFRESH')),
    user_id       UUID NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_tokens_user_id ON tokens(user_id);
CREATE INDEX idx_tokens_type ON tokens(type);

-- to insert both user and token in a single transaction
CREATE OR REPLACE FUNCTION create_user_and_token(
    p_email TEXT,
    p_password_hash TEXT,
    p_full_name TEXT,
    p_token_hash TEXT,
    p_token_type TEXT,
    p_token_expires_at TIMESTAMP
)
    RETURNS TABLE (
      id UUID,
      "fullName" TEXT,
      email TEXT,
      verified BOOLEAN,
      "createdAt" TIMESTAMP
    ) AS $$
DECLARE
    new_user_id UUID;
    new_name TEXT;
    new_email TEXT;
    new_verified BOOLEAN;
    new_created_at TIMESTAMP;
BEGIN
    INSERT INTO users (email, password_hash, name)
    VALUES (p_email, p_password_hash, p_full_name)
    RETURNING users.id, users.name, users.email, users.verified, users.created_at
        INTO new_user_id, new_name, new_email, new_verified, new_created_at;

    INSERT INTO tokens (user_id, token_hash, type, expires_at)
    VALUES (new_user_id, p_token_hash, p_token_type, p_token_expires_at);

    RETURN QUERY
        SELECT new_user_id AS id,
               new_name AS "fullName",
               new_email AS email,
               new_verified AS verified,
               new_created_at AS "createdAt";
END;
$$ LANGUAGE plpgsql;





